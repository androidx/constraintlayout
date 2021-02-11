package androidx.constraintlayout.experiments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.helper.widget.CircularFlow

class CircularFlowDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circular_flow_demo)

        findViewById<View>(R.id.view6).setOnClickListener {
            findViewById<CircularFlow>(R.id.circularFlow).addViewToCircularFlow(
                it, 130, 160F
            )
        }

        findViewById<View>(R.id.view7).setOnClickListener {
            findViewById<CircularFlow>(R.id.circularFlow).addViewToCircularFlow(
                it, 140, 200F
            )
        }

        findViewById<View>(R.id.view8).setOnClickListener {
            findViewById<CircularFlow>(R.id.circularFlow).addViewToCircularFlow(
                it, 150, 240F
            )
        }

        findViewById<View>(R.id.view2).setOnClickListener {
            findViewById<CircularFlow>(R.id.circularFlow).removeViewFromCircularFlow(
                it
            )
        }
    }
}