package com.fy.image_loader

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.StrictMode
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import java.io.File
import java.net.URL

object ImageLoader {

    fun openImageLoader(activity: Activity, pathOrUrl: String) {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

        // Enable networking on main thread for simplicity (not recommended for prod)
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        // Overlay container
        val overlay = FrameLayout(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#CC000000"))
        }

        // ImageView with zoom support
        val imageView = ZoomableImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )
            scaleType = ImageView.ScaleType.MATRIX
        }

        // Load Bitmap
        // Inside loadNativeZoomableImage(...)
        val bitmap = try {
            when {
                pathOrUrl.startsWith("http") -> {
                    val url = URL(pathOrUrl)
                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
                }

                pathOrUrl.startsWith("content://") -> {
                    val uri = android.net.Uri.parse(pathOrUrl)
                    activity.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }

                else -> {
                    BitmapFactory.decodeFile(File(pathOrUrl).absolutePath)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }


        if (bitmap == null) {
            // show nothing if failed to load
            return
        }

        imageView.setImageBitmap(bitmap)

        // Close button
        val closeButton = ImageButton(activity).apply {
            layoutParams = FrameLayout.LayoutParams(100, 100, Gravity.TOP or Gravity.END).apply {
                setMargins(32, 32, 32, 32)
            }
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setBackgroundColor(Color.TRANSPARENT)
            setColorFilter(Color.WHITE)
        }

        closeButton.setOnClickListener {
            rootView.removeView(overlay)
        }

        overlay.addView(imageView)
        overlay.addView(closeButton)
        rootView.addView(overlay)
    }
}
