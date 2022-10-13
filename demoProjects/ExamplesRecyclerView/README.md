## MotionLayout RecyclerView Sample
Sample project that demonstrates the use of MotionLayout in RecycleView.

## Overview
-----------------------------

### This show use of MotionLayout as an item in RecyclerView

* [MainActivity.java](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/java/com/example/motionrecycle/MainActivity.java)
- the recycler view code
* [res/layout/motion_item.xml](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/res/layout/motion_item.xml)
- the layout used as an item containing the MotionLayout
* [res/xml/motion_item_scene.xml](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/res/xml/motion_item_scene.xml)
 - the motionScene file 

https://user-images.githubusercontent.com/20599348/194928671-7ba50aec-c7a7-45cf-80b0-3656601301a7.mov

### MotionLayout in Recycler view showing how to cache the states of active motionLayouts

Implementation may vary but typically you will need to cache and reflect some parts of the state of a MotionLayout before they atach to other views.
You may also need to update the state of MotionLayout as infomation in the backend changes.

This is a simple implementation of a series of timers.

* [MotionRecycler2.java](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/java/com/example/motionrecycle/MotionRecycler2.java) Contains all the recycler view code
* [res/layout/timer_item.xml](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/res/layout/timer_item.xml)
is the layout used as an item containing the MotionLayout
* [res/xml/timer_item_scene.xml](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/res/xml/timer_item_scene.xml)
the motionScene file
https://user-images.githubusercontent.com/20599348/195418010-512859a1-8503-48e5-8631-671b5ce7fd64.mov



## Contributing

If you'd like to get involved and contribute please read [CONTRIBUTING](https://github.com/androidx/constraintlayout/blob/main/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

- **John Hoford** ([jafu888](https://github.com/jafu888))

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](https://github.com/androidx/constraintlayout/blob/main/LICENSE) file for details
