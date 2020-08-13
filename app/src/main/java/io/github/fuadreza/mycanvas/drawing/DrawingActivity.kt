package io.github.fuadreza.mycanvas.drawing

import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import androidx.appcompat.app.AppCompatActivity
import io.github.fuadreza.mycanvas.R

/**
 * Dibuat dengan kerjakerasbagaiquda oleh Shifu pada tanggal 12/08/2020.
 *
 */
class DrawingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_drawing)
        //supportActionBar?.hide()

        val myCanvasView = MyCanvasView(this)
        supportActionBar?.hide()

        myCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        myCanvasView.contentDescription = getString(R.string.canvasContentDescription)

        setContentView(myCanvasView)
    }
}