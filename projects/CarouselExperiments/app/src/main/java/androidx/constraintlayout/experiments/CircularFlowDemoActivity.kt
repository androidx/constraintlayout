package androidx.constraintlayout.experiments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.helper.widget.CircularFlow

class CircularFlowDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circular_flow_demo)

        findViewById<View>(R.id.view6).setOnClickListener {
            findViewById<CircularFlow>(R.id.circularFlow).addViewToCircularFlow(
                it, 130, 160F
            )
            Toast.makeText(
                it.context,
                "addViewToCircularFlow - Radius: " + 130 + " Angle: " + 160,
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(R.id.view7).setOnClickListener {
            findViewById<CircularFlow>(R.id.circularFlow).addViewToCircularFlow(
                it, 140, 200F
            )
            Toast.makeText(
                it.context,
                "AddViewToCircularFlow - Radius: " + 140 + " Angle: " + 200,
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(R.id.view8).setOnClickListener {
            findViewById<CircularFlow>(R.id.circularFlow).addViewToCircularFlow(
                it, 150, 240F
            )
            Toast.makeText(
                it.context,
                "addViewToCircularFlow - Radius: " + 150 + " Angle: " + 240,
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(R.id.view2).setOnClickListener {
            findViewById<CircularFlow>(R.id.circularFlow).updateAngle(
                it, 90F
            )
            Toast.makeText(it.context, "UpdateAngle Angle: " + 90, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.view3).setOnClickListener {
            findViewById<CircularFlow>(R.id.circularFlow).updateRadius(
                it, 150
            )
            Toast.makeText(it.context, "UpdateRadius Radius: " + 150, Toast.LENGTH_SHORT).show()

        }

        findViewById<View>(R.id.view4).setOnClickListener {
            findViewById<CircularFlow>(R.id.circularFlow).updateReference(
                it, 160, 135F
            )
            Toast.makeText(
                it.context,
                "UpdateReference - Radius: " + 160 + " Angle: " + 135,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}