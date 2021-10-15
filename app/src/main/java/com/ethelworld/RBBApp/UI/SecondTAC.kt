package com.ethelworld.RBBApp.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.ethelworld.RBBApp.R
import com.google.android.material.button.MaterialButton

class SecondTAC : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_tac)

        val termAndCondition: WebView = findViewById(R.id.termandcondition)
        termAndCondition.loadUrl("file:///android_asset/term.html")

        val back = findViewById<MaterialButton>(R.id.back)

        back.setOnClickListener{
            onBackPressed()
        }

        val toMainScreen = findViewById<MaterialButton>(R.id.tomain)

        toMainScreen.setOnClickListener{
            val mainscreen = Intent(this, MainScreen::class.java)

            mainscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(mainscreen)

            finish()
        }
    }
}