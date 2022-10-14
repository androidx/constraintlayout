## MotionLayout For Compose Examples
Sample project that demonstrates various uses of MotionLayout in Compose

## Overview
-----------------------------

### App Demo

#### Motion Layout as Collapsing toolbar for Column

This is based on using 
```kotlin
Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(scroll)
    )
```

* [DSL Version](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesComposeMotionLayout/app/src/main/java/com/example/examplescomposemotionlayout/CollapsingToolbarDsl.kt)
* [JSON Version](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesComposeMotionLayout/app/src/main/java/com/example/examplescomposemotionlayout/CollapsingToolbarJson.kt)

https://user-images.githubusercontent.com/15019413/195418373-5a92e2b7-9ff1-4a8a-851e-09951557147b.mp4

#### Motion Layout as Collapsing toolbar for Lazy column

This is based on using
```kotlin
        Box(
            Modifier
                .fillMaxWidth()
                .nestedScroll(nestedScrollConnection)) {
            LazyColumn() {
                items(100) {
                    Text(text = "item $it", modifier = Modifier.padding(4.dp))
                }
            }
        }
```

* [DSL Version](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesComposeMotionLayout/app/src/main/java/com/example/examplescomposemotionlayout/CollapsingToolbarLazyDsl.kt)
* [JSON Version](https://github.com/androidx/constraintlayout/blob/main/demoProjects/ExamplesComposeMotionLayout/app/src/main/java/com/example/examplescomposemotionlayout/CollapsingToolbarLazyJson.kt)

https://user-images.githubusercontent.com/15019413/195679372-153f6ccf-d263-4085-9441-c29c105360e7.mp4



## Contributing

If you'd like to get involved and contribute please read [CONTRIBUTING](https://github.com/androidx/constraintlayout/blob/main/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

- **John Hoford** ([jafu888](https://github.com/jafu888))

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](https://github.com/androidx/constraintlayout/blob/main/LICENSE) file for details
