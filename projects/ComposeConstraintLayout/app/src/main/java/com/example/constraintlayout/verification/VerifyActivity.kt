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
import androidx.constraintlayout.core.parser.CLParser
import androidx.constraintlayout.coreAndroid.PhoneState
import androidx.constraintlayout.motion.widget.Debug
import androidx.constraintlayout.tools.LinkServer
import com.example.constraintlayout.R
import com.example.constraintlayout.link.MotionLink
import com.google.accompanist.coil.rememberCoilPainter
import java.util.*

class VerifyActivity : AppCompatActivity() {
    private var mFrameLayout: FrameLayout? = null
    private var composeNum = 0

    private var MAX = 7
    private val TAG = "VerifyActivity"
    var map = HashMap<Int, String>();
    val linkServer = LinkServer()
    lateinit var link: MotionLink
    val layouts = java.util.HashMap<String, String>()

    init {
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
        com.setContent {

            when (composeNum) {
                0 -> End()
                1 -> VTest02a()
                2 -> VTest02b()
                3 -> VTest02c()
                4 -> VTest02d()
                5 -> VTest02e()
                6 -> VTest02f()
                7 -> VTest02g()

                else -> {
                    composeNum = 0;
                    Log.v(TAG, Debug.getLoc() + " reset " + composeNum)
                    End()
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

    @OptIn(ExperimentalMaterialApi::class)
    private fun fromLink(event: MotionLink.Event, link: MotionLink) {
        val name = link.layoutNames[link.lastUpdateLayout]
        val str = Arrays.toString(link.layoutNames) + name
        val f = link.layoutInfos;

        when (event) {
            MotionLink.Event.STATUS -> {
                Log.v(TAG, Debug.getLoc() + ">=>= STATUS")

            }
            MotionLink.Event.ERROR -> {
                Log.v(TAG, Debug.getLoc() + ">=>= ERROR")

            }
            MotionLink.Event.LAYOUT_LIST_UPDATE -> {
                link.selectMotionScene(name)
                link.updateLayoutInformation()

            }
            MotionLink.Event.MOTION_SCENE_UPDATE -> {
                Log.v(TAG, Debug.getLoc() + ">=>= MOTION_SCENE_UPDATE")
            }
            MotionLink.Event.LAYOUT_UPDATE -> {
                Log.v(
                    TAG,
                    Debug.getLoc() + " layout : " + name + " =  " + f?.substring(
                        0,
                        f.indexOf('\n')
                    )
                )
                if (link.layoutInfos != null) {
                    layouts.put(link.layoutNames[link.lastUpdateLayout], link.layoutInfos)
                    if (link.layoutNames.size == layouts.size && composeNum == MAX) {
                        printResults()
                    }
                }

                mFrameLayout?.postDelayed(Runnable { doNext() }, 200)
            }
        }

    }

    fun printResults() {

        for (s in layouts.keys) {

            var str = readFromRaw(resources.getIdentifier(s, "raw", packageName))
            str = CLParser.parse(str).toFormattedJSON()
            var s2 = layouts[s];
            s2 = CLParser.parse(s2).toFormattedJSON()
            if (s2 != null) {
                if (multiLineComp(str, s2)) {
                    Log.v(TAG, Debug.getLoc() + "  $s fail !!!!!!!!!!!!!")
                    Log.v(TAG, Debug.getLoc() + "\n" + s2);
                } else {
                    Log.v(TAG, Debug.getLoc() + " $s  pass")
                }
            }

        }
    }

    private fun multiLineComp(s1: String?, s2: String): Boolean {
        if (s1 == null) {
            Log.v(TAG, "======== no save data")
            return true
        }
        val l1 = s1.lines();
        val l2 = s2.lines();
        var i1 = 0
        var i2 = 0;
        var dif = 0

        while (i1 < l1.size && i2 < l2.size) {
            if (l1[i1].contains("phone_orientation")) {
                i1++;
                continue
            }
            if (l2[i2].contains("phone_orientation")) {
                i2++;
                continue
            }
            if (l1[i1] != l2[i2]) {

                if (l2.size <= i2) {
                    Log.v(TAG, " " + i1 + ": \"" + l1[i1] + "\" \"")
                } else {
                    Log.v(TAG, " " + i1 + ": \"" + l1[i1] + "\"  -------   \"" + l2[i2] + "\"")
                }
                dif++
            }
            i1++
            i2++
        }
        if (dif > 0) {
            Log.v(TAG, "======== $dif lines")
        }
        return dif > 0
    }

    fun readFromRaw(raw: Int): String? {
        try {

            val ins = resources.openRawResource(raw)
            val b = ByteArray(ins.available())
            ins.read(b)
            return String(b)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
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
