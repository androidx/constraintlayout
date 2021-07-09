package com.example.constraintlayout

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.compose.ScreenExample
import androidx.constraintlayout.compose.ScreenExample10
import androidx.constraintlayout.compose.ScreenExample11
import androidx.constraintlayout.compose.ScreenExample12
import androidx.constraintlayout.compose.ScreenExample13
import androidx.constraintlayout.compose.ScreenExample14
import androidx.constraintlayout.compose.ScreenExample15
import androidx.constraintlayout.compose.ScreenExample16
import androidx.constraintlayout.compose.ScreenExample17
import androidx.constraintlayout.compose.ScreenExample18
import androidx.constraintlayout.compose.ScreenExample19
import androidx.constraintlayout.compose.ScreenExample2
import androidx.constraintlayout.compose.ScreenExample3
import androidx.constraintlayout.compose.ScreenExample4
import androidx.constraintlayout.compose.ScreenExample5
import androidx.constraintlayout.compose.ScreenExample6
import androidx.constraintlayout.compose.ScreenExample7
import androidx.constraintlayout.compose.ScreenExample8
import androidx.constraintlayout.compose.ScreenExample9

class MainActivity : AppCompatActivity() {
    private var mFrameLayout: FrameLayout? = null
    private var composeNum = 19
    private var MAX = 33
    var map = HashMap<Int, String>();
    val debugServer = DebugServer()

    init {

        map.put(24, "scaleX/Y")
        map.put(25, "tanslationX/Y")
        map.put(26, "rotationZ")
        map.put(27, "rotationXY")
        map.put(28, "Cycle Scale")
        map.put(29, "Cycle TranslationXY")
        map.put(30, "Cycle RotationZ")
        map.put(31, "Cycle RotationXY")

        debugServer.start()
    }

    @ExperimentalMaterialApi
    private fun show(com: ComposeView) {
        com.setContent() {
            when (composeNum) {
                0 -> ScreenExample()
                1 -> ScreenExample()
                2 -> ScreenExample2()
                3 -> ScreenExample3()
                4 -> ScreenExample4()
                5 -> ScreenExample5()
                6 -> ScreenExample6()
                7 -> ScreenExample7()
                8 -> ScreenExample8()
                9 -> ScreenExample9()
                10 -> ScreenExample10()
                11 -> ScreenExample11()
                12 -> ScreenExample12()
                13 -> ScreenExample13()
                14 -> ScreenExample14()
                15 -> ScreenExample15()
                16 -> ScreenExample16()
                17 -> ScreenExample17()
                18 -> ScreenExample18()
                19 -> ScreenExample19()
                20 -> MotionExample1()
                21 -> MotionExample2()
                22 -> MotionExample3()
                23 -> MotionExample4()
                24 -> MotionExample5()
                25 -> MotionExample6()

                26 -> AttributesScale()
                27 -> AttributesTranslationXY()
                28 -> AttributesRotationZ()
                29 -> AttributesRotationXY()

                30 -> CycleScale()
                31 -> CycleTranslationXY()
                32 -> CycleRotationZ()
                33 -> CycleRotationXY()
                else -> {
                    composeNum = 0
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
        setCompose();
    }

    override fun onPause() {
        super.onPause()
        debugServer.stop()
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
        var sub = " example " + composeNum
        if (map.containsKey(composeNum)) {
            sub += " " + map.get(composeNum)
        }
        title = sub;
        findViewById<TextView>(R.id.layoutName).text = "! example " + composeNum;
        var com = ComposeView(this);
        mFrameLayout!!.addView(com)
        show(com)
    }

    @ExperimentalMaterialApi
    fun prev(view: View) {
        composeNum = (composeNum + MAX - 1) % MAX
        setCompose()
    }

    @ExperimentalMaterialApi
    fun next(view: View) {
        composeNum = (composeNum + 1) % MAX
        setCompose();
    }
}
