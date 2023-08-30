# CHANGELOG

## 3.3.1 (TBD)
* Fix default onBackPressed() won't trigger after `handleOnBackPressed` is overridden and handled once. Callback is now always re-enabled afterwards.

## 3.3.0 (2023-08-29)
* Fix `onBackPressed` deprecation, now using `OnBackPressedCallback`.
* Remove `handleOnBackPressed` and `onBackPressed()` in favour of `handleOnBackPressed()`.
* bump Gradle plugin to 8.1.1, Kotlin to 1.9.0 
* bump buildToolsVersion 34.0.0, targetSdkVersion, compileSdkVersion to 34
* bump core-ktx to 1.10.1, appcompat to 1.6.1, material to 1.9.0