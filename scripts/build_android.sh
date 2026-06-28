#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.."; pwd)"
DIST_DIR="$ROOT_DIR/dist"
REPO_DIR="$ROOT_DIR/third_party/easytier"

REF="${ET_REF:-${1:-}}"
ABIS="${ABIS:-arm64-v8a,armeabi-v7a,x86,x86_64}"
CARGO_PROFILE="${CARGO_PROFILE:-release}"
PROFILE_FLAG="--release"
if [[ "$CARGO_PROFILE" != "release" ]]; then
  PROFILE_FLAG="--profile ${CARGO_PROFILE}"
fi

command -v rustup >/dev/null || { echo "ERROR: rustup not found"; exit 1; }
command -v cargo  >/dev/null || { echo "ERROR: cargo not found";  exit 1; }

if ! command -v cargo-ndk >/dev/null; then
  echo ">> Installing cargo-ndk ..."
  cargo install cargo-ndk
fi

if [[ -z "${ANDROID_NDK_HOME:-}" && -z "${ANDROID_NDK_ROOT:-}" ]]; then
  if [[ -n "${ANDROID_NDK:-}" ]]; then
    export ANDROID_NDK_HOME="$ANDROID_NDK"
  fi
fi
if [[ -z "${ANDROID_NDK_HOME:-}" ]]; then
  if [[ -n "${ANDROID_HOME:-}" && -d "$ANDROID_HOME/ndk" ]]; then
    CANDIDATE="$(ls -1d "$ANDROID_HOME/ndk"/* 2>/dev/null | sort -V | tail -n1 || true)"
    if [[ -n "$CANDIDATE" ]]; then
      export ANDROID_NDK_HOME="$CANDIDATE"
    fi
  fi
fi
if [[ -z "${ANDROID_NDK_HOME:-}" ]]; then
  echo "ERROR: ANDROID_NDK_HOME not set and cannot auto-detect from ANDROID_HOME/ndk/*" >&2
  exit 1
else
  echo ">> ANDROID_NDK_HOME = $ANDROID_NDK_HOME"
fi

if [[ ! -d "$REPO_DIR/.git" ]]; then
  echo ">> Upstream not found, fetching..."
  "$ROOT_DIR/scripts/fetch_easytier.sh" ${REF:+--ref "$REF"}
elif [[ -n "$REF" ]]; then
  echo ">> Switching upstream to ref: $REF"
  git -C "$REPO_DIR" fetch --tags --recurse-submodules origin
  git -C "$REPO_DIR" checkout --recurse-submodules "$REF"
fi

pushd "$REPO_DIR" >/dev/null
rustup show active-toolchain
rustup target add \
  aarch64-linux-android \
  armv7-linux-androideabi \
  i686-linux-android \
  x86_64-linux-android
rustup target list --installed
popd >/dev/null

JNI_MANIFEST="${JNI_MANIFEST:-}"
if [[ -n "$JNI_MANIFEST" && ! -f "$JNI_MANIFEST" ]]; then
  echo ">> WARNING: JNI_MANIFEST is set but not found: $JNI_MANIFEST"
  echo ">> Falling back to auto-detection"
  JNI_MANIFEST=""
fi

if [[ -z "$JNI_MANIFEST" ]]; then
  JNI_MANIFEST="$(find "$REPO_DIR" -maxdepth 6 -type f -name Cargo.toml -print0 2>/dev/null \
    | xargs -0 -r grep -IlE '^[[:space:]]*name[[:space:]]*=[[:space:]]*"easytier-android-jni"' \
    | head -n1 || true)"
fi

if [[ -z "$JNI_MANIFEST" && -f "$REPO_DIR/easytier-contrib/easytier-android-jni/Cargo.toml" ]]; then
  JNI_MANIFEST="$REPO_DIR/easytier-contrib/easytier-android-jni/Cargo.toml"
fi

if [[ -z "$JNI_MANIFEST" ]]; then
  echo "ERROR: Cannot locate Cargo.toml for easytier-android-jni crate." >&2
  find "$REPO_DIR" -maxdepth 4 -type f -name Cargo.toml -print >&2 || true
  exit 1
fi

if [[ "${JNI_MANIFEST:0:1}" != "/" ]]; then
  JNI_MANIFEST="$ROOT_DIR/${JNI_MANIFEST#./}"
fi
if [[ ! -f "$JNI_MANIFEST" ]]; then
  echo "ERROR: JNI_MANIFEST resolved to '$JNI_MANIFEST' but not found" >&2
  exit 1
fi

echo ">> Using JNI MANIFEST: $JNI_MANIFEST"

FFI_MANIFEST="$REPO_DIR/easytier-contrib/easytier-ffi/Cargo.toml"
if [[ -f "$FFI_MANIFEST" ]]; then
  echo ">> Patching easytier-ffi crate-type to rlib only..."
  sed -i.bak 's/crate-type = \["cdylib", "rlib"\]/crate-type = ["rlib"]/' "$FFI_MANIFEST" || true
  sed -i.bak 's/crate-type = \[.*cdylib.*\]/crate-type = ["rlib"]/' "$FFI_MANIFEST" || true
  echo ">> Patched: $FFI_MANIFEST"
else
  echo ">> WARNING: easytier-ffi/Cargo.toml not found, skipping patch"
fi

mkdir -p "$DIST_DIR"
pushd "$REPO_DIR" >/dev/null

echo ">> Building libeasytier_android_jni.so for ABIs: $ABIS"
cargo ndk -o "$DIST_DIR" -t "$ABIS" -- build $PROFILE_FLAG --manifest-path "$JNI_MANIFEST"

popd >/dev/null

echo ">> Build complete. Outputs:"
find "$DIST_DIR" -maxdepth 2 -name 'libeasytier_android_jni.so' -print
