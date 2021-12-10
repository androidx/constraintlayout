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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.compose.DesignElements
import androidx.constraintlayout.compose.ScreenExample
import androidx.constraintlayout.coreAndroid.PhoneState
import androidx.constraintlayout.tools.LinkServer
import com.example.constraintlayout.link.MotionLink
import com.google.accompanist.coil.rememberCoilPainter

class VerifyActivity : AppCompatActivity() {
    private var mFrameLayout: FrameLayout? = null
    private var composeNum = 41
    private var MAX = 40

    var map = HashMap<Int, String>();
    val linkServer = LinkServer()
    lateinit var link:MotionLink
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

    @ExperimentalMaterialApi
    private fun show(com: ComposeView) {
        println(" $composeNum ")

        com.setContent {

            when (composeNum) {
                0 -> VTest02a()
                1 -> VTest02b()
                2 -> VTest02a()
                3 -> VTest02b()
                4 -> VTest02a()
                5 -> VTest02b()
                6 -> VTest02a()
                7 -> VTest02b()
                8 -> VTest02a()
                9 -> VTest02b()
                else -> {
                    composeNum = (composeNum + 9) % 9
                    println(composeNum)
                    ScreenExample()
                }
            }
        }
    }



    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
 

        if (savedInstanceState.containsKey("SHOWNUM")) {
            composeNum = savedInstanceState.getInt("SHOWNUM")
        }
        }
        setContentView(R.layout.activity_main)
        mFrameLayout = findViewById<FrameLayout>(R.id.frame)
        mFrameLayout?.postDelayed( Runnable { doNext() },1000)
        link = MotionLink(mFrameLayout)
        setCompose();
         PhoneState(this) // monitor orientation present PhoneState.phoneOrientation
    }
    @ExperimentalMaterialApi
    fun doNext() {
        composeNum++
        setCompose();
        if (composeNum  != 1) {
            mFrameLayout?.postDelayed( Runnable { doNext() },1000)
            link.getLayoutList()
        } else {
            for (s in link.layoutNames) {
                println(s)
            }
        }
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
