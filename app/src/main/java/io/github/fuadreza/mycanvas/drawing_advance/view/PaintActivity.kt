package io.github.fuadreza.mycanvas.drawing_advance.view

import android.content.ContentValues
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.colorpicker.ColorPickerDialog
import com.android.colorpicker.ColorPickerSwatch
import io.github.fuadreza.mycanvas.R
import kotlinx.android.synthetic.main.activity_paint.*
import kotlinx.android.synthetic.main.brush_width_view.*
import kotlinx.android.synthetic.main.brush_width_view.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


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
        supportActionBar?.hide()

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

        /*fab.setOnClickListener { _ ->
            val optionsBehavior = BottomSheetBehavior.from(options)
            optionsBehavior?.let {
                it.state = if (it.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
            }
        }*/

        btn_color.setOnClickListener { _ ->
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

        btn_clear.setOnClickListener { _ ->
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

        btn_brush.setOnClickListener { _ ->
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

        btn_next.setOnClickListener {
            canvas?.let {
                val bitmap = Bitmap.createBitmap(canvas.bitMap)
                val filename = "contoh"
                //writeBitmapToMemory(filename, bitmap) -- masih error (Readonly files | Not found)
                saveImage(bitmap, filename)
            }
        }
    }

    fun writeBitmapToMemory(filename: String, bitmap: Bitmap) {
        val path = getExternalFilesDir(null)
        val file = File(path, filename)
        val fos = FileOutputStream(file)
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            //PNG is lossless so it will ignore 100 quality
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun saveImage(bitmap: Bitmap, name: String) {
        val fos: OutputStream?
        fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.png")
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Elea")
            val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            resolver.openOutputStream(imageUri!!)
        } else {
            val imagesDir: String =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString()
            val image = File(imagesDir, "$name.jpg")
            FileOutputStream(image)
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        Objects.requireNonNull(fos)?.close()
    }
}