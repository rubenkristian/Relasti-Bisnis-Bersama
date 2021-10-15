package com.ethelworld.RBBApp.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.ethelworld.RBBApp.R

class SettingScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.title = getString(R.string.settings)

        val changePassword = findViewById<CardView>(R.id.tac_layout)

        changePassword.setOnClickListener {
            val versionActivity = Intent(this, ChangeScreen::class.java)

            startActivity(versionActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }
}