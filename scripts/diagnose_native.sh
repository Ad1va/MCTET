#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.."; pwd)"
REPORT_DIR="$ROOT_DIR/artifacts"
REPORT_FILE="$REPORT_DIR/easytier-native-diagnostics.txt"
DIST_DIR="$ROOT_DIR/dist"
REPO_DIR="$ROOT_DIR/third_party/easytier"
JNI_LIB_DIR="$ROOT_DIR/android/library/src/main/jniLibs"

mkdir -p "$REPORT_DIR"
: > "$REPORT_FILE"

log() {
  echo "$*" | tee -a "$REPORT_FILE"
}

section() {
  log ""
  log "============================================================"
  log "$*"
  log "============================================================"
}

run_capture() {
  log ""
  log "> $*"
  set +e
  "$@" >> "$REPORT_FILE" 2>&1
  code=$?
  set -e
  log "[exit=$code]"
}

section "Environment"
log "UTC: $(date -u '+%Y-%m-%dT%H:%M:%SZ')"
log "ROOT_DIR=$ROOT_DIR"
log "ET_REF=${ET_REF:-}"
log "REPO_URL=${REPO_URL:-}"
run_capture uname -a
run_capture rustc --version
run_capture cargo --version
run_capture rustup target list --installed

section "EasyTier Source State"
if [[ -d "$REPO_DIR/.git" ]]; then
  run_capture git -C "$REPO_DIR" rev-parse HEAD
  run_capture git -C "$REPO_DIR" status --short
else
  log "EasyTier repo not found at $REPO_DIR"
fi

section "Cargo Manifests"
for manifest in \
  "$REPO_DIR/easytier-contrib/easytier-android-jni/Cargo.toml" \
  "$REPO_DIR/easytier-contrib/easytier-ffi/Cargo.toml"; do
  if [[ -f "$manifest" ]]; then
    log ""
    log "--- $manifest ---"
    sed -n '1,140p' "$manifest" >> "$REPORT_FILE" 2>&1 || true
  else
    log "Missing manifest: $manifest"
  fi
done

section "Search collect_network_infos In Source"
if [[ -d "$REPO_DIR" ]]; then
  run_capture grep -RIn --exclude-dir=target --exclude-dir=.git "collect_network_infos" "$REPO_DIR"
else
  log "EasyTier repo unavailable"
fi

section "Built Native Libraries"
run_capture find "$DIST_DIR" "$JNI_LIB_DIR" -type f -name '*.so' -print -exec ls -lh {} \;

section "ELF Diagnostics"
READELF=""
for candidate in \
  "${ANDROID_NDK_HOME:-}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-readelf" \
  "${ANDROID_NDK_ROOT:-}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-readelf" \
  "$(command -v llvm-readelf || true)" \
  "$(command -v readelf || true)"; do
  if [[ -n "$candidate" && -x "$candidate" ]]; then
    READELF="$candidate"
    break
  fi
done
log "READELF=$READELF"

while IFS= read -r so; do
  log ""
  log "--- ELF: $so ---"
  run_capture file "$so"
  if [[ -n "$READELF" ]]; then
    log ""
    log "NEEDED entries:"
    "$READELF" -d "$so" 2>&1 | grep -E 'NEEDED|SONAME|RPATH|RUNPATH' >> "$REPORT_FILE" || true
    log ""
    log "collect_network_infos symbols:"
    "$READELF" -Ws "$so" 2>&1 | grep -E 'collect_network_infos|UND' | grep -E 'collect_network_infos|UND' >> "$REPORT_FILE" || true
  else
    log "No readelf available"
  fi
done < <(find "$DIST_DIR" "$JNI_LIB_DIR" -type f -name '*.so' 2>/dev/null | sort -u)

section "AAR Contents"
if [[ -d "$ROOT_DIR/android/library/build/outputs" ]]; then
  while IFS= read -r aar; do
    log ""
    log "--- AAR: $aar ---"
    run_capture ls -lh "$aar"
    run_capture unzip -l "$aar"
  done < <(find "$ROOT_DIR/android/library/build/outputs" -type f -name '*.aar' | sort)
else
  log "No AAR output directory yet"
fi

section "Conclusion Hints"
log "If collect_network_infos appears as UND in libeasytier_android_jni.so, the JNI library still has an unresolved external symbol."
log "If another .so exports collect_network_infos but is not present in the AAR/APK, it must be packaged and loaded before libeasytier_android_jni.so."
log "If no library exports collect_network_infos, the EasyTier source/build needs a code-level patch or link argument correction."

log ""
log "Report written to: $REPORT_FILE"
