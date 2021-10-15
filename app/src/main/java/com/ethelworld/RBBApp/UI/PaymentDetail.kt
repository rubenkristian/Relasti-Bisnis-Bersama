package com.ethelworld.RBBApp.UI

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ethelworld.RBBApp.R
import java.text.NumberFormat
import java.util.*

class PaymentDetail : AppCompatActivity() {
    private lateinit var paymentWebView: WebView

    private lateinit var clipboard: ClipboardManager

    private val localID: Locale = Locale("in", "ID")
    private lateinit var rpFormat: NumberFormat

    private val numbersAdminList: Array<String> = arrayOf()

    private var referal: String = ""
    private var code: String = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_detail)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.title = "Payment Detail"

        val price   = intent.extras?.getInt("price")
        val code    = intent.extras?.getString("code").toString()
        val total   = intent.extras?.getInt("total")
        val referal = intent.extras?.getString("referal").toString()
        val name    = intent.extras?.getString("name")
        val wa      = intent.extras?.getString("wa")
        val date    = intent.extras?.getString("date")

        rpFormat = NumberFormat.getCurrencyInstance(localID)

        rpFormat.maximumFractionDigits = 0

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

        paymentWebView = findViewById(R.id.screen_payment)

        with(paymentWebView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            defaultTextEncodingName = "utf-8"
        }

        clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        val jsl = AppInterface(this@PaymentDetail,
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

        }

        paymentWebView.webViewClient = webViewClient

        paymentWebView.addJavascriptInterface(jsl, "Payment")
        paymentWebView.loadUrl("file:///android_asset/index.html")
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
        fun getPhoneNumberAdminList(): Array<String> {
            return numbersAdmin
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
}