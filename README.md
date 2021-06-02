# SingleActivityNavigation
A single activity application framework with straightforward navigation controls, allowing seamless
movement between fragments through different containers using a single line of code.
<br/><br/>
<img src="example.gif" alt="Example Flow" width="320"/>

## How does it work?
Your (single) activity must extend from BaseSingleActivity and all your fragments extend from BaseSingleFragment.
Then you can simply move between fragments from either activity or fragment.

``` kotlin
// take any fragment(s) of your choosing
val myFragment = MyFragment()

// multiple ways to navigate through back stack
// notice how we can use same fragment and seamlessly put it into any container
navigateBack()
navigateBackTo("fragment name")
navigateBackToRoot()
navigateTo(myFragment)
navigateToRoot(myFragment)
navigateToBottomSheed(myFragment)
navigateToDialog(myFragment)
dismissOpenBottomSheet()
dismissOpenDialog()
```

<br/>Make use of many convenience methods to help you control the state of your app
``` kotlin
getCurrentFragment()?.let {
    when (it.fragmentType) {
        // this fragment is root, came into existence with navigateToRoot() call
        FragmentType.ROOT -> TODO()
        // this fragment is default, came into existence with navigateTo() call, no special flags
        FragmentType.DEFAULT -> TODO()
        // this fragment is also default, but was flagged as modal during navigateTo() call
        FragmentType.MODAL -> TODO()
        // this fragment is a dialog, came into existence with navigateToDialog() call
        FragmentType.DIALOG -> TODO()
        // this fragment is a dialog, came into existence with navigateToBottomSheet() call
        FragmentType.BOTTOM_SHEET -> TODO()
    }
}
```

<br/>Let the fragment define behavioural patterns of it's own by overriding open properties:
``` kotlin
open val isModal: Boolean
open val mustBeValidToInvokeNavigation: Boolean
open val overridesBackPress: Boolean
open val animationEnter: Int
open val animationExit: Int
open val animationPopEnter: Int
open val animationPopExit: Int
```

<br/>And much more:
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
