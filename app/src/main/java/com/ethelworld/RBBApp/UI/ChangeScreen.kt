package com.ethelworld.RBBApp.UI

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.ethelworld.RBBApp.Presenter.ChangePasswordPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.ChangePasswordView
import com.ethelworld.RBBApp.tools.LoadingDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeScreen : ParentAppActivity(), ChangePasswordView.View {
    private lateinit var changePasswordPresenter: ChangePasswordPresenter

    private lateinit var oldPassword: TextInputEditText
    private lateinit var newPassword: TextInputEditText

    private lateinit var loadingDialog: LoadingDialog

    lateinit var rootView: View

    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.title = getString(R.string.change_password)

        oldPassword = findViewById(R.id.oldpassword)
        newPassword = findViewById(R.id.newpassword)

        loadingDialog = LoadingDialog(this)

        rootView = window.decorView.rootView

        changePasswordPresenter = ChangePasswordPresenter(this, applicationContext)

        val submit: MaterialButton = findViewById(R.id.submit)

        submit.setOnClickListener {
            if(loading) return@setOnClickListener
            loading = true
            hideKeyword()
            val oldPasswordString = oldPassword.text.toString()
            val newPasswordString = newPassword.text.toString()

            if(oldPasswordString.isEmpty()) {
                oldPassword.error = "Harus memasukan password lama"
                oldPassword.requestFocus()
                loading = false
                return@setOnClickListener
            }
            if(newPasswordString.isEmpty()) {
                newPassword.error = "Harus memasukan password baru"
                newPassword.requestFocus()
                loading = false
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.IO).launch {
                changePasswordPresenter.changePassword(newPasswordString, oldPasswordString)
            }
        }
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
        loading = false
        withContext(Dispatchers.Main) {
            loadingDialog.dismissDialog()
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        loading = false
        withContext(Dispatchers.Main) {
            errorShow(msg)
        }
    }

    private fun errorShow(msg: String?) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        with(builder){
            setTitle("Error")
            setMessage(msg)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override suspend fun onSuccess(token: String) {
        loading = false
        withContext(Dispatchers.Main) {
            val snackbar = Snackbar.make(rootView, token, Snackbar.LENGTH_LONG)
            snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
            snackbar.show()
        }
    }

    private fun successShow(token: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        with(builder){
            setTitle("Error")
            setMessage(token)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        changePasswordPresenter.onDestroy()
    }
}