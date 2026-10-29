# Permissions Lab (Practice 8)

## Part A. Theory Answers

### Question 1
1. Decide if a permission is needed.
2. Choose the correct category (install-time, runtime, special).
3. Declare in AndroidManifest.xml.
4. Check and request at runtime if necessary.
Example: Local file operations within app-specific storage do not need permissions because the app has isolated sandboxed storage.

### Question 2
- Install-time permissions: Granted by the system at installation.
- Runtime permissions: Granted by the user via a dialog while the app is running.
- Special permissions: Granted by the user on a dedicated system Settings screen.
The category determines whether declarations alone suffice or if dynamic check and request logic must be implemented.

### Question 3
Normal permissions are low-risk and automatically granted at install time without user interaction.
If the manifest declaration is forgotten, the app throws a `SecurityException` when attempting the protected operation.
Examples: `INTERNET`, `ACCESS_NETWORK_STATE`, `WAKE_LOCK`.

### Question 4
Signature permissions are granted only to apps signed with the same signing certificate as the declaring app.
ProtectionLevel: `signature`.
Wallet (same signature or permitted) gets access, stranger app is denied. No dialog is shown to the user.

### Question 5
Install-time permissions are shown to the user during installation in the Play Store/package installer and granted automatically upon install.
Two kinds: normal permissions and signature permissions.

### Question 6
Runtime permissions were introduced in Android 6.0 (API 23).
On API 22, runtime-type permissions are granted at install time because the API level predates the runtime permission model.
The model evaluates `granted(kind, sdk)` based on whether the device API supports runtime permission enforcement.

### Question 7
States: Not asked yet → Denied once → Denied twice (permanently denied).
In state 1: shouldShowRationale is false.
In state 2: shouldShowRationale is true.
In state 3: shouldShowRationale is false (or permanently denied), and the app should send the user to app settings.

### Question 8
`rememberPermissionState` manages runtime permission state in Compose.
Branches: `isGranted`, `shouldShowRationale`, and the initial/unrequested state.
State survives recomposition via Compose state holders (`remember`).
`ExperimentalPermissionsApi` marks APIs that are still subject to change in Accompanist/Permissions libraries.

### Question 9
`rememberLauncherForActivityResult` with `ActivityResultContracts.RequestPermission()` registers a callback for permission results.
`ContextCompat.checkSelfPermission` reads the current state, and `ActivityCompat.shouldShowRequestPermissionRationale` determines if rationale should be shown.
This approach avoids external dependencies like Accompanist.

### Question 10
`RequestMultiplePermissions` returns `Map<String, Boolean>` mapping permission strings to boolean results.
The system combines fine and coarse location into a single dialog to simplify user experience.
If both are denied, the app should gracefully disable location features or guide the user to settings.

### Question 11
Special permissions require the user to navigate to system settings rather than showing a standard runtime dialog.
- SYSTEM_ALERT_WINDOW: `Settings.canDrawOverlays`
- WRITE_SETTINGS: `Settings.System.canWrite`
- MANAGE_EXTERNAL_STORAGE: `Environment.isExternalStorageManager`
- REQUEST_INSTALL_PACKAGES: `PackageManager.canRequestPackageInstalls`
- ACCESS_BACKGROUND_LOCATION: Handled via runtime check after fine location is granted.

### Question 12
The pattern: check state → open Settings via Intent → check state again upon returning.
No callback is received because returning from system Settings does not invoke an Activity Result callback for special permissions.
Solution: Re-check the permission state in `ON_RESUME` using lifecycle observers (`LifecycleEventEffect` or `DisposableEffect`).

### Question 13
Step 1: Request `ACCESS_FINE_LOCATION` (and/or `ACCESS_COARSE_LOCATION`).
Step 2: After foreground location is granted, request `ACCESS_BACKGROUND_LOCATION` (wrapped in `SDK_INT >= Build.VERSION_CODES.Q`) because background location cannot be requested simultaneously with foreground location on Android 10+.

### Question 14
A permission group clusters related permissions (e.g., Camera, Location, Contacts) to minimize the number of dialogs shown to the user.
Group examples: Location, Contacts, Camera.
Code should never rely on group membership because group structures can change across Android versions.

### Question 15
If `<uses-permission>` is missing for a runtime permission, the system denies access or throws an exception when invoked.
Install-time permissions are declared in the manifest; runtime/special permissions require manifest declaration plus dynamic request.
`android:maxSdkVersion` is used when a permission is no longer needed on newer Android versions (e.g. `WRITE_EXTERNAL_STORAGE` on API 29+).

### Question 16
`android:required="true"` filters the app from devices lacking the hardware in the store. `android:required="false"` allows installation on all devices.
With `required="false"`, the app must check `PackageManager.hasSystemFeature`.
The `visibleOn` model determines app visibility in stores based on hardware availability and feature requirement flags.

### Question 17
One-time permissions (API 30+) are temporary and revoked when the app goes to the background or closes.
Never cache granted state forever because temporary permissions expire.
Fix: Re-check permission state on `ON_RESUME`.

### Question 18
(1) Auto-reset (Android 11+) revokes permissions if an app is unused for several months. Apps should re-verify permissions on launch. Users can disable auto-reset in app settings (`ACTION_AUTO_REVOKE_PERMISSIONS`).
(2) `revokeSelfPermissionOnKill` on API 33 allows apps to relinquish runtime permissions they no longer need, protecting user privacy.

### Question 19
- API 23: Introduced runtime permissions.
- API 30: Introduced one-time permissions and auto-reset.
- API 33: Introduced `POST_NOTIFICATIONS` runtime permission and runtime permission revocation (`revokeSelfPermissionOnKill`).
`POST_NOTIFICATIONS` became a runtime permission in Android 13; requesting it on versions below API 33 throws or is unnecessary since notifications were granted by default.

---

## Part B. Hands-on Tasks Summary

- Task 1: Implemented `PermissionModel` and verified denial states via unit test `PermissionModelTest`.
- Task 2: Declared `INTERNET` and `ACCESS_NETWORK_STATE` and monitored network status with `ConnectivityManager`.
- Task 3: Implemented Camera screen handling three permission states, rationale, and settings redirection after two denials.
- Task 4: Implemented overlay permission (`SYSTEM_ALERT_WINDOW`) with Switch and `ON_RESUME` state synchronization.
- Task 5: Implemented one-time location request (`ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`) with `ON_RESUME` re-checking.
- Task 6: Implemented `POST_NOTIFICATIONS` runtime permission on API 33+ with test notification delivery.

### Tested API Levels
- Tested on API 24 (Android 7.0), API 30 (Android 11), and API 34 (Android 14).
- Task 5 observation: When choosing "Only this time", minimizing the app and returning resets or revokes the temporary permission state upon resume.
