# Akari Test

Personal, unofficial fork of [konvar/Akari](https://github.com/konvar/Akari), based on **v0.8.2**.
It adds Trakt device sign-in for apps without a Client Secret and keeps a separate package so the
upstream launcher can stay installed. This app uses Trakt but is not endorsed or certified by Trakt.

**[Download the signed APK](https://github.com/gnicol/Akari/releases/latest)**

- App name: **Akari Test**
- Package: `com.konvar.tvhome.trakttest`
- Development/default branch: `trakt-test`
- Android 8 / API 26 or newer; no root required
- License: GPL-3.0, inherited from Akari. See [LICENSE](LICENSE).

## Changes from upstream

- Trakt device-code login and renewal use `auth.trakt.tv` and omit `client_secret`.
- A Client ID is enough to start sign-in. Failed or expired code dialogs show retry instructions.
- Next-episode lookups cover the **40 most recently watched eligible shows**, rather than checking
  a lifetime of shows before displaying Continue Watching. This does not modify Trakt history.
  Older shows may lack next-episode cards and completion checkmarks in this trial.
- A shared cooldown honors Trakt's `Retry-After` response instead of repeatedly retrying a 429.
- Initial favorites: SmartTube stable, Stremio, Same Old Table, Premiumize, TizenTube.
  Your apps is the initial focus; Stremio opens titles and SmartTube plays trailers.
- Separate app storage and signing. Original Akari, Projectivy and CleanYourTV are not replaced.
- Automatic upstream APK updates are disabled. Install this fork's releases manually.

## Install or update on another TV

Download `Akari-Test-<version>.apk` and `SHA256SUMS` from Releases. On macOS verify with
`shasum -a 256 -c SHA256SUMS` in the download directory; on Linux use `sha256sum -c SHA256SUMS`.

Enable ADB debugging and accept the TV's authorization prompt, then run:

```sh
adb connect TV_IP:5555
adb -s TV_IP:5555 install -r Akari-Test-0.8.2-trakt-test4.apk
adb -s TV_IP:5555 shell am start -n com.konvar.tvhome.trakttest/com.konvar.tvhome.MainActivity
```

`-r` preserves existing Akari Test data when the signing certificate matches. This package can coexist
with official Akari. Installation does **not** configure Home-button interception or replace the current
launcher. Open Akari Test manually to try it, then choose it in your existing Home-button helper if desired.
No accessibility or secure-settings grants are needed for the Trakt trial itself.

Every TV stores its own account connection. In Akari Test:

1. Open Settings > API keys > Trakt app Client ID. Scan the QR and paste your own Trakt app's Client ID.
   No Client Secret is needed. A new Trakt application can be registered at
   [Trakt Developer](https://developer.trakt.tv/). The device flow does not use a browser callback;
   this build retains `urn:ietf:wg:oauth:2.0:oob` in token renewal requests.
2. Open Settings > Tracking > Trakt, scan the approval QR, and authorize in your browser.
3. Confirm Connected / In use. Continue Watching and Upcoming load below your apps.

To undo the trial, restore your previous target in the Home-button helper if you changed it, then
optionally uninstall **Akari Test**. Uninstalling removes its settings and login, not the original launcher.

## Build and signing

Requirements: JDK 21, Android SDK platform 36 / build-tools 36.0.0, Python 3 for release verification.
Set `JAVA_HOME` and `ANDROID_HOME` for your machine, or put the SDK path in ignored `local.properties`.

```sh
git clone --branch trakt-test https://github.com/gnicol/Akari.git
cd Akari
./gradlew --no-daemon testDebugUnitTest assembleRelease
```

Without `signing.properties`, the release APK is **unsigned** and cannot be installed. GitHub Actions
builds this unsigned artifact and runs the tests. It has no signing key or account credentials.
Debug builds use the normal development key and are not distributed as releases.

Maintainer releases use a local `signing.properties` file containing `storeFile`, `storePassword`,
`keyAlias` and `keyPassword`, with a private keystore outside Git. Both are ignored. Preserve the original
trial's key: replacing it prevents updates to existing installations. The certificate subject is
Android Debug because the initial trial used that development key; continuity is intentional for this
personal test package. Do not treat it as a new production signing identity.

Expected signer SHA-256:

```text
f4c187acf197544630d714ae95bb9cf15cb0dfcb0b06cadebddccbb59a36913e
```

With signing configured, run `./tools/build-release.sh`. It runs the tests, builds, verifies package and
signer, and writes the signed APK plus `SHA256SUMS` to ignored `release-output/`. It refuses builds with
personal API credentials configured. The preserved signing key stays on the maintainer's Mac and is
not uploaded to GitHub or GitHub Actions. Keep a separate private backup of it.

## Bring in upstream updates

```sh
git remote add upstream https://github.com/konvar/Akari.git  # once, if absent
git fetch upstream --tags
git switch trakt-test
git merge <new-upstream-release-tag>
```

Resolve conflicts while preserving our application ID, signing policy, OAuth changes, tests and favorite
defaults. Increment `versionCode` and `versionName` in `app/build.gradle`; run tests and compare lint
against the same unmodified upstream version. Build with the preserved key and test on a spare TV.
Commit the resulting changes, push `trakt-test`, tag `akari-test-v<versionName>`, and publish the matching
signed APK and checksums on that tag. Never overwrite a published release with different bytes.

## Validation and limitations

Eight unit tests cover OAuth request payloads/hosts, the recent-history bound, and rate-limit cooldown.
The test3 build was verified on a Fire TV Stick 4K Max (2nd generation), Fire OS 8.1.8.5: Trakt login,
Continue Watching, Upcoming, and matching-episode handoff to Stremio worked. Home still returned to the
original launcher. Test4 packages the same behavior with explicit signing and build automation;
its APK is checked locally and in CI but has not been installed on another TV.

Full `lintRelease` currently reports 145 existing upstream errors. Comparison of the fork against
unmodified v0.8.2 found no additional errors. The only new warning is a newer version being available
for the test-only JSON library. Lint has not been disabled; it is not the CI gate until upstream's
baseline is addressed. Successful unit tests and release assembly are the CI gates.

Automatic token renewal has unit coverage but has not been exercised through a live expiry. Optional
TMDB artwork/features can differ because this fork does not bundle upstream's private TMDB API key.
The title catalog and fallback artwork work without it. Home interception and reboot behavior depend
on each TV's separately configured helper; installing this APK alone does not configure those.

For general launcher features, consult [upstream Akari](https://github.com/konvar/Akari).
