package com.ethelworld.RBBApp.UI

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.ethelworld.RBBApp.Presenter.ForgotPasswordPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.ForgotPasswordView
import com.ethelworld.RBBApp.tools.LoadingDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ForgotPasswordScreen : ParentAppActivity(), ForgotPasswordView.View {
    private lateinit var forgotPasswordPresenter: ForgotPasswordPresenter

    private lateinit var emailInput: TextInputEditText
    private lateinit var codeInput: TextInputEditText
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var reNewPasswordInput: TextInputEditText
    private lateinit var usernameInput: TextInputEditText
    private lateinit var submitEmail: MaterialButton
    private lateinit var submitCode: MaterialButton
    private lateinit var submitPassword: MaterialButton
    private lateinit var resendCode: TextView

    private lateinit var layoutEmailViewStub: ViewStub
    private lateinit var layoutCodeViewStub: ViewStub
    private lateinit var layoutPasswordViewStub: ViewStub

    private lateinit var realLayoutEmailView: View
    private lateinit var realLayoutCodeView: View
    private lateinit var realLayoutPasswordView: View

    private lateinit var emailText: String
    private lateinit var codeText: String
    private lateinit var passwordText: String
    private lateinit var rePasswordText: String
    private lateinit var usernameText: String
    private lateinit var hashId: String

    private lateinit var loadingDialog: LoadingDialog

    private var loadingStat: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_screen)

        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.forgot_password)

        forgotPasswordPresenter = ForgotPasswordPresenter(this, applicationContext)

        layoutEmailViewStub     = findViewById(R.id.email_input)
        layoutCodeViewStub      = findViewById(R.id.code_confirm)
        layoutPasswordViewStub  = findViewById(R.id.password_input)

        realLayoutEmailView = layoutEmailViewStub.inflate()

        emailInput      = findViewById(R.id.email)
        submitEmail     = findViewById(R.id.submit_email)
        usernameInput   = findViewById(R.id.username)

        submitEmail.setOnClickListener {
            emailText = emailInput.text.toString()
            usernameText = usernameInput.text.toString()
            sendEmail(
                emailText,
                usernameText)
        }

        emailInput.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                emailText = emailInput.text.toString()
                usernameText = usernameInput.text.toString()
                sendEmail(
                    emailText,
                    usernameText)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        loadingDialog = LoadingDialog(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            loadingDialog.startLoadingDialog(false)
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            loadingDialog.dismissDialog()
            loadingStat = false
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

    private fun successAlert(msg: String?) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        with(builder) {
            setTitle("Berhasil")
            setMessage(msg)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                hideKeyword()
                finish()
            }
        }
        builder.show()
    }

    override suspend fun onSuccessSendEmail(result: JSONObject) {
        withContext(Dispatchers.Main) {
            realLayoutEmailView.visibility = View.GONE
            realLayoutCodeView = layoutCodeViewStub.inflate()

            codeInput   = findViewById(R.id.code_input)
            submitCode  = findViewById(R.id.submit_code)
            resendCode  = findViewById(R.id.resend_code)

            hashId   = result.getString("hashid")

            submitCode.setOnClickListener {
                codeText = codeInput.text.toString()
                sendCode(
                    codeText,
                    emailText,
                    hashId)
            }

            codeInput.setOnEditorActionListener { _, actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    codeText = codeInput.text.toString()
                    sendCode(
                        codeText,
                        emailText,
                        hashId)
                }
                return@setOnEditorActionListener false
            }

            resendCode.setOnClickListener {
                if(!loadingStat) {
                    loadingStat = true
                    hideKeyword()
                    CoroutineScope(Dispatchers.IO).launch {
                        forgotPasswordPresenter.resendCode(
                            emailText,
                            usernameText)
                    }
                }
            }
        }
    }

    override suspend fun onSuccessConfirmCode(result: JSONObject) {
        withContext(Dispatchers.Main) {
            realLayoutCodeView.visibility = View.GONE
            realLayoutPasswordView = layoutPasswordViewStub.inflate()

            newPasswordInput    = findViewById(R.id.newpassword)
            reNewPasswordInput  = findViewById(R.id.retype_newpassword)
            submitPassword      = findViewById(R.id.submit_password)

            hashId          = result.getString("hashid")
            submitPassword.setOnClickListener {
                passwordText    = newPasswordInput.text.toString()
                rePasswordText  = reNewPasswordInput.text.toString()

                if(passwordText == rePasswordText) {
                    changePassword(
                        passwordText,
                        rePasswordText,
                        emailText,
                        codeText,
                        hashId)
                } else {
                    reNewPasswordInput.error = "password tidak sama"
                }
            }

            reNewPasswordInput.setOnEditorActionListener { _, actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    passwordText    = newPasswordInput.text.toString()
                    rePasswordText  = reNewPasswordInput.text.toString()

                    if(passwordText == rePasswordText) {
                        changePassword(
                            passwordText,
                            rePasswordText,
                            emailText,
                            codeText,
                            hashId)
                    } else {
                        reNewPasswordInput.error = "password tidak sama"
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private fun sendCode(
        code: String,
        email: String,
        hashid: String){
        if(!loadingStat) {
            loadingStat = true
            hideKeyword()
            CoroutineScope(Dispatchers.IO).launch {
                forgotPasswordPresenter.sendConfirmCode(
                    code,
                    email,
                    hashid)
            }
        }
    }

    private fun sendEmail(email: String, username: String) {
        if(!loadingStat) {
            loadingStat = true
            hideKeyword()
            CoroutineScope(Dispatchers.IO).launch {
                forgotPasswordPresenter.sendEmailChangePassword(email, username)
            }
        }
    }

    private fun changePassword(
        password: String,
        rePassword: String,
        email: String,
        code: String,
        hashid: String) {
        if (!loadingStat) {
            loadingStat = true
            hideKeyword()
            CoroutineScope(Dispatchers.IO).launch {
                forgotPasswordPresenter.sendChangePassword(
                    password,
                    rePassword,
                    email,
                    code,
                    hashid
                )
            }
        }
    }

    override suspend fun onSuccessChangePassword(result: JSONObject) {
        withContext(Dispatchers.Main) {
            val msg = result.getString("msg")
            successAlert(msg)
        }
    }

    override suspend fun onSuccessResendCode(result: JSONObject) {
        withContext(Dispatchers.Main) {
            hashId  = result.getString("hashid")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        forgotPasswordPresenter.onDestroy()
    }
}