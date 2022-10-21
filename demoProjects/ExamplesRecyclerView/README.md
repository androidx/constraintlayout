## MotionLayout RecyclerView Sample
Sample project that demonstrates the use of MotionLayout in RecycleView.

## Overview

Using ConstraintLayout or MotionLayout in Recycler view can be trickey. 
The basic pattern of useage is 
1. Create an base layout (say res/layout/activity_main.xml) that contains <RecyclerView..>
2. Create a layout for one item (say res/layout/motion_item.xml)
1. create a subclass of RecyclerView.Adapter say ```static class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder>```
1. create a subclass of RecyclerView.ViewHolder say ```static class CustomViewHolder extends RecyclerView.ViewHolder```
1. find the RecyclerView. ```rv = findViewById(R.id.recyclerView)```
1. set your custom ViewHolder to it ```rv.setAdapter(new CustomAdapter(...));```
1. set a Layout Manager on the RecyclerView ```rv.setLayoutManager(new LinearLayoutManager(this));```

-----------------------------
### ConstraintLayout in Recycler View

* [CalendarRecycler.java](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/java/com/example/motionrecycle/CalendarRecycler.java)
 the recycler view code
* [res/layout/calendar.xml](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/res/layout/calendar.xml)
 the layout used as an item containing the ConstraintLayout
* [res/layout/calendar_entries.xml](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/res/layout/calendar_entries.xml)
 the TextViews included in the ConstraintLayout
* [res/xml/motion_item_scene.xml]()
 the motionScene file 

![recycler_vew_cl](https://user-images.githubusercontent.com/15019413/197081542-de4947e7-fa14-4c95-8b8d-9e46766230e0.gif)


### This show use of MotionLayout as an item in RecyclerView

* [MainActivity.java](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/java/com/example/motionrecycle/CalendarRecycler2.java)
 the recycler view code
* [res/layout/calendar_motion.xml](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/res/layout/calendar_motion.xml)
 the layout used as an item containing the MotionLayout
* [res/xml/calendar_motion_scene.xml](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/res/xml/calendar_motion_scene.xml)
 the motionScene file 

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

### Scroll locking during transition

Demo of locking the RecyclerView during swipe

* [java code](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/java/com/example/motionrecycle/MotionRecycler1.java)
* [layout](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/res/layout/lock_recycler_item.xml) 
* [motion scene](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesRecyclerView/app/src/main/res/xml/lock_recycler_item_scene.xml)

![scrollLock](https://user-images.githubusercontent.com/15019413/196498539-b17683ac-84b7-495a-9242-103ede4440ec.gif)

#important code snipit
```java
        TransitionAdapter adapter = new TransitionAdapter() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                mRecyclerView.suppressLayout(true); // the swipe has begun 
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                mRecyclerView.suppressLayout(false); // the swipe end allow scroll 
            }
        };
 ```
Consider adjusting ```motion:dragThreshold="15"``` affects how many pixels (default 10 pixels) you swipe before you start the swip.

```XML
 <OnSwipe
            motion:touchAnchorId="@+id/backdrop"
            motion:dragDirection="dragRight"
            motion:maxAcceleration="400"
            motion:maxVelocity="300"
            motion:dragThreshold="15"
            motion:springBoundary="bounceStart"
            motion:onTouchUp="neverCompleteToEnd"
            motion:touchAnchorSide="left"
            />
```

## Contributing

If you'd like to get involved and contribute please read [CONTRIBUTING](https://github.com/androidx/constraintlayout/blob/main/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

- **John Hoford** ([jafu888](https://github.com/jafu888))

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](https://github.com/androidx/constraintlayout/blob/main/LICENSE) file for details
