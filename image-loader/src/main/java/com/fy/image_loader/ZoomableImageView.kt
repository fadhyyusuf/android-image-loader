package com.fy.image_loader

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max
import kotlin.math.min

class ZoomableImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val matrix = Matrix()
    private val savedMatrix = Matrix()
    private var scale = 1f
    private var minScale = 1f
    private var maxScale = 5f

    private enum class Mode { NONE, DRAG, ZOOM }
    private var mode = Mode.NONE

    private val last = PointF()
    private val start = PointF()
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())

    init {
        scaleType = ScaleType.MATRIX
        imageMatrix = matrix
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        post { centerAndFitImage() }
    }

    private fun centerAndFitImage() {
        drawable?.let { d ->
            val viewWidth = width.toFloat()
            val viewHeight = height.toFloat()
            val imageWidth = d.intrinsicWidth.toFloat()
            val imageHeight = d.intrinsicHeight.toFloat()

            val scale = min(viewWidth / imageWidth, viewHeight / imageHeight)
            minScale = scale
            this.scale = scale

            val dx = (viewWidth - imageWidth * scale) / 2f
            val dy = (viewHeight - imageHeight * scale) / 2f

            matrix.setScale(scale, scale)
            matrix.postTranslate(dx, dy)
            imageMatrix = matrix
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        val currentPoint = PointF(event.x, event.y)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start.set(currentPoint)
                mode = Mode.DRAG
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                savedMatrix.set(matrix)
                mode = Mode.ZOOM
            }

            MotionEvent.ACTION_MOVE -> {
                if (mode == Mode.DRAG) {
                    val dx = currentPoint.x - start.x
                    val dy = currentPoint.y - start.y
                    matrix.set(savedMatrix)
                    matrix.postTranslate(dx, dy)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = Mode.NONE
            }
        }

        imageMatrix = matrix
        return true
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val newScale = (scale * scaleFactor).coerceIn(minScale, maxScale)

            val factor = newScale / scale
            scale = newScale

            matrix.postScale(factor, factor, detector.focusX, detector.focusY)
            return true
        }
    }
}
