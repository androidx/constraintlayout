/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.constraintlayout

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.compose.*
import androidx.constraintlayout.coreAndroid.PhoneState
import androidx.constraintlayout.tools.LinkServer
import com.google.accompanist.coil.rememberCoilPainter

class MainActivity : AppCompatActivity() {
    private var mFrameLayout: FrameLayout? = null
    private var composeNum = 0
    private val START_NUMBER = 44
    private var demos:ArrayList<CompFunc> = ArrayList()
    var map = HashMap<Int, String>();
    val linkServer = LinkServer()

    init {

        map.put(24, "scaleX/Y")
        map.put(25, "tanslationX/Y")
       // map.put(26, "rotationZ")
        map.put(27, "rotationXY")
        map.put(28, "Cycle Scale")
        map.put(29, "Cycle TranslationXY")
        map.put(30, "Cycle RotationZ")
        map.put(31, "Cycle RotationXY")

        defineDesignElements()
        linkServer.start()
    }

    private fun defineDesignElements() {
        DesignElements.define("text-material") {
                id, params ->
            val text = params["text"] ?: "text"
            Text(modifier = Modifier.layoutId(id),
                text= text)
        }
        DesignElements.define("button-material") {
                id, params ->
            val text = params["text"] ?: "text"
            Button(
                modifier = Modifier.layoutId(id),
                onClick = {},
            ) {
                Text(text = text)
            }
        }
        DesignElements.define("image-coil") {
                id, params ->
            val url = params["url"] ?: "url"
            val description = params["description"] ?: "Image Description"
            Image(modifier = Modifier.layoutId(id),
                painter = rememberCoilPainter(url),
                contentDescription = description
            )
        }
    }

    interface CompFunc {
        @Composable
        fun Run()
    }



    private fun setup() {
        if (demos.size > 0) {
            return
        }
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample2() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample3() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample4() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample5() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample6() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample7() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample8() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample9() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample10() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample11() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample13() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample14() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample15() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample16() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample17() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample18() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample19() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample20() } })

        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample1() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample2() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample3() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample4() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample5() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample6() } })

        demos.add(object : CompFunc { @Composable override fun Run() { AttributesScale() } })
        demos.add(object : CompFunc { @Composable override fun Run() { AttributesTranslationXY() } })
        demos.add(object : CompFunc { @Composable override fun Run() { AttributesRotationZ() } })
        demos.add(object : CompFunc { @Composable override fun Run() { AttributesRotationXY() } })

        demos.add(object : CompFunc { @Composable override fun Run() { CycleScale() } })
        demos.add(object : CompFunc { @Composable override fun Run() { CycleTranslationXY() } })
        demos.add(object : CompFunc { @Composable override fun Run() { CycleRotationZ() } })
        demos.add(object : CompFunc { @Composable override fun Run() { CycleRotationXY() } })

        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample7() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample21() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ScreenExample22() } })
        demos.add(object : CompFunc { @Composable override fun Run() { ResizeExample1() } })

        demos.add(object : CompFunc { @Composable override fun Run() { ExampleLayout() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample8() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample9() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample10() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample11() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MotionExample12() } })

        demos.add(object : CompFunc { @Composable override fun Run() { OnSwipeExperiment() } })
        demos.add(object : CompFunc { @Composable override fun Run() { OnSwipeSample1() } })
        demos.add(object : CompFunc { @Composable override fun Run() { OnSwipeSample2() } })
        demos.add(object : CompFunc { @Composable override fun Run() { MultiSwipe() } })

        demos.add(object : CompFunc { @Composable override fun Run() { Example () } })
        demos.add(object : CompFunc { @Composable override fun Run() { RowColExample () } })
        demos.add(object : CompFunc { @Composable override fun Run() { ShowTwenty  () } })
        demos.add(object : CompFunc { @Composable override fun Run() { Repro  () } })

        composeNum =  if (demos.size < START_NUMBER)
            demos.size - 1 else START_NUMBER
     }


    @ExperimentalMaterialApi
    private fun show(com: ComposeView) {
        setup()
        println(" $composeNum ")
        com.setContent {
            composeNum = (composeNum + demos.size) % demos.size
            demos[composeNum].Run();
        }
    }


    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
 

        if (savedInstanceState.containsKey("SHOWNUM")) {
            composeNum = savedInstanceState.getInt("SHOWNUM")
        }
        }
        setContentView(R.layout.activity_main)
        mFrameLayout = findViewById<FrameLayout>(R.id.frame)
        setCompose()
        PhoneState(this) // monitor orientation present PhoneState.phoneOrientation
    }

    override fun onPause() {
        super.onPause()
        linkServer.stop()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("SHOWNUM", composeNum)
    }

    @ExperimentalMaterialApi
    fun setCompose() {
        if (mFrameLayout!!.childCount > 0) {
            mFrameLayout!!.removeAllViews()
        }
        var com = ComposeView(this);
        mFrameLayout!!.addView(com)
        show(com)
        findViewById<TextView>(R.id.layoutName).text = "! example " + composeNum;
        var sub = " example " + composeNum
        if (map.containsKey(composeNum)) {
            sub += " " + map.get(composeNum)
        }
        title = sub;
    }

    @ExperimentalMaterialApi
    fun prev(view: View) {
        composeNum--
        setCompose()
    }

    @ExperimentalMaterialApi
    fun next(view: View) {
        composeNum++
        setCompose();
    }
}
