package io.github.fuadreza.mycanvas.drawing_advance.view

import android.graphics.*
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.android.colorpicker.ColorPickerDialog
import com.android.colorpicker.ColorPickerSwatch
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.github.fuadreza.mycanvas.R

import kotlinx.android.synthetic.main.activity_drawing.view.*
import kotlinx.android.synthetic.main.activity_paint.*
import kotlinx.android.synthetic.main.brush_width_view.*
import kotlinx.android.synthetic.main.brush_width_view.view.*
import kotlinx.android.synthetic.main.options_bottom_sheet.view.*

/**
 * Dibuat dengan kerjakerasbagaiquda oleh Shifu pada tanggal 13/08/2020.
 *
 */
class PaintActivity: AppCompatActivity(), ColorPickerSwatch.OnColorSelectedListener, SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, user: Boolean) {
        // cant have 0 size
        val size = if (progress <= 0) 1f else progress.toFloat()
        seekbar?.rootView?.brushWidthProgress?.text = size.toString()
        drawBrushWidthCircle(size, seekbar?.rootView)
        this.canvas.setBrushWidth(size)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    override fun onColorSelected(color: Int) {
        canvas.setPaintColor(color)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)

        setupOptionsClickListeners()

        savedInstanceState?.let {
            val bitmapBytes = it.getByteArray("bitmap")
            val bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes!!.size)
            val brushWidth = it.getFloat("brush_width")
            val brushColor = it.getInt("brush_color")

            canvas.drawSavedBitMap(bitmap)
            canvas.setPaintColor(brushColor)
            canvas.setBrushWidth(brushWidth)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        canvas?.let {
            // Bundle/Intent IPC only allow for 1mb, saving locally to reload on rotation.
            val bitmap = Bitmap.createBitmap(canvas.bitMap)
            val bitMapBytes = canvas.saveBitMap(bitmap)
            bitMapBytes?.let {
                val brushWidth = canvas.getCurrentBrushSize()
                val brushColor = canvas.getCurrentColor()

                outState.putByteArray("bitmap", bitMapBytes)
                outState.putFloat("brush_width", brushWidth)
                outState.putInt("brush_color", brushColor)
            }
        }
    }

    private fun drawBrushWidthCircle(size: Float, view: View?) {
        view?.let {
            val preview = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888)
            val tempCanvas = Canvas(preview)
            val tempPaint = Paint()
            tempPaint.style = Paint.Style.FILL
            tempPaint.color = canvas.getCurrentColor()
            tempCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
            tempCanvas.drawCircle(75f, 75f, size, tempPaint)
            view.brush_width_circle?.setImageBitmap(preview)
        }
    }

    private fun drawBrushWidthCircle(size: Float, dialog: AlertDialog) {
        val preview = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(preview)
        val tempPaint = Paint()
        tempPaint.style = Paint.Style.FILL
        tempPaint.color = canvas.getCurrentColor()
        tempCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        tempCanvas.drawCircle(75f, 75f, size, tempPaint)
        dialog.brush_width_circle?.setImageBitmap(preview)
    }

    private fun setupOptionsClickListeners() {

        fab.setOnClickListener { _ ->
            val optionsBehavior = BottomSheetBehavior.from(options)
            optionsBehavior?.let {
                it.state = if (it.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
            }
        }

        options.modifyColorBtn?.setOnClickListener { view ->
            canvas?.let {
                val colorPicker = ColorPickerDialog()
                val allColors: IntArray = canvas.getAllColors()
                colorPicker.initialize(R.string.color_picker_title,
                    allColors,
                    canvas.getCurrentColor(),
                    5,
                    allColors.size)
                colorPicker.show(fragmentManager, "colorpicker")
                colorPicker.setOnColorSelectedListener(this)
            }
        }

        options.clearCanvasBtn.setOnClickListener { _ ->
            val alert = AlertDialog.Builder(this)
                .setTitle(R.string.dialog_clear_canvas)
                .setPositiveButton(android.R.string.yes) {
                        dialog, whichButton ->
                    canvas.clearCanvas()
                }
                .setNegativeButton(android.R.string.no) {
                        dialog, whichButton -> dialog.dismiss()
                }
                .setMessage(R.string.dialog_are_you_sure)
                .create()
            alert.show()
        }

        options.modifyBrushWidthBtn.setOnClickListener { _ ->
            val alert = AlertDialog.Builder(this)
                .setTitle(R.string.brush_width_change_title)
                .setView(R.layout.brush_width_view)
                .create()
            alert.show()
            alert.brushWidthSeekBar.progress = canvas.getCurrentBrushSize().toInt()
            alert.brushWidthProgress.text = canvas.getCurrentBrushSize().toString()
            alert.brushWidthSeekBar.setOnSeekBarChangeListener(this)
            drawBrushWidthCircle(canvas.getCurrentBrushSize(), alert)
        }

    }
}