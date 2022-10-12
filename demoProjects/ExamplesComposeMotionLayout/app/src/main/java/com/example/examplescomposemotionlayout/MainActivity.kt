package com.example.examplescomposemotionlayout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.examplescomposemotionlayout.ui.theme.ExamplesComposeMotionLayoutTheme

class MainActivity : ComponentActivity() {
    private val composeKey = "USE_COMPOSE"

    private var cmap =   listOf(
        get("CollapsingToolbarJson") {ToolBarExampleDsl()},
        get("CollapsingToolbarDSL") {ToolBarExample()},
        get("ToolBarLazyExample") {ToolBarLazyExample()},
        get("ToolBarLazyExampleDsl") {ToolBarLazyExampleDsl()},

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extra = intent.extras
        var cfunc: ComposeFunc? = null
        if (extra != null) {
            val composeName = extra.getString(composeKey)
            for (composeFunc in cmap) {
                if (composeFunc.toString() == composeName) {
                    cfunc = composeFunc
                    break
                }
            }
        }
        setContent {
            ExamplesComposeMotionLayoutTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (cfunc != null) {
                        Log.v("MAIN", " running $cfunc")
                        cfunc.Run()
                    } else {
                        ComposableMenu(map = cmap) { act -> launch(act) }
                    }

                }
            }
        }
    }

    private fun launch(to_run: ComposeFunc) {
        Log.v("MAIN", " launch $to_run")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(composeKey, to_run.toString())
        startActivity(intent)

    }
}

@Composable
fun ComposableMenu(map: List<ComposeFunc>, act: (act:ComposeFunc) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        for (cFunc in map) {
            Button(onClick = { act(cFunc)}) {
                Text(cFunc.toString())
            }
        }
    }
}

fun get(name: String, cRun:@Composable () ->  Unit):ComposeFunc {
    return object: ComposeFunc {
        @Composable
        override fun Run() {
            cRun()
        }
        override fun toString(): String {
            return name
        }
    }
}

interface  ComposeFunc {
    @Composable
    fun Run()
    override fun toString():String
}
