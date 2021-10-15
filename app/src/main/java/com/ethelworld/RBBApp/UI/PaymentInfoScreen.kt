package com.ethelworld.RBBApp.UI

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ethelworld.RBBApp.Presenter.PaymentInfoPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.View.PaymentInfoView
import com.ethelworld.RBBApp.tools.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class PaymentInfoScreen : AppCompatActivity(), PaymentInfoView.View {
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var paymentInfoPresenter: PaymentInfoPresenter
    private lateinit var paymentWebView: WebView

    private lateinit var clipboard: ClipboardManager

    private val localID: Locale = Locale("in", "ID")
    private lateinit var rpFormat: NumberFormat

    private val numbersAdminList: Array<String> = arrayOf()

    private var idPayment: Long? = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_info_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.title = "Payment Detail"

        loadingDialog = LoadingDialog(this)

        idPayment = intent.extras?.getLong("id")

        rpFormat = NumberFormat.getCurrencyInstance(localID)

        rpFormat.maximumFractionDigits = 0

        paymentInfoPresenter = PaymentInfoPresenter(this, applicationContext)

        paymentWebView = findViewById(R.id.screen_payment)
        with(paymentWebView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            defaultTextEncodingName = "utf-8"
        }

        clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        CoroutineScope(Dispatchers.IO).launch {
            idPayment?.let { paymentInfoPresenter.getPaymentInfo(it) }
        }
    }

    private fun loadJS(webView: WebView?) {
        webView?.loadUrl(
            """
                javascript:(function f() {
                })()
            """
        )
    }

    class AppInterface(
        private val context: Context,
        private val wa: String,
        private val date: String,
        private val name: String,
        private val referal: String,
        private val price: String,
        private val code: String,
        private val codeText: String,
        private val total: String,
        private val payment: () -> Unit,
        private val numbersAdmin: Array<String>,
        private val copy: (String) -> Unit
    ) {

        private var clipboard: ClipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        val whatsAppMessage = Intent(Intent.ACTION_VIEW)

        @JavascriptInterface
        fun getName(): String {
            return name
        }

        @JavascriptInterface
        fun getReferal(): String {
            return referal
        }

        @JavascriptInterface
        fun getCode(): String {
            return code
        }

        @JavascriptInterface
        fun getPrice(): String {
            return price
        }

        @JavascriptInterface
        fun getTotal(): String {
            return total
        }

        @JavascriptInterface
        fun getDate(): String {
            return date
        }

        @JavascriptInterface
        fun getWA(): String {
            return wa
        }

        @JavascriptInterface
        fun getPhoneNumberAdminList(): Array<String> {
            return numbersAdmin
        }

        @JavascriptInterface
        fun copyNumber(text: String) {
            val clip = ClipData.newPlainText("copy", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Berhasil menyalin '$text'", Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun confirmPayment() {
            payment()
        }

        @JavascriptInterface
        fun sendWA(number: String) {
//            val url = "http://api.whatsapp.com/send?phone=+6287880003805&text="
            val url = "whatsapp://send?phone=+$number&text=Konfirmasi pembayaran untuk akun $referal dengan kode pembayaran $codeText"
            whatsAppMessage.data = Uri.parse(url)
            context.startActivity(whatsAppMessage)
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
        withContext(Dispatchers.Main) {
            loadingDialog.dismissDialog()
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            msg?.let { errorShow(it) }
        }
    }

    override suspend fun onSuccess(data: JSONObject) {
        withContext(Dispatchers.Main) {

            val webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    loadJS(view)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (request != null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            view?.loadUrl(request.url.toString())
                        } else {
                            view?.loadUrl(request.toString())
                        }
                    }
                    return false
                }
            }

            val wa      = data.getString("wa")
            val date    = data.getString("date")
            val name    = data.getString("name")
            val referal = data.getString("referal")
            val price   = data.getInt("price")
            val code    = data.getString("code")
            val total   = data.getInt("total")

            val jsl = AppInterface(this@PaymentInfoScreen,
                wa.toString(),
                date.toString(),
                name.toString(),
                referal,
                rpFormat.format(price),
                rpFormat.format(code.toLong()),
                code,
                rpFormat.format(total),
                {
//                paymentWebView.loadUrl("file:///android_asset/payment.html")
                },
                numbersAdminList
            ) {
//                Log.i("TEXTCOPY", it)
            }
            paymentWebView.webViewClient = webViewClient

            paymentWebView.addJavascriptInterface(jsl, "Payment")
            paymentWebView.loadUrl("file:///android_asset/payment.html")
        }
    }

    private fun errorShow(msg: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)

        with(builder) {
            setTitle("Error")
            setMessage(msg)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
        }

        builder.setCancelable(false)
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()

        paymentInfoPresenter.onDestroy()
    }
}