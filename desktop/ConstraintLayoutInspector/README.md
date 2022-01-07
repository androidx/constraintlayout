# ConstraintLayout live inspector

This is a simple inspector utility that can connect to a running
application and inspect the current state.

At the moment, this is only enabled for MotionLayout in Compose.

# What can it do

- JSON MotionScene that contains a `Debug: { name: 'somename' }` can be connected to
- You can get the current JSON description used by the MotionLayout
- You can edit it and push it back to the running application
- You can scrub the progress from 0 to 1 using the bottom slider.
- You can toggle on/off the debug draw for MotionLayout

# How to

- This only works for JSON MotionScenes for now
- the JSON description should contain a `Debug: { name: 'somename' }` (see example 19)
- copy the DebugServer.kt file in your app
- make sure to setup an ADB tunnel: `adb forward tcp:9999 tcp:9999`
- build or download the executable jar Link.jar (to run: "java -jar Link.jar")
- run your app and your phone or emulator and the live inspector app on your desktop

# Notes

The value set via the slider takes over the progress value of the running composable;
to reset it to be handled by compose, click on the reset button.
