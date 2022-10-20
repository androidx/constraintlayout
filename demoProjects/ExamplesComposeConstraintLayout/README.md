## ConstraintLayout For Compose Examples
Sample project that demonstrates some unique use of ConstraintLayoutCompose

## Overview
-----------------------------

### Flow Demos Keypad and Calendar

Source 
* [FlowCalendar.kt](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesComposeConstraintLayout/app/src/main/java/com/example/examplescomposeconstraintlayout/FlowCalendar.kt) - scrolling list of a calendar in 100 LOC!
* [FlowKeyPad.kt](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesComposeConstraintLayout/app/src/main/java/com/example/examplescomposeconstraintlayout/FlowKeyPad.kt) - Simple keypad in 40 LOC


These both folow a similar pattern where we create a list of strings which we turn into ids for flow
```kotlin
 ConstraintLayout(
   ConstraintSet {
     val keys = names.map { createRefFor(it) }.toTypedArray()
     val flow = createFlow(
          elements      = keys,
          maxElement    = 3,
          wrapMode      = Wrap.Aligned,
          verticalGap   = 8.dp,
          horizontalGap = 8.dp
        )
     constrain(flow) {
       centerTo(parent)
     }
   },
   modifier = Modifier.background(Color(0xFFDAB539)).padding(8.dp)
) {
  names.map {
    Button(modifier = Modifier.layoutId(it), onClick = {}) {
      Text(text = it)
    }
  }
}
   
```

![examplesComposeConstraintLayout](https://user-images.githubusercontent.com/15019413/196848961-69b7e895-f86a-4f3a-a52f-688f6e5c98a7.gif)

 


## Contributing

If you'd like to get involved and contribute please read [CONTRIBUTING](https://github.com/androidx/constraintlayout/blob/main/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

- **John Hoford** ([jafu888](https://github.com/jafu888))

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](https://github.com/androidx/constraintlayout/blob/main/LICENSE) file for details
