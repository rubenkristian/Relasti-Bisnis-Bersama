package com.ethelworld.RBBApp.UI

import android.content.Intent
import android.os.Bundle
import android.view.ViewStub
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.ethelworld.RBBApp.Presenter.LoginPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.LoginView
import com.ethelworld.RBBApp.tools.LoadingDialog
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import test.jinesh.captchaimageviewlib.CaptchaImageView

class LoginScreen : ParentAppActivity(), LoginView.View {
    private lateinit var loginPresenter: LoginPresenter
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var authentication: Authentication

    // input
    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var captchaCodeInput: TextInputEditText

    private lateinit var imageCaptcha: CaptchaImageView

    private lateinit var forgotPassword: TextView

    private lateinit var loginViewStub: ViewStub

    private lateinit var mainScreen: Intent
    private lateinit var firstTAC: Intent
    private lateinit var recoveryScreen: Intent
    private lateinit var forgotPasswordScreen: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen2)
        loginPresenter = LoginPresenter(this, applicationContext)

        authentication = Authentication(applicationContext)

        mainScreen              = Intent(this, MainScreen::class.java)
        firstTAC                = Intent(this, FirstTAC::class.java)
        recoveryScreen          = Intent(this, ContactRecoveryScreen::class.java)
        forgotPasswordScreen    = Intent(this, ForgotPasswordScreen::class.java)

        val splashImage = findViewById<ImageView>(R.id.background_splash)

        Glide.with(this).load(R.drawable.bg_splash_screen).into(splashImage)

        loadView()
    }

    private fun loadView() {
        loginViewStub = findViewById(R.id.login)

        loginViewStub.inflate()

        captchaCodeInput = findViewById(R.id.captcha)
        loadingDialog = LoadingDialog(this)

        usernameInput = findViewById(R.id.username)
        passwordInput = findViewById(R.id.password)

        forgotPassword = findViewById(R.id.forgot_password)

        imageCaptcha = findViewById(R.id.image_captcha)
        val refreshCaptcha = findViewById<ImageButton>(R.id.refresh_captcha)

        refreshCaptcha.setOnClickListener {
            imageCaptcha.regenerate()
        }

        forgotPassword.setOnClickListener {
            startActivity(forgotPasswordScreen)
        }

        findViewById<MaterialButton>(R.id.signin).setOnClickListener {
            submitLogin()
        }

        captchaCodeInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                submitLogin()
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })
    }

    private fun submitLogin() {
        hideKeyword()
        val username: String = usernameInput.text.toString()
        val password: String = passwordInput.text.toString()
        val captcha: String  = captchaCodeInput.text.toString()

        if(username.trim().isEmpty()) {
            usernameInput.error = "Username harus diisi."
            usernameInput.requestFocus()
        } else if(password.trim().isEmpty()) {
            passwordInput.error = "Password harus diisi."
            passwordInput.requestFocus()
        } else if(captcha.trim().isEmpty()) {
            captchaCodeInput.error = "Captcha harus diisi."
            captchaCodeInput.requestFocus()
        } else  {
            if(imageCaptcha.captchaCode == captcha.trim()) {
                CoroutineScope(Dispatchers.IO).launch {
                    loginPresenter.authentication(username, password)
                }
            } else {
                captchaCodeInput.error = "Captcha tidak cocok."
                captchaCodeInput.requestFocus()
            }
        }
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            loadingDialog.startLoadingDialog(false)
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            loadingDialog.dismissDialog()
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            errorAlert(msg)
        }
    }

    private fun errorAlert(msg: String?) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        with(builder) {
            setTitle("Error")
            setMessage(msg)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override suspend fun onSuccess(token: String) {
        withContext(Dispatchers.Main) {
            authentication.save(Authentication.TOKEN, token)

            if (authentication.isLogged()) {
                if (authentication.isTACAgreed()) {
                    mainScreen.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(mainScreen)
                } else {
                    firstTAC.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(firstTAC)
                }
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.onDestroy()
    }
}