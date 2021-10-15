package com.ethelworld.RBBApp.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ethelworld.RBBApp.R

class DetailAds : AppCompatActivity() {
    private lateinit var imageAds: ImageView
    private lateinit var imageClose: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_ads)

        val url     = intent.extras?.getString("url")
        imageAds    = findViewById(R.id.ads_image)
        imageClose  = findViewById(R.id.close)

        Glide
            .with(this)
            .load(url)
            .fitCenter()
            .placeholder(R.drawable.rbb)
            .into(imageAds)

        imageClose.setOnClickListener {
            onBackPressed()
        }
    }
}