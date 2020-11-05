# SingleActivityNavigation
A single activity application framework with straightforward navigation controlls, allowing free
movement between fragments and different containers using a single line of code.
<br/><br/>
<img src="example.gif" alt="Example Flow" width="320"/>

## How does it work?
Your (single) activity must extend from BaseSingleActivity and all your fragments extend from BaseSingleFragment.
Then you can simply move between fragments from either activity or fragment.

``` kotlin
// take any fragment(s) of your choosing
val myFragment = MyFragment()

// simply navigate to it
navigateTo(myFragment)

// ... or open it in bottom sheet
openBottomSheet(myFragment)

// ... or in a dialog
openDialog(myFragment)

// multiple ways to navigate through back stack
navigateBack()
navigateBackToRoot()
navigateBackTo("fragment name")
closeCurrentlyOpenBottomSheet()
closeCurrentlyOpenDialog()
```

<br/>Make use of many convenience methods to help you control the state of your app
``` kotlin
getCurrentFragment()?.let {
    when (it.fragmentType) {
        FragmentType.ROOT -> TODO()
        FragmentType.DEFAULT -> TODO()
        FragmentType.MODAL -> TODO()
        FragmentType.DIALOG -> TODO()
        FragmentType.BOTTOM_SHEET -> TODO()
    }
}
```

<br/>Let the fragment define behavioural patterns of it's own by overriding open properties:
``` kotlin
open val isModal: Boolean
open val overridesBackPress: Boolean
open val animationEnter: Int
open val animationExit: Int
open val animationPopEnter: Int
open val animationPopExit: Int
```

<br/>And some more:
* Master/detail implementation support.
* Safe instance state recreation.
* Custom transition animations based on fragment type.
* Make sure to check out example app to help you get started.
<br/>

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

And SingleActivityNavigation dependency is added to app build.gradle
``` gradle
dependencies {
    implementation 'com.github.mvojtkovszky:SingleActivityNavigation:$latest_version'
}
```
