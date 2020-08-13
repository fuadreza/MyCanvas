package io.github.fuadreza.mycanvas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.fuadreza.mycanvas.drawing.DrawingActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_drawing.setOnClickListener {
            startActivity(Intent(this, DrawingActivity::class.java))
        }
    }
}