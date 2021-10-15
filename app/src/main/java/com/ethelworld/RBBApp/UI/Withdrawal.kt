package com.ethelworld.RBBApp.UI

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import android.view.ViewStub
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.ethelworld.RBBApp.Item.InfoWithdraw
import com.ethelworld.RBBApp.Presenter.WithdrawalPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.View.WithdrawalView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.NumberFormatException
import java.text.NumberFormat
import java.util.*

class Withdrawal : AppCompatActivity(), WithdrawalView.View {
    private lateinit var actionBar: ActionBar
    private lateinit var withdrawalPresenter: WithdrawalPresenter

    // Text input edit
    private lateinit var username: TextInputEditText
    private lateinit var totalBonus: TextInputEditText
    private lateinit var bankName: TextInputEditText
    private lateinit var bankNumberAccount: TextInputEditText
    private lateinit var bankNameAccount: TextInputEditText
    private lateinit var withdrawTotal: TextInputEditText
    private lateinit var withdrawTextInputLayout: TextInputLayout

    // View
    private lateinit var loadingView: View
    private lateinit var withdrawLayout: View
    private lateinit var rootView: View
    private lateinit var withdrawViewStub: ViewStub

    // button
    private lateinit var withdrawSubmit: MaterialButton

    private val localID: Locale = Locale("in", "ID")
    private lateinit var rpFormat: NumberFormat

    private var totalBonusCurrency: Long = 0
    private var minimumWithdraw: Long = 0

    private lateinit var registerResultActivity: ActivityResultLauncher<Intent>

    private lateinit var withdrawWatcher: TextWatcher

    private lateinit var withdrawConfirmActivity: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        registerResultActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
            } else{
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdrawal)
        setSupportActionBar(findViewById(R.id.toolbar))

        actionBar = supportActionBar!!

        actionBar.setDisplayHomeAsUpEnabled(true)

        actionBar.title = getString(R.string.withdrawal)

        withdrawalPresenter = WithdrawalPresenter(this, applicationContext)

        loadingView         = findViewById(R.id.loading)
        withdrawViewStub    = findViewById(R.id.withdraw)

        rpFormat = NumberFormat.getCurrencyInstance(localID)

        rpFormat.maximumFractionDigits = 0

        withdrawWatcher = object: TextWatcher {
            var editing = false
            var digitCurrency : Long = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(!editing) {
                    editing = true

                    val digits      = s.toString().filter { it.isDigit() }
                    var currency    = rpFormat.format(digitCurrency)

                    if(digits.isEmpty()) {
                        digitCurrency       = 0
                        withdrawTotal.text  = SpannableStringBuilder(currency)

                        withdrawTotal.setSelection(currency.length)
                    } else {
                        try {
                            currency        = rpFormat.format(digits.toLong())
                            digitCurrency   = digits.toLong()

                            withdrawTotal.text = SpannableStringBuilder(currency)

                            withdrawTotal.setSelection(currency.length)
                        } catch (e: NumberFormatException) {
                            withdrawTotal.text = SpannableStringBuilder(currency)

                            withdrawTotal.setSelection(currency.length)
                        }
                    }

                    when {
                        digitCurrency.compareTo(minimumWithdraw) == -1 -> {
                            withdrawTotal.error = "Input yang anda masukan kurang dari minimum withdraw"
                        }
                        digitCurrency.compareTo(totalBonusCurrency) == 1 -> {
                            withdrawTotal.error = "Input yang anda masukan melebihi dari total bonus yang kamu miliki"
                        }
                        else -> {
                            withdrawTotal.error = null
                        }
                    }
                    editing = false
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            withdrawalPresenter.getInfo()
        }

        rootView = window.decorView.rootView

        withdrawConfirmActivity = Intent(this, WithdrawConfirm::class.java)
    }

    fun hideKeyword() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyword()
        onBackPressed()

        return true
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            loadingView.visibility = View.VISIBLE
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            loadingView.visibility = View.GONE
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            Snackbar
                .make(rootView, msg?:"Terjadi kesalahan.", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    override suspend fun onSuccess(data: JSONObject) {
        withContext(Dispatchers.Main) {
            val msg = data.getString("msg")
            val id = data.getInt("id")

            withdrawConfirmActivity.putExtra("msg", msg)
            withdrawConfirmActivity.putExtra("id", id)

            registerResultActivity.launch(withdrawConfirmActivity)
        }
    }

    override suspend fun onLoadDone(infoWithdraw: InfoWithdraw) {
        withContext(Dispatchers.Main) {
            withdrawLayout = withdrawViewStub.inflate()

            username                = findViewById(R.id.username)
            totalBonus              = findViewById(R.id.total_bonus)
            bankName                = findViewById(R.id.bank)
            bankNumberAccount       = findViewById(R.id.bank_number)
            bankNameAccount         = findViewById(R.id.bank_name)
            withdrawTotal           = findViewById(R.id.withdrawal_input)
            withdrawTextInputLayout = findViewById(R.id.withdrawal_layout)

            withdrawTotal.text  = SpannableStringBuilder(rpFormat.format(0))

            withdrawSubmit = findViewById(R.id.withdrawal_submit)

            val bank        = infoWithdraw.bank
            val noAccount   = infoWithdraw.bankNoAccount
            val nameAccount = infoWithdraw.bankNameAccount

            totalBonusCurrency                  = infoWithdraw.bonusTotal
            minimumWithdraw                     = infoWithdraw.minimum
            username.text                       = SpannableStringBuilder(infoWithdraw.username.toUpperCase(Locale.ROOT))
            totalBonus.text                     = SpannableStringBuilder(rpFormat.format(totalBonusCurrency))
            bankName.text                       = SpannableStringBuilder(bank)
            bankNumberAccount.text              = SpannableStringBuilder(noAccount)
            bankNameAccount.text                = SpannableStringBuilder(nameAccount)
            withdrawTextInputLayout.helperText  = infoWithdraw.helpTextWithdraw

            if(totalBonusCurrency.compareTo(minimumWithdraw) == -1) {
                withdrawTotal.error = "Minimum withdraw ${rpFormat.format(minimumWithdraw)}"
                withdrawTotal.requestFocus()
                withdrawTotal.isEnabled = false
                withdrawSubmit.isEnabled = false
            }

            withdrawTotal.addTextChangedListener(withdrawWatcher)

            withdrawTotal.setOnEditorActionListener { _, actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    submitWithdraw()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            withdrawSubmit.setOnClickListener {
                submitWithdraw()
            }

            if(bank.trim().isEmpty() || noAccount.trim().isEmpty() || nameAccount.trim().isEmpty()) {
                errorShow()
            }

            loadingView.visibility = View.GONE
        }
    }

    private fun submitWithdraw() {
        val totWithdraw = rpFormat.parse(withdrawTotal.text.toString())
        CoroutineScope(Dispatchers.IO).launch {
            withdrawalPresenter.submitWithdraw(totWithdraw!!.toString())
        }
//        withdrawConfirmActivity.putExtra("withdraw", totWithdraw)
//
//        registerResultActivity.launch(withdrawConfirmActivity)
    }

    private fun errorShow() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)

        with(builder) {
            setTitle("Error")
            setMessage("Harus menyertakan informasi akun bank")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
        }

        builder.setCancelable(false)
        builder.show()
    }

    override fun onDestroy() {
        setResult(Activity.RESULT_CANCELED)

        super.onDestroy()

        withdrawalPresenter.onDestroy()
    }
}