package com.ethelworld.RBBApp.UI

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ethelworld.RBBApp.Presenter.WithdrawConfirmPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.View.WithdrawConfirmView
import com.ethelworld.RBBApp.tools.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class WithdrawConfirm : AppCompatActivity(), WithdrawConfirmView.View {
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var withdrawConfirmPresenter: WithdrawConfirmPresenter

    private lateinit var codeTextInput: TextInputEditText
    private lateinit var submitButton: Button

    private var idWithdraw: Int? = 0
    private var msg: String? = null
    private var withdraw: String? = null

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_confirm)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Withdraw Confirm"

        loadingDialog = LoadingDialog(this)

        idWithdraw  = intent.extras?.getInt("id")
        msg         = intent.extras?.getString("msg")
//        withdraw    = intent.extras?.getString("withdraw")

        withdrawConfirmPresenter = WithdrawConfirmPresenter(this, applicationContext)

        codeTextInput   = findViewById(R.id.code_input)
        submitButton    = findViewById(R.id.submit_code)

        submitButton.setOnClickListener {
            submitConfirm()
        }

        rootView = window.decorView.rootView
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun hideKeyword() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun submitConfirm() {
        val code = codeTextInput.text.toString()

        hideKeyword()

        CoroutineScope(Dispatchers.IO).launch {
            withdrawConfirmPresenter.submitCodeConfirm(idWithdraw, code)
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
            val snackbar = Snackbar.make(rootView, msg?:"Terjadi kesalahan.", Snackbar.LENGTH_LONG)

            snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE

            snackbar.show()
        }
    }

    override suspend fun onSuccess(msg: String) {
        withContext(Dispatchers.Main) {
            successShow(msg)
        }
    }

    private fun successShow(msg: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)

        with(builder) {
            setTitle("Confirm")
            setMessage(msg)

            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        builder.setCancelable(false)
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()

        withdrawConfirmPresenter.onDestroy()
    }
}