# 👋 Contributing to ConstraintLayout

First of all, thanks for any contributions you make to the project, they're really appreciated! If you would like to contribute code to ConstraintLayout you can do so through GitHub by forking the repository and sending a pull request.

When submitting code, please make every effort to follow existing conventions and style in order to keep the code as readable as possible. Please also make sure your code compiles by running `./gradlew build`.

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Getting Started Checklist

Following this file from beginning to end can be intimidating, so here's a simple check list to follow as you go:

* [Sign Google's CLA](https://cla.developers.google.com/) if you haven't already
* Download the latest stable versions of [Android Studio](https://developer.android.com/studio) and [IntelliJ](https://www.jetbrains.com/idea/download/)
* Build the project and run the tests to make sure you're in a good place.
* Find an outstanding issue on [Google's issue tracker](https://issuetracker.google.com/issues?q=componentid:323867%20status:open) or the [Github Issues](https://github.com/androidx/constraintlayout/issues) in this repository.
* If you want to make a feature request, feel free to make a PR - or if its something you're not sure will be accepted, write about it in an issue.
* Submit your first Pull Request. Make sure all commit status check are pass to be sure it will get reviewed.

## Prerequisites

#### 📝 Signed CLA

Before we can look at your pull request, you'll need to sign a (Contributor License Agreement (CLA))[https://cla.developers.google.com/].

#### 🦾🤖 Android Studio

* [Download the latest version of Android Studio](https://developer.android.com/studio)
* Download resources in Android SDK Manger: Android Build Tools to 30.0.2 and Android SDKs 25 & 30
* In the AVD Manager you'll want to create a Nexus 5 API 25 emulator
* Add the latest Kotlin plugin V1.4.21 within Android Studio
* If you intend to work with the Compose bindings of ConstraintLayout you'll also need to download [Android Studio Arctic Fox](https://developer.android.com/studio/preview)

#### ✈️🧠 IntelliJ

The projects under the `desktop` directory are all meant to be run by IntelliJ where the resulting application runs on Mac/Linux/Windows.

* [Download the latest IntelliJ Community Edition](https://www.jetbrains.com/idea/download/)

## 🔨 Building Project

See [the ConstraintLayout README](constraintlayout/README.md) for how to build the library.

## 🧪 Running Tests

#### ✅🔬 Unit Tests

The core library has a set of tests that use the TestNG runner. You should be able to run them directly from Android Studio or `./gradlew :core:test` from the `constraintlayout` working directory.

#### 📱🔬 ConstraintLayout Integration Tests

See the [Validation Tool README](desktop/ValidationTool/README.md)

#### 💫🔬 MotionLayout Integration Tests

TBD

## Looking for something to do?

This project has a list of issues on [Google's issue tracker](https://issuetracker.google.com/issues?q=componentid:323867%20status:open) as well in the [Github Issues](https://github.com/androidx/constraintlayout/issues) in this repository.

## 🚀 Deployment

See [the ConstraintLayout README](constraintlayout/README.md) for how to deploy the library.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 
