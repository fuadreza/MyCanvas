package io.github.fuadreza.mycanvas.drawing_advance.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Path
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import io.github.fuadreza.mycanvas.R
import io.github.fuadreza.mycanvas.drawing_advance.model.Brush
import java.io.ByteArrayOutputStream

/**
 * Dibuat dengan kerjakerasbagaiquda oleh Shifu pada tanggal 13/08/2020.
 *
 */
class PaintViewModel : ViewModel() {

    private var brush = Brush()
    private var brushPath = Path()
    private var TOUCH_TOLERANCE = 4

    fun startPainting(posX: Float, posY: Float){
        brushPath.reset()
        brushPath.moveTo(posX, posY)
        brush.posX = posX
        brush.posY = posY
    }

    fun moveBrush(posX: Float, posY: Float){
        val currentX = brush.posX ?: 0f
        val currentY = brush.posY ?: 0f
        val dimenX = Math.abs(posX - currentX)
        val dimenY = Math.abs(posY - currentY)
        if(dimenX >= TOUCH_TOLERANCE || dimenY >= TOUCH_TOLERANCE) {
            brushPath.quadTo(currentX, currentY, (posX + currentX)/2, (posY + currentY)/2)
            brush.posX = posX
            brush.posY = posY
        }
    }

    fun liftBrush(): Path {
        val endX = brush.posX ?: 0f
        val endY = brush.posY ?: 0f
        brushPath.lineTo(endX, endY)
        return brushPath
//        brushPath.reset()
    }

    fun resetBrushPath(){
        brushPath.reset()
    }

    fun getBrushPath() = brushPath

    fun getPaint() = brush.paint

    fun setPaintColor(color: Int){
        brush.paint.color = color
    }

    fun getAllColors(context: Context): Array<Int>{
        return arrayOf(
            ContextCompat.getColor(context,R.color.red),
            ContextCompat.getColor(context,R.color.pink),
            ContextCompat.getColor(context,R.color.purple),
            ContextCompat.getColor(context,R.color.deep_purple),
            ContextCompat.getColor(context,R.color.indigo),
            ContextCompat.getColor(context,R.color.blue),
            ContextCompat.getColor(context,R.color.light_blue),
            ContextCompat.getColor(context,R.color.cyan),
            ContextCompat.getColor(context,R.color.teal),
            ContextCompat.getColor(context,R.color.green),
            ContextCompat.getColor(context,R.color.light_green),
            ContextCompat.getColor(context,R.color.lime),
            ContextCompat.getColor(context,R.color.yellow),
            ContextCompat.getColor(context,R.color.amber),
            ContextCompat.getColor(context,R.color.orange),
            ContextCompat.getColor(context,R.color.deep_orange),
            ContextCompat.getColor(context,R.color.brown),
            ContextCompat.getColor(context,R.color.white),
            ContextCompat.getColor(context,R.color.blue_grey),
            ContextCompat.getColor(context,R.color.black)
        )
    }

    fun setBrushWidth(width: Float){
        brush.paint.strokeWidth = width
    }

    fun getBrushWidth() = brush.paint.strokeWidth

    fun bitMapToByteArray(bitmap: Bitmap?): ByteArray? {
        bitmap?.let {
            val byteStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
            return byteStream.toByteArray()
        }
        return null
    }
}