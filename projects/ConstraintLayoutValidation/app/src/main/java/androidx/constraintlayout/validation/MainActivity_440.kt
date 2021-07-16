package androidx.constraintlayout.validation;

import android.os.Bundle
import android.support.constraintlayout.validation.R
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity_440 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_440)

        val content = findViewById<View>(R.id.container)
        val group = findViewById<Group>(R.id.grpTest)
        val text = findViewById<TextView>(R.id.txtTest)
        val etext = findViewById<EditText>(R.id.editTextTextPersonName)

        ViewCompat.setOnApplyWindowInsetsListener(content.rootView) { v, insets ->
            if (insets.isVisible(WindowInsetsCompat.Type.ime())) {
                Log.e("TestActivity", "Keyboard showing")
                // This does not work alone, if you add a requestLayout it will start working, though
                group.visibility = View.VISIBLE
//                content.requestLayout()
            } else {
                Log.e("TestActivity", "Keyboard hidden")
                // This does not work alone, if you add a requestLayout it will start working, though
                group.visibility = View.GONE
//                content.requestLayout()
            }
            insets
        }
    }
}