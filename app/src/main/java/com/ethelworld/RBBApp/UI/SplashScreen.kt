package com.ethelworld.RBBApp.UI

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.ethelworld.RBBApp.Presenter.SplashScreenPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.View.SplashScreenView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*

class SplashScreen : AppCompatActivity(), SplashScreenView.View {
    private lateinit var splashScreenPresenter: SplashScreenPresenter
    private lateinit var homeScreen: Intent
    private lateinit var loginScreen: Intent
    private lateinit var signupScreen: Intent
    private lateinit var tacScreen: Intent
    private lateinit var recoveryScreen: Intent
    private lateinit var paymentInfoScreen: Intent
    private lateinit var versionUpdateScreen: Intent

    private lateinit var splashViewStub: ViewStub
    private lateinit var buttonLayout: View

    private lateinit var signinButton: MaterialButton
    private lateinit var signupButton: MaterialButton

    private lateinit var splashImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashScreenPresenter   = SplashScreenPresenter(this, applicationContext)
        tacScreen               = Intent(this, FirstTAC::class.java)
        homeScreen              = Intent(this, MainScreen::class.java)
        loginScreen             = Intent(this, LoginScreen::class.java)
        signupScreen            = Intent(this, SelfRegisterScreen::class.java)
        recoveryScreen          = Intent(this, ContactRecoveryScreen::class.java)
        paymentInfoScreen       = Intent(this, PaymentInfoScreen::class.java)
        versionUpdateScreen     = Intent(this, VersionUpdateScreen::class.java)

        splashViewStub  = findViewById(R.id.button_content)
        splashImage     = findViewById(R.id.background_splash)

        Glide.with(this).load(R.drawable.splash_screen).into(splashImage)

        CoroutineScope(Dispatchers.IO).launch {
            splashScreenPresenter.authentication()
        }
    }

    private fun slideUp(view: View) {
        Glide.with(this).load(R.drawable.bg_splash_screen).into(splashImage)

        view.visibility = View.VISIBLE
//        val animate = TranslateAnimation(0F, 0F, view.height.toFloat(), 0F)
        val animate     = TranslateAnimation(0F, 0F, 1000F, 0F)

        animate.duration = 1000
        animate.fillAfter = true

        view.startAnimation(animate)
    }

    override suspend fun onLogged(updated: Boolean, versionName: String) {
        withContext(Dispatchers.Main) {
//            if(Authentication(applicationContext).isRecoveryDone()) {
            if(updated) {
                updateNotification(versionName)
            } else {
                startActivity(homeScreen)
                finish()
            }
//            } else {
//                startActivity(recoveryScreen)
//                finish()
//            }
        }
    }

    private fun updateNotification(versionName: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)

        with(builder) {
            setTitle("Pembaharuan")
            setMessage("Pembaharuan dengan versi $versionName telah tersedia di PlayStore")
            setPositiveButton("Perbaharui") { _, _ ->
                val appId = applicationContext.packageName

                val updateView = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appId"))

                startActivity(updateView)
                finish()
            }
            setNegativeButton("Nanti") { _, _ ->
                startActivity(homeScreen)
                finish()
            }
        }

        builder.setCancelable(false)
        builder.show()
    }

    override suspend fun onNoLogged() {
        withContext(Dispatchers.Main) {
            buttonLayout = splashViewStub.inflate()
            signinButton = findViewById(R.id.sign_in)
            signupButton = findViewById(R.id.sign_up)

            buttonLayout.visibility = View.INVISIBLE

            signinButton.setOnClickListener {
                startActivity(loginScreen)
            }

            signupButton.setOnClickListener {
                val id = Authentication(applicationContext).getValueLong(Authentication.IDLASTREGISTER)
                if(id > 0) {
                    paymentInfoScreen.putExtra("id", id)

                    startActivity(paymentInfoScreen)
                } else {
                    startActivity(signupScreen)
                }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                slideUp(buttonLayout)
            }, 1000)
        }
    }

    override suspend fun onTACNotSigned() {
        withContext(Dispatchers.Main) {
            startActivity(tacScreen)
            finish()
        }
    }

    override suspend fun onVersionUpdate(versionName: String) {
        withContext(Dispatchers.Main) {
            startActivity(versionUpdateScreen)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        splashScreenPresenter.onDestroy()
    }
}