package com.ethelworld.RBBApp.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.ethelworld.RBBApp.Database.EthelDBHelper
import com.ethelworld.RBBApp.Presenter.ContactRecoveryPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.View.ContactRecoveryView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.google.android.material.button.MaterialButton

class ContactRecoveryScreen : AppCompatActivity(), ContactRecoveryView.View {
    private lateinit var contactRecoveryPresenter: ContactRecoveryPresenter

    private lateinit var contactRecoveryProgress: ProgressBar
    private lateinit var labelProgressText: TextView
    private lateinit var buttonProgress: MaterialButton

    private lateinit var mainScreen: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_recovery_screen)

        val ethelDBHelper = EthelDBHelper(applicationContext)

        contactRecoveryPresenter = ContactRecoveryPresenter(this, applicationContext, ethelDBHelper)

        contactRecoveryProgress = findViewById(R.id.progress_contact_recovery)
        labelProgressText = findViewById(R.id.status_label)
        buttonProgress = findViewById(R.id.done)
        buttonProgress.visibility = View.GONE

        contactRecoveryPresenter.contactRecovery()
        buttonProgress.setOnClickListener {
            startActivity(mainScreen)
            finish()
        }

        mainScreen = Intent(this, MainScreen::class.java)
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun progressLoading(progress: Int?, status: String) {
        contactRecoveryProgress.progress = progress!!
        labelProgressText.text = status
    }

    override fun showError(msg: String?) {

    }

    override fun onSuccess() {
        Authentication(applicationContext).save(Authentication.RECOVERY, true)
        buttonProgress.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        contactRecoveryPresenter.onDestroy()
    }
}