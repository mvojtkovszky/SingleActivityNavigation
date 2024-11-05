# CHANGELOG

## 3.4.2 (TBD)
* 

## 3.4.1 (2024-11-05)
* bump Kotlin to 2.0.20, Gradle plugin to 8.7.2
* bump core-ktx to 1.15.0, appcompat to 1.7.0, material to 1.12.0

## 3.4.0 (2023-10-27)
* Calling `navigateBack` will take stack into consideration instead of calling default back press.
* Calling `dismissOpenBottomSheet` or `dismissOpenDialog` will now return `Boolean` indicating whether open sheet or dialog was dismissed or not.
* Fix internally call `onBackPressedDispatcher.onBackPressed()` instead of deprecated `onBackPressed()`
* bump Kotlin to 1.9.10, Gradle plugin to 8.1.2
* bump core-ktx to 1.12.0, material to 1.10.0

## 3.3.1 (2023-08-30)
* Fix default `onBackPressed()` won't trigger after `handleOnBackPressed` is overridden and handled once. Callback is now always re-enabled afterwards.

## 3.3.0 (2023-08-29)
* Fix `onBackPressed` deprecation, now using `OnBackPressedCallback`.
* Remove `handleOnBackPressed` and `onBackPressed()` in favour of `handleOnBackPressed()`.
* bump Gradle plugin to 8.1.1, Kotlin to 1.9.0 
* bump buildToolsVersion 34.0.0, targetSdkVersion, compileSdkVersion to 34
* bump core-ktx to 1.10.1, appcompat to 1.6.1, material to 1.9.0