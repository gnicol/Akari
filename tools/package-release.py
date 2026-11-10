#!/usr/bin/env python3
"""Verify a locally signed APK and prepare release assets; never uploads or installs."""
import hashlib
import os
from pathlib import Path
import re
import shutil
import subprocess

ROOT = Path(__file__).resolve().parents[1]
APK = ROOT / "app/build/outputs/apk/release/app-release.apk"
SDK = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
if not SDK:
    raise SystemExit("Set ANDROID_HOME to the Android SDK before packaging a release.")
TOOLS = Path(SDK) / "build-tools/36.0.0"
EXPECTED_SIGNER = "f4c187acf197544630d714ae95bb9cf15cb0dfcb0b06cadebddccbb59a36913e"

badging = subprocess.check_output([str(TOOLS / "aapt"), "dump", "badging", str(APK)], text=True)
package = re.search(r"^package: name='([^']+)' versionCode='(\d+)' versionName='([^']+)'", badging)
if not package or package[1] != "com.konvar.tvhome.trakttest":
    raise SystemExit("Unexpected APK package; refusing to package release.")
version = package[3]
if not re.fullmatch(r"[A-Za-z0-9._-]+", version):
    raise SystemExit("Unexpected APK version format.")
certificates = subprocess.check_output(
    [str(TOOLS / "apksigner"), "verify", "--print-certs", str(APK)], text=True
)
signers = re.findall(r"Signer #\d+ certificate SHA-256 digest: ([0-9a-f]+)", certificates)
if signers != [EXPECTED_SIGNER]:
    raise SystemExit("Signing certificate does not match the existing Akari Test installation.")

out = ROOT / "release-output"
out.mkdir(exist_ok=True)
target = out / f"Akari-Test-{version}.apk"
digest = hashlib.sha256(APK.read_bytes()).hexdigest()
if target.exists() and hashlib.sha256(target.read_bytes()).hexdigest() != digest:
    raise SystemExit("Different APK already exists for this version; increment the version first.")
shutil.copy2(APK, target)
(out / "SHA256SUMS").write_text(f"{digest}  {target.name}\n")
print(f"Verified package {package[1]}, version {version} ({package[2]}), matching signer.")
print(f"Release APK: {target}")
print(f"SHA256: {digest}")
