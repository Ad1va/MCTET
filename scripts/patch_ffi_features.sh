#!/usr/bin/env bash
set -Eeuo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 /path/to/easytier-ffi/Cargo.toml" >&2
  exit 1
fi

MANIFEST="$1"
if [[ ! -f "$MANIFEST" ]]; then
  echo "ERROR: Not found: $MANIFEST" >&2
  exit 1
fi

TMP="$(mktemp)"
python3 - "$MANIFEST" > "$TMP" <<'PY'
import re
import sys
from pathlib import Path

path = Path(sys.argv[1])
text = path.read_text(encoding='utf-8')

required = {
    'default': '["c-abi", "ffi-dataplane"]',
    'c-abi': '[]',
    'ffi-dataplane': '[]',
}

features_match = re.search(r'(?ms)^\[features\]\n(?P<body>.*?)(?=^\[|\Z)', text)
if not features_match:
    if not text.endswith('\n'):
        text += '\n'
    text += '\n[features]\n'
    for key, value in required.items():
        text += f'{key} = {value}\n'
else:
    body = features_match.group('body')
    for key, value in required.items():
        pattern = rf'(?m)^{re.escape(key)}\s*=.*$'
        replacement = f'{key} = {value}'
        if re.search(pattern, body):
            body = re.sub(pattern, replacement, body)
        else:
            if body and not body.endswith('\n'):
                body += '\n'
            body += replacement + '\n'
    text = text[:features_match.start('body')] + body + text[features_match.end('body'):]

path.write_text(text, encoding='utf-8')
print(text, end='')
PY
mv "$TMP" "$MANIFEST"
echo ">> Ensured easytier-ffi features in: $MANIFEST"
