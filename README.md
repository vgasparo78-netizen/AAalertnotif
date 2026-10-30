# AAIdrive Alert Relay

A tiny Android app that makes Radarbot and Waze alerts show up on a BMW screen through AAIdrive.
It listens for their notifications and re-posts each change as a normal, dismissable,
high-importance notification, which AAIdrive already shows and pops up in the car.
It needs no changes to AAIdrive.

## Build the APK
**Without Android Studio:** put this folder in a new GitHub repository and push it. The
"Build APK" workflow builds `app-debug.apk` (Actions tab, download the artifact).

**With Android Studio:** open this folder, then Build > Build APK(s).
The result is `app/build/outputs/apk/debug/app-debug.apk`.

## Use it
1. Install the APK and open it.
2. Tap "1. Allow notification access" and enable AAIdrive Alert Relay.
3. Tap "2. Allow posting notifications" (Android 13 and newer).
4. In AAIdrive, make sure Notifications is enabled with popups on.

## Change the apps
Edit `SOURCES` in `AlertRelayService.kt`. Keys are package name prefixes.
Radarbot Free is `com.vialsoft.radarbot_free`; if the paid version uses another package, add it.
