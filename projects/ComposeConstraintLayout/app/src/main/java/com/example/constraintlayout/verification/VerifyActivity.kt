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

package com.example.constraintlayout.verification

import android.os.Bundle
import android.util.Log
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
import androidx.constraintlayout.coreAndroid.PhoneState
import androidx.constraintlayout.motion.widget.Debug
import androidx.constraintlayout.tools.LinkServer
import com.example.constraintlayout.R
import com.example.constraintlayout.link.MotionLink
import com.google.accompanist.coil.rememberCoilPainter
import java.util.*

class VerifyActivity : AppCompatActivity() {
    private var mFrameLayout: FrameLayout? = null
    private var composeNum = 2
    private var MAX = 3
    private val TAG = "VerifyActivity"
    var map = HashMap<Int, String>();
    val linkServer = LinkServer()
    lateinit var link: MotionLink

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
        DesignElements.define("text-material") { id, params ->
            val text = params["text"] ?: "text"
            Text(
                modifier = Modifier.layoutId(id),
                text = text
            )
        }
        DesignElements.define("button-material") { id, params ->
            val text = params["text"] ?: "text"
            Button(
                modifier = Modifier.layoutId(id),
                onClick = {},
            ) {
                Text(text = text)
            }
        }
        DesignElements.define("image-coil") { id, params ->
            val url = params["url"] ?: "url"
            val description = params["description"] ?: "Image Description"
            Image(
                modifier = Modifier.layoutId(id),
                painter = rememberCoilPainter(url),
                contentDescription = description
            )
        }
    }

    @ExperimentalMaterialApi
    private fun show(com: ComposeView) {
        Log.v(TAG, Debug.getLoc() + " $composeNum ")

        com.setContent {
            when (composeNum) {
                0 -> VTest()
                1 -> VTest1()
                2 -> VTest2()
                3 -> VTest1()
                4 -> VTest2()
//                4 -> VTest02a()
//                5 -> VTest02b()
//                6 -> VTest02a()
//                7 -> VTest02b()
//                8 -> VTest02a()
//                9 -> VTest02b()
                else -> {
                    composeNum = 0;
                    Log.v(TAG, Debug.getLoc() + composeNum)
                    VTest2()
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
        mFrameLayout?.postDelayed(Runnable { doNext() }, 1000)
        link = MotionLink(mFrameLayout)
        link.addListener { event: MotionLink.Event, link: MotionLink ->
            fromLink(
                event,
                link
            )
        }
        setCompose();
        PhoneState(this) // monitor orientation present PhoneState.phoneOrientation
    }

    val layouts = java.util.HashMap<String, String>()

    @OptIn(ExperimentalMaterialApi::class)
    private fun fromLink(event: MotionLink.Event, link: MotionLink) {
//        Log.v(TAG, "===========================================================================================")
//        Log.v(TAG, Debug.getLoc() + " " + Arrays.toString(link.layoutNames))
//        Log.v(TAG, Debug.getLoc() + " " + link.layoutNames[link.lastUpdateLayout] + "  " + layouts.size)
        when (event) {
            MotionLink.Event.STATUS -> {
                Log.v(TAG, Debug.getLoc() + " STATUS")

            }
            MotionLink.Event.ERROR -> {
                Log.v(TAG, Debug.getLoc() + " ERROR")

            }
            MotionLink.Event.LAYOUT_LIST_UPDATE -> {
                Log.v(TAG, Debug.getLoc() + " LAYOUT_LIST_UPDATE")


            }
            MotionLink.Event.MOTION_SCENE_UPDATE -> {
                Log.v(TAG, Debug.getLoc() + " MOTION_SCENE_UPDATE")

            }
            MotionLink.Event.LAYOUT_UPDATE -> {
//                Log.v(TAG, Debug.getLoc() + " LAYOUT_UPDATE " + (link.layoutInfos != null))
                if (link.layoutInfos != null) {
                    layouts.put(link.layoutNames[link.lastUpdateLayout], link.layoutInfos)
                    if (link.layoutNames.size == layouts.size) {
                        printResults()
                    }
                }
            }
        }

    }

    fun printResults() {
        for (s in layouts.keys) {
            Log.v(TAG, "=======================$s===================")
            Log.v(TAG, " " + layouts[s])
            val str = readFromRaw(resources.getIdentifier(s, "raw", packageName))
            val s2 = layouts[s];
            if (s2 != null) {
                multi_line_comp(str, s2);
            }
            Log.v(TAG, " ")
        }
        Log.v(TAG, "================ DONE=========================")
    }

    fun multi_line_comp(s1: String, s2: String) {
        val l1 = s1.lines();
        val l2 = s2.lines();

        var dif = 0
        for (i in l1.indices) {
            if (l1[i] != l2[i]) {
                if (l1[i].contains("phone_orientation"))
                    continue
                Log.v(TAG, " " + i + ": \"" + l1[i] + "\" \"" + l2[i])
                dif++
            }
        }
        if (dif > 0) {
            Log.v(TAG, "======== $dif lines")
        }
    }

    fun readFromRaw(raw: Int): String {
        try {
            val ins = resources.openRawResource(raw)
            val b = ByteArray(ins.available())
            ins.read(b)
            return String(b)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @ExperimentalMaterialApi
    fun doNext() {
        composeNum++
        setCompose();
        if (composeNum != 0) {
            mFrameLayout?.postDelayed(Runnable { getLayout() }, 1000)
        }
    }

    @ExperimentalMaterialApi
    fun getLayout() {
        if (composeNum != 0) {
            link.getLayoutList()
            link.updateLayoutInformation()
            mFrameLayout?.postDelayed(Runnable { doNext() }, 1000)
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
