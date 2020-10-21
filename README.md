# SingleActivityNavigation
A simple single activity application framework with straightforward navigation controlls, allowing
to reuse fragments and move between them as root views, dialogs, bottom sheets, modals etc. with minimal dependencies.

## How does it work?
Your single activity must extend from BaseSingleActivity and all your fragments extend from BaseSingleFragment.
Then you can simply move between your fragments by using public methods in your activity or fragment.

<br/>
<img src="drawing.jpg" alt="drawing" width="360"/>
<br/>

``` kotlin
// take any fragment of your choosing
val myFragment = MyFragment()

// simply navigate to it
navigateTo(myFragment)

// open your fragment in bottom sheet
openBottomSheet(myFragment)

// ... or in a dialog
openDialog(myFragment)

// you can simply navigate through the back stack using:
navigateBack()
navigateBackToRoot()
navigateBackToFragment("fragmentName")

// define your root fragment(s). Those will be held at the bottom of stack, intended as the initial activity's fragment
override fun getNewRootFragmentInstance(positionIndex: Int): BaseSingleFragment? {
    return MyRootFragment()
}

// you can fine-tune animation behaviour for fragment transitions by accessing customAnimationSettings
customAnimationSettings.setCustomAnimationsRoot(...)
customAnimationSettings.setCustomAnimationsModal(...)
customAnimationSettings.setCustomAnimationsDefault(...)
```

Refer to example application for detailed implementation overview.


## Nice! How do I get started?
Make sure root build.gradle repositories include JitPack
``` gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

And BillinSingleActivityNavigationHelper dependency is added to app build.gradle
``` gradle
dependencies {
    implementation 'com.github.mvojtkovszky:SingleActivityNavigation:$latest_version'
}
```
