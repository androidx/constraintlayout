package com.example.constraintlayout

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.compose.*
import androidx.constraintlayout.coreAndroid.PhoneState
import androidx.constraintlayout.tools.LinkServer
import com.google.accompanist.coil.rememberCoilPainter

class MainActivity : AppCompatActivity() {
    private var mFrameLayout: FrameLayout? = null
    private var composeNum = 37
    private var MAX = 39

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
            Button(modifier = Modifier.layoutId(id),
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
                20 -> ScreenExample20()

                21 -> MotionExample1()
                22 -> MotionExample2()
                23 -> MotionExample3()
                24 -> MotionExample4()
                25 -> MotionExample5()
                26 -> MotionExample6()

                27 -> AttributesScale()
                28 -> AttributesTranslationXY()
                29 -> AttributesRotationZ()
                30 -> AttributesRotationXY()

                31 -> CycleScale()
                32 -> CycleTranslationXY()
                33 -> CycleRotationZ()
                34 -> CycleRotationXY()

                35 -> MotionExample7()
                36 -> ScreenExample21()
                37 -> ResizeExample1()
                38 -> ScreenExample22()

                39 -> ExampleLayout()
                else -> {
                    composeNum = (composeNum + 39) % 39
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
        setCompose();
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
