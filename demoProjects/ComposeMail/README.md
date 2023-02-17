## ComposeMail Sample
Sample project that demonstrates the use of MotionLayout for a Mail client application.

Includes MotionLayout examples for:
- Foldable Support
  - MotionSceneDsl + MotionLayout: [MotionScene](https://github.com/androidx/constraintlayout/blob/7fed594bbbd3d244767d093534dc5ed6ba3ce3e3/demoProjects/ComposeMail/app/src/main/java/com/example/composemail/ui/home/Home.kt#L55), [MotionLayout()](https://github.com/androidx/constraintlayout/blob/7fed594bbbd3d244767d093534dc5ed6ba3ce3e3/demoProjects/ComposeMail/app/src/main/java/com/example/composemail/ui/home/Home.kt#L237), [resolveConstraintSet()](https://github.com/androidx/constraintlayout/blob/7fed594bbbd3d244767d093534dc5ed6ba3ce3e3/demoProjects/ComposeMail/app/src/main/java/com/example/composemail/ui/home/Home.kt#L283)
  - Collect Foldable Info and provide as State: [LocalFoldableInfo](https://github.com/androidx/constraintlayout/blob/7fed594bbbd3d244767d093534dc5ed6ba3ce3e3/demoProjects/ComposeMail/app/src/main/java/com/example/composemail/ui/compositionlocal/FoldableInfo.kt#L22), [collectFoldableInfoAsState()](https://github.com/androidx/constraintlayout/blob/7fed594bbbd3d244767d093534dc5ed6ba3ce3e3/demoProjects/ComposeMail/app/src/main/java/com/example/composemail/MainActivity.kt#L77)
- Usage with Paging Library
  - Handle Placeholders as Loading Indicators: [MailItem()](https://github.com/androidx/constraintlayout/blob/7fed594bbbd3d244767d093534dc5ed6ba3ce3e3/demoProjects/ComposeMail/app/src/main/java/com/example/composemail/ui/mails/MailItem.kt#L74)
## ComposeMail Overview
-----------------------------

### App Demo
https://user-images.githubusercontent.com/61845568/217963382-eea1768d-1439-4259-8910-028f41c5e9d1.webm

### Mail Item - [Composable](https://github.com/androidx/constraintlayout/blob/229131491d82ff88cfe65bcaa0de436e9aeb4650/demoProjects/ComposeMail/app/src/main/java/com/example/composemail/ui/mails/MailItem.kt#L70)
https://user-images.githubusercontent.com/61845568/176752054-d8791336-244e-4037-83f3-1d7204fd7562.mp4

### New Mail Button - [Composable](https://github.com/androidx/constraintlayout/blob/229131491d82ff88cfe65bcaa0de436e9aeb4650/demoProjects/ComposeMail/app/src/main/java/com/example/composemail/ui/newmail/NewMail.kt#L321)
https://user-images.githubusercontent.com/61845568/176752227-b79298ee-b4cb-4b2e-a39a-5baf73cc569a.mp4

## Contributing

If you'd like to get involved and contribute please read [CONTRIBUTING](https://github.com/androidx/constraintlayout/blob/main/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

- **Oscar Adame** ([oscar-ad](https://github.com/oscar-ad))

See also the list of [contributors](https://github.com/androidx/constraintlayout/graphs/contributors) who participated in this project.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](https://github.com/androidx/constraintlayout/blob/main/LICENSE) file for details
