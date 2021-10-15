package com.ethelworld.RBBApp.UI

import android.content.Intent
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity

class AboutScreen : ParentAppActivity() {
    private lateinit var tacScreen: Intent
    private lateinit var versionScreen: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.title = getString(R.string.about)

        val tac: CardView = findViewById(R.id.tac_layout)
        val version: CardView = findViewById(R.id.version_layout)

        tacScreen = Intent(this, TACScreen::class.java)
        versionScreen   = Intent(this, VersionScreen::class.java)

        tac.setOnClickListener {
            startActivity(tacScreen)
        }

        version.setOnClickListener {
            startActivity(versionScreen)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}