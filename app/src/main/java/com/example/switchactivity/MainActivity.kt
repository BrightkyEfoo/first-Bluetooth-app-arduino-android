package com.example.switchactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var startButton:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity_layout)
        startButton = findViewById(R.id.startAllButton)
        startButton.setOnClickListener {
            switchActivity()
        }
    }
    private fun switchActivity(){
        var switcActivityIntent = Intent(this , MainActivity2::class.java)
        startActivity(switcActivityIntent)
    }
}

