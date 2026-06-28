#!/usr/bin/env bash
set -Eeuo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 /path/to/Cargo.toml [cdylib|rlib|staticlib]" >&2
  exit 1
fi

MANIFEST="$1"
MODE="${2:-cdylib}"

if [[ ! -f "$MANIFEST" ]]; then
  echo "ERROR: Not found: $MANIFEST" >&2
  exit 1
fi

case "$MODE" in
  cdylib)
    CRATE_TYPE='["cdylib", "rlib"]'
    ;;
  rlib)
    CRATE_TYPE='["rlib"]'
    ;;
  staticlib)
    CRATE_TYPE='["staticlib"]'
    ;;
  *)
    echo "ERROR: Unsupported crate-type mode: $MODE" >&2
    exit 1
    ;;
esac

TMP="$(mktemp)"
perl -0777 -pe '
  my $crate_type = $ENV{"CRATE_TYPE"};
  if (/crate-type\s*=/s) {
    s/crate-type\s*=\s*\[[^\]]*\]/crate-type = $crate_type/s;
  } else {
    s/(\[lib\][^\[]*)/$1\ncrate-type = $crate_type\n/s;
  }
' "$MANIFEST" > "$TMP"
mv "$TMP" "$MANIFEST"
echo ">> Patched crate-type to $CRATE_TYPE: $MANIFEST"
