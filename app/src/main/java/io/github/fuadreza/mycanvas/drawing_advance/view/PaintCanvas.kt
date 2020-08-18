package io.github.fuadreza.mycanvas.drawing_advance.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import io.github.fuadreza.mycanvas.drawing_advance.viewmodel.PaintViewModel

/**
 * Dibuat dengan kerjakerasbagaiquda oleh Shifu pada tanggal 13/08/2020.
 *
 */
class PaintCanvas(context: Context, attributes: AttributeSet) : View(context, attributes) {

    var canvas: Canvas = Canvas()
    var drawPath = Path()
    lateinit var bitMap: Bitmap
    var bitMapPaint: Paint = Paint(Paint.DITHER_FLAG)
    var viewModel: PaintViewModel = PaintViewModel()

    var strokePaint: Paint = Paint()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::bitMap.isInitialized) bitMap.recycle()
        bitMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitMap)
        canvas.drawColor(Color.WHITE)
        /*if(::bitMap.isInitialized){
            //each pixel is 4 bytes
            bitMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitMap)
        }else {
            bitMap = Bitmap.createScaledBitmap(bitMap, w, h, true)
            canvas = Canvas(bitMap)
            canvas.drawBitmap(bitMap, 0f, 0f, bitMapPaint)
        }*/
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            super.onDraw(canvas)
            bitMapPaint.color = Color.WHITE
            canvas.drawBitmap(bitMap, 0f, 0f, bitMapPaint)
            canvas.drawPath(viewModel.getBrushPath(), viewModel.getPaint())

            //border
            val radius = 10f
            strokePaint.style = Paint.Style.STROKE
            strokePaint.color = Color.GRAY
            strokePaint.strokeWidth = 10f

            canvas.drawRoundRect(0f, 0f, 0f, 0f, radius, radius, strokePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            super.onTouchEvent(event)
            val posX = event.x
            val posY = event.y

            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    viewModel.startPainting(posX, posY)
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    canvas.drawPath(viewModel.liftBrush(), viewModel.getPaint())
                    viewModel.resetBrushPath()
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    viewModel.moveBrush(posX, posY)
                    invalidate()
                }
                else -> {
                    // extra
                }
            }
        }
        return true
    }

    fun drawSavedBitMap(bitmap: Bitmap){
        bitMap = bitmap
    }

    fun setPaintColor(color: Int) {
        viewModel.setPaintColor(color)
    }

    fun getAllColors() = viewModel.getAllColors(this.context).toIntArray()

    fun getCurrentColor() = viewModel.getPaint().color

    fun clearCanvas() {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
    }

    fun setBrushWidth(width: Float){
        viewModel.setBrushWidth(width)
    }

    fun getCurrentBrushSize() = viewModel.getBrushWidth()

    fun saveBitMap(bitmap: Bitmap?): ByteArray? {
        return viewModel.bitMapToByteArray(bitmap)
    }

    fun getPaint() = viewModel.getPaint()
}
