#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT"
if [ ! -f signing.properties ]; then
    echo "Missing private signing.properties; refusing to publish an unsigned or differently signed APK." >&2
    exit 1
fi
python3 - <<'PYTHON'
from pathlib import Path
import os, re
names = ('TRAKT_CLIENT_ID', 'TRAKT_CLIENT_SECRET', 'SIMKL_CLIENT_ID', 'TMDB_API_KEY',
         'NUVIO_SUPABASE_URL', 'NUVIO_SUPABASE_ANON_KEY')
props = Path('local.properties').read_text() if Path('local.properties').exists() else ''
for name in names:
    if os.environ.get(name) or re.search(r'^\s*'+name+r'\s*[:=]\s*\S', props, re.M):
        raise SystemExit('Refusing distributable APK with configured credential: ' + name)
PYTHON
./gradlew --no-daemon testDebugUnitTest assembleRelease
python3 tools/package-release.py
