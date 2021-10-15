package com.ethelworld.RBBApp.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.ethelworld.RBBApp.Presenter.TACPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.View.TACView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*

class FirstTAC : AppCompatActivity(), TACView.View {
    private lateinit var agreeButton: MaterialButton
    private lateinit var checkAgreement: CheckBox
    private lateinit var loader: FrameLayout
    private lateinit var tacPresenter: TACPresenter

    private lateinit var secondTacScreen: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_tac)

        val termAndCondition: WebView = findViewById(R.id.termandcondition)
        termAndCondition.loadUrl("file:///android_asset/tac.html")

        tacPresenter = TACPresenter(this, applicationContext)

        loader          = findViewById(R.id.loader)
        agreeButton     = findViewById(R.id.agree)
        checkAgreement  = findViewById(R.id.check_agreement)

        checkAgreement.isChecked = Authentication(applicationContext).isTACAgreed()

        agreeButton.isEnabled = false

        checkAgreement.setOnCheckedChangeListener { _, isChecked ->
            agreeButton.isEnabled = isChecked
        }

        agreeButton.setOnClickListener { _ ->
            if(checkAgreement.isChecked){
//                val nextScreen = Intent(this, SecondTAC::class.java)
//                startActivity(nextScreen)
                CoroutineScope(Dispatchers.IO).launch {
                    tacPresenter.sendTAC()
                }
            }
        }

        secondTacScreen = Intent(this, SecondTAC::class.java)
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            val inAnimation = AlphaAnimation(0f, 1f)
            inAnimation.duration = 200
            loader.animation = inAnimation
            loader.visibility = View.VISIBLE
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            val outAnimation = AlphaAnimation(1f, 0f)
            outAnimation.duration = 200
            loader.animation = outAnimation
            loader.visibility = View.GONE
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            errorShow(msg)
        }
    }

    fun errorShow(msg: String?) {
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

    override suspend fun onSuccess(success: Boolean) {
        withContext(Dispatchers.Main) {
            if(success){
                startActivity(secondTacScreen)
            }else{
                showError(500, "Something went wrong, try again.")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tacPresenter.onDestroy()
    }
}