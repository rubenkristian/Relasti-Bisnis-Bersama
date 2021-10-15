package com.ethelworld.RBBApp.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.ethelworld.RBBApp.R

class TACScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t_a_c_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.title = getString(R.string.syarat_dan_ketentuan)

        val termAndCondition: WebView = findViewById(R.id.termandcondition)

        termAndCondition.loadUrl("file:///android_asset/tac.html")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }
}