# ConstraintLayout 🗜️📏

![core](https://github.com/androidx/constraintlayout/workflows/core/badge.svg)

ConstraintLayout is an Android layout component which allows you to position and size widgets in a flexible way. This repository contains the core Java engine, Android library, validation tools, and experiments.

[Android Reference Docs](https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout)

Have a question that isn't answered here? Try StackOverflow for [ConstraintLayout](https://stackoverflow.com/questions/tagged/android-constraintlayout) or [MotionLayout](https://stackoverflow.com/questions/tagged/android-motionlayout).

## ✨🤩 Key Features

📐 [Aspect Ratio](https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout#ratio) defines one dimension of a widget as a ratio of the other one. If both `width` and `height` are set to `0dp` the system sets the largest dimensions that satisfy all constraints while maintaining the aspect ratio.
⛓️ [Chains](https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout#Chains) provide group-like behavior in a single axis (horizontally or vertically). The other axis can be constrained independently.
🦮 [Guidelines](https://developer.android.com/reference/androidx/constraintlayout/widget/Guideline) allow reactive layout behavior with fixed or percentage based positioning for multiple widgets.
🚧 [Barrier](https://developer.android.com/reference/androidx/constraintlayout/widget/Barrier) references multiple widgets to create a virtual guideline based on the most extreme widget on the specified side.
☂️ [Group](https://developer.android.com/reference/androidx/constraintlayout/widget/Group) constrols the visibility of a set of referenced widgets.
💫 [MotionLayout](https://developer.android.com/reference/androidx/constraintlayout/motion/widget/MotionLayout) a subclass of ConstraintLayout that supports transitions between constraint sets defined in MotionScenes.
🌊 [Flow](https://developer.android.com/reference/androidx/constraintlayout/helper/widget/Flow) is a VirtualLayout that allows positioning of referenced widgets horizontally or vertically similar to a Chain. If the referenced elements do not fit within the given bounds it has the ability to wrap them and create multiple chains.

## 📚👩‍🏫 Learning Materials

[Build a Responsive UI with ConstraintLayout](https://developer.android.com/training/constraint-layout)
[ConstraintLayout Codelab](https://codelabs.developers.google.com/codelabs/constraint-layout/index.html#0)
Introduction to MotionLayout [Part I](https://medium.com/google-developers/introduction-to-motionlayout-part-i-29208674b10d) | [Part II](https://medium.com/google-developers/introduction-to-motionlayout-part-ii-a31acc084f59) | [Part III](https://medium.com/google-developers/introduction-to-motionlayout-part-iii-47cd64d51a5) | [Part IV](https://medium.com/google-developers/defining-motion-paths-in-motionlayout-6095b874d37)
[MotionLayout Codelab](https://codelabs.developers.google.com/codelabs/motion-layout#0)

## 🤝 Contributing

If you'd like to get involved and contribute please read [CONTRIBUTING](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## 💻 Authors

* **John Hoford** ([jafu888](https://github.com/jafu888))
* **Nicolas Roard** ([camaelon](https://github.com/camaelon))

See also the list of [contributors](contributors) who participated in this project.

## 🔖 License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details
