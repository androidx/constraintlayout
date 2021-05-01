package com.example.constraintlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.constraintlayout.compose.Screen2
import androidx.constraintlayout.compose.ScreenExample3
import androidx.constraintlayout.compose.ScreenExample4

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScreenExample4()
        }
    }
}