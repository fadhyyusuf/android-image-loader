package com.fy.imagepreview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fy.image_loader.ImageLoader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<android.widget.Button>(R.id.btn_load_image)
        button.setOnClickListener {
            ImageLoader.openImageLoader(
                this,
                "https://www.pawlovetreats.com/cdn/shop/articles/pembroke-welsh-corgi-puppy_600x.jpg?v=1628638716"
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}