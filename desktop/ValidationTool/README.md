# Validation Tool

This is an IntelliJ Gradle-based Java project. It is meant to run tests against [projects/ConstraintLayoutValidation](projects/ConstraintLayoutValidation).

## ⚠️ Prerequisite

You should create an emulator with Android Studio AVD Manager that is specifically `API 25 Nexus 5`. The Android API level and emulator resolution is very important as the measurements taken for these integration tests currently vary depending on the actual device resolution and small changes between API levels. If you use a different emulator you will have to create a new baseline, but this tool is not yet capable of using multiple baselines.

## 🔨 How to Run

Currently these tests require building and running two applications and enabling adb port forwarding.

The first thing to run is the Android app in [projects/ConstraintLayoutValidation](projects/ConstraintLayoutValidation) in Android Studio on an `API 25 Nexus 5` emulator.

You'll then want to run this Validation Tool by right clicking `android.constraintlayout.validation.Main.java` in IntelliJ and selecting `Run Main.main()` - this should render a Java Swing application on your local machine.

The last thing to run is `adb forward tcp:4242 tcp:4242` to get adb port forwarding running so that the ValidationTool can communicate with the ConstraintLayoutValidation Android app.

## 👨‍💻 How to Use

To use the Validation Tool once its running you'll want to hit `Connect` - which should then show a list of all the validation tests to run. These tests correlate with all the layout files being rendered in the Android app.

Clicking `Validate` will run all tests against prerecorded measurements which are stored in the `references` folder. Each run will show a number of visual indicators for whether it passed or failed. After running the tests you can click on each result which should render the layout, the expected bounding box of each view, and the actual measured bounding box of each view.
