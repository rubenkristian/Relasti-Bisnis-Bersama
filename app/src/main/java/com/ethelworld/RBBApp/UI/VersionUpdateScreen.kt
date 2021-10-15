package com.ethelworld.RBBApp.UI

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ethelworld.RBBApp.R

class VersionUpdateScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version_update_screen)

        val updateBtn = findViewById<Button>(R.id.update)

        updateBtn.setOnClickListener {
            val appId = applicationContext.packageName

            val updateView = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$appId"))

            startActivity(updateView)
            finish()
        }
    }
}