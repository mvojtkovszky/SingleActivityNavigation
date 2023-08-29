# CHANGELOG

## 3.3.0 (TBD)
* Fix `onBackPressed` deprecation, now using `OnBackPressedCallback`. Any Fragment extending `BaseSingleFragment` can now override `handleOnBackPressed` directly, `overridesBackPress` property has been removed.
* bump Gradle plugin to 8.1.1, Kotlin to 1.9.0 
* bump buildToolsVersion 34.0.0, targetSdkVersion, compileSdkVersion to 34
* bump core-ktx to 1.10.1, appcompat to 1.6.1, material to 1.9.0