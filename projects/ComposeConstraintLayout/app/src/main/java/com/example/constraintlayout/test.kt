package androidx.constraintlayout.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.Text
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import com.example.constraintlayout.R

@Preview
@Composable
public fun Screen() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (image, header, tag1, tag2, tag3, bSignup, bLogin, disclaimer) = createRefs()
        val g1 = createGuidelineFromStart(44.dp)
        val g2 = createGuidelineFromEnd(44.dp)
        Image(
            modifier = Modifier.constrainAs(image) {
                width = Dimension.value(201.dp)
                height = Dimension.value(179.dp)
                top.linkTo(parent.top, 32.dp)
                start.linkTo(g1)
            },
            painter = painterResource(id = R.drawable.intercom_snooze),
            contentDescription = null
        )
        Text(
            modifier = Modifier.constrainAs(header) {
                top.linkTo(image.bottom, 32.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            },
            text = stringResource(id = R.string.welcome_header),
            style = MaterialTheme.typography.h5,
        )
        Text(
            modifier = Modifier.constrainAs(tag1) {
                top.linkTo(header.bottom, 16.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            },
            text = stringResource(id = R.string.welcome_tagline1)
        )
        Text(
            modifier = Modifier.constrainAs(tag2) {
                top.linkTo(tag1.bottom, 8.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            },
            text = stringResource(id = R.string.welcome_tagline2)
        )
        Text(
            modifier = Modifier.constrainAs(tag3) {
                top.linkTo(tag2.bottom, 8.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            },
            text = stringResource(id = R.string.welcome_tagline3)
        )
        Button(
            modifier = Modifier.constrainAs(bSignup) {
                bottom.linkTo(bLogin.top, 16.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            },
            onClick = {}
        ) {
            Text(text = stringResource(id = R.string.sign_up))
        }
        Button(
            modifier = Modifier.constrainAs(bLogin) {
                bottom.linkTo(disclaimer.top, 16.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            },
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(
            modifier = Modifier.constrainAs(disclaimer) {
                bottom.linkTo(parent.bottom, 8.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            },
            text = stringResource(id = R.string.trial_disclaimer),
            style = MaterialTheme.typography.caption,
        )
    }
}

@Preview
@Composable
public fun Screen2() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val bLogin = createRef()
        val g1 = createGuidelineFromStart(44.dp)
        val g2 = createGuidelineFromEnd(44.dp)
        Button(
            modifier = Modifier.constrainAs(bLogin) {
                bottom.linkTo(parent.bottom, 16.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            },
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
    }
}

@Preview
@Composable
public fun Screen3() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val bLogin = createRef()
        val g1 = createGuidelineFromTop(44.dp)
        val g2 = createGuidelineFromBottom(44.dp)
        Button(
            modifier = Modifier.constrainAs(bLogin) {
                top.linkTo(g1)
                bottom.linkTo(g2)
                height = Dimension.fillToConstraints
            },
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
    }
}

@Preview
@Composable
public fun Screen4() {
    ConstraintLayout(
        ConstraintSet {
            val image = createRefFor("image")
            val header = createRefFor("header")
            val tag1 = createRefFor("tag1")
            val tag2 = createRefFor("tag2")
            val tag3 = createRefFor("tag3")
            val bSignup = createRefFor("bSignup")
            val bLogin = createRefFor("bLogin")
            val disclaimer = createRefFor("disclaimer")

            val g1 = createGuidelineFromStart(44.dp)
            val g2 = createGuidelineFromEnd(44.dp)

            constrain(image) {
                width = Dimension.value(201.dp)
                height = Dimension.value(179.dp)
                top.linkTo(parent.top, 32.dp)
                start.linkTo(g1)
            }

            constrain(header) {
                top.linkTo(image.bottom, 32.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            }

            constrain(tag1) {
                top.linkTo(header.bottom, 16.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            }

            constrain(tag2) {
                top.linkTo(tag1.bottom, 8.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            }

            constrain(tag3) {
                top.linkTo(tag2.bottom, 8.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            }

            constrain(bSignup) {
                bottom.linkTo(bLogin.top, 16.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            }

            constrain(bLogin) {
                bottom.linkTo(disclaimer.top, 16.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            }

            constrain(disclaimer) {
                bottom.linkTo(parent.bottom, 8.dp)
                start.linkTo(g1)
                end.linkTo(g2)
                width = Dimension.fillToConstraints
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(modifier = Modifier.layoutId("image"),
            painter = painterResource(id = R.drawable.intercom_snooze),
            contentDescription = null
        )
        Text(modifier = Modifier.layoutId("header"),
            text = stringResource(id = R.string.welcome_header),
            style = MaterialTheme.typography.h5,
        )
        Text(modifier = Modifier.layoutId("tag1"),
            text = stringResource(id = R.string.welcome_tagline1)
        )
        Text(modifier = Modifier.layoutId("tag2"),
            text = stringResource(id = R.string.welcome_tagline2)
        )
        Text(modifier = Modifier.layoutId("tag3"),
            text = stringResource(id = R.string.welcome_tagline3)
        )
        Button(modifier = Modifier.layoutId("bSignup"),
            onClick = {}
        ) {
            Text(text = stringResource(id = R.string.sign_up))
        }
        Button(modifier = Modifier.layoutId("bLogin"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(modifier = Modifier.layoutId("disclaimer"),
            text = stringResource(id = R.string.trial_disclaimer),
            style = MaterialTheme.typography.caption,
        )
    }
}

@Preview
@Composable
public fun Screen5() {
    val cs = """
        {
          g1 : { type: 'guideline', at: ['start', 44] },
          g2 : { type: 'guideline', at: ['end', 44] },
          image: {
            width: 201, height: 179,
            top: ['parent','top', 32],
            start: ['g1'] 
          },
          header: {
            width: 'match_constraints',
            start: ['g1'], end: ['g2'],
            top: ['image','bottom', 32]
          },
          tag1: {
            width: 'match_constraints',
            start: ['g1'], end: ['g2'],
            top: ['header','bottom', 16]
          },
          tag2: {
            width: 'match_constraints',
            start: ['g1'], end: ['g2'],
            top: ['tag1','bottom', 8]
          },
          tag3: {
            width: 'match_constraints',
            start: ['g1'], end: ['g2'],
            top: ['tag2','bottom', 8]
          },
          bSignup: {
            width: 'match_constraints',
            start: ['g1'], end: ['g2'],
            bottom: ['bLogin','top', 16]
          },
          bLogin: {
            width: 'match_constraints',
            start: ['g1'], end: ['g2'],
            bottom: ['disclaimer','top', 16]
          },
          disclaimer: {
            width: 'match_constraints',
            start: ['g1'], end: ['g2'],
            bottom: ['parent','bottom', 8]
          },
        }
    """
    ConstraintLayout(
        ConstraintSet {},
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(modifier = Modifier.layoutId("image"),
            painter = painterResource(id = R.drawable.intercom_snooze),
            contentDescription = null
        )
        Text(modifier = Modifier.layoutId("header"),
            text = stringResource(id = R.string.welcome_header),
            style = MaterialTheme.typography.h5,
        )
        Text(modifier = Modifier.layoutId("tag1"),
            text = stringResource(id = R.string.welcome_tagline1)
        )
        Text(modifier = Modifier.layoutId("tag2"),
            text = stringResource(id = R.string.welcome_tagline2)
        )
        Text(modifier = Modifier.layoutId("tag3"),
            text = stringResource(id = R.string.welcome_tagline3)
        )
        Button(modifier = Modifier.layoutId("bSignup"),
            onClick = {}
        ) {
            Text(text = stringResource(id = R.string.sign_up))
        }
        Button(modifier = Modifier.layoutId("bLogin"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(modifier = Modifier.layoutId("disclaimer"),
            text = stringResource(id = R.string.trial_disclaimer),
            style = MaterialTheme.typography.caption,
        )
    }
}


@Preview
@Composable
public fun ScreenExample() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (button, title) = createRefs()
        val g1 = createGuidelineFromStart(80.dp)
        Button(
            modifier = Modifier.constrainAs(button) {
                top.linkTo(title.bottom, 16.dp)
                start.linkTo(g1)
            },
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(modifier = Modifier.constrainAs(title) {
            centerVerticallyTo(parent)
            start.linkTo(g1)
        },
            text = stringResource(id = R.string.welcome_header),
            style = MaterialTheme.typography.h2,
        )
    }
}

@Preview
@Composable
public fun ScreenExample2() {
    ConstraintLayout(
        ConstraintSet {
            val button = createRefFor("button")
            val title = createRefFor("title")
            val g1 = createGuidelineFromStart(80.dp)
            constrain(button) {
                top.linkTo(title.bottom, 16.dp)
                start.linkTo(g1)
            }
            constrain(title) {
                centerVerticallyTo(parent)
                start.linkTo(g1)
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(modifier = Modifier.layoutId("title"),
            text = stringResource(id = R.string.welcome_header),
            style = MaterialTheme.typography.h2,
        )
    }
}

@Preview(group = "new")
@Composable
public fun ScreenExample3() {
    ConstraintLayout(
        ConstraintSet("""
            {
                g1: { type: 'guideline', start: 80 },
                button: {
                  top: ['title', 'bottom', 16],
                  start: ['g1', 'start']
                },
                title: {
                  centerVertically: 'parent',
                  start: ['g1', 'start']
                }
            }
        """),
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
        Text(modifier = Modifier.layoutId("title"),
            text = stringResource(id = R.string.welcome_header),
            style = MaterialTheme.typography.h2,
        )
    }
}

@Preview(group = "new2")
@Composable
public fun ScreenExample4() {
    ConstraintLayout(
        ConstraintSet("""
            {
                g1: { type: 'guideline', percent: 0.5 },
                button: {
                  start: ['g1', 'start']
                }
            }
        """),
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(
            modifier = Modifier.layoutId("button"),
            onClick = {},
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
    }
}