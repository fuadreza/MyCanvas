package io.github.fuadreza.mycanvas.drawing_advance.model

import android.graphics.Color
import android.graphics.Paint

/**
 * Dibuat dengan kerjakerasbagaiquda oleh Shifu pada tanggal 13/08/2020.
 *
 */
class Brush {

    var paint : Paint = Paint()
    var posX : Float? = null
    var posY : Float? = null

    init {
        paint.let {
            //smooth edge
            it.isAntiAlias = true
            it.strokeJoin = Paint.Join.ROUND
            it.style = Paint.Style.STROKE

            //default color & stroke width
            it.color = Color.GREEN
            it.strokeWidth = 7f
        }
    }
}