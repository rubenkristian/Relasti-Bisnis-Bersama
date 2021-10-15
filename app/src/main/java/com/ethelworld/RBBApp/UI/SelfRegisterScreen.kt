package com.ethelworld.RBBApp.UI

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.ethelworld.RBBApp.Adapter.BankAdapter
import com.ethelworld.RBBApp.Adapter.CityAdapter
import com.ethelworld.RBBApp.Adapter.ProvinceAdapter
import com.ethelworld.RBBApp.Item.Bank
import com.ethelworld.RBBApp.Item.City
import com.ethelworld.RBBApp.Item.Province
import com.ethelworld.RBBApp.Presenter.SelfRegisterPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.View.SelfRegisterView
import com.ethelworld.RBBApp.tools.LoadingDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.AdapterView.OnItemClickListener
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.google.android.material.snackbar.Snackbar
import com.hbb20.CountryCodePicker
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class SelfRegisterScreen : ParentAppActivity(), SelfRegisterView.View {
    private lateinit var selfRegisterPresenter: SelfRegisterPresenter
    private lateinit var loadingDialog: LoadingDialog

    private var listProvince: ArrayList<Province?>  = ArrayList()
    private var listCity: ArrayList<City>           = ArrayList()
    private var listBank: ArrayList<Bank>           = ArrayList()

    private lateinit var provinceAdapter: ProvinceAdapter
    private lateinit var cityAdapter: CityAdapter
    private lateinit var bankAdapter: BankAdapter

    private var idProvince: Int?    = null
    private var idGender: Int?      = null
    private var idCity: Int?        = null

    private val emailPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"
    private lateinit var patternEmail: Pattern
//    private var idBank: Int? = null

    // input
    private lateinit var referalInput: TextInputEditText
    private lateinit var nameInput : TextInputEditText
    private lateinit var numberInput : TextInputEditText
    private lateinit var genderInput : AutoCompleteTextView
    private lateinit var occupationInput : TextInputEditText
    private lateinit var companyInput : TextInputEditText
    private lateinit var provinceInput : AutoCompleteTextView
    private lateinit var cityInput : AutoCompleteTextView
    private lateinit var facebookInput : TextInputEditText
    private lateinit var instagramInput : TextInputEditText
    private lateinit var olshopInput : TextInputEditText
    private lateinit var tiktokInput : TextInputEditText
    private lateinit var youtubeInput : TextInputEditText
    private lateinit var emailInput : TextInputEditText
    private lateinit var submitInput : MaterialButton
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var ccpInput: CountryCodePicker

    private lateinit var paymentInfoScreen: Intent

    private lateinit var snackBarMessage: Snackbar

//    private val codePhone = "+62"
    private lateinit var rootView: View

    private var loading : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_self_register_screen)

        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.partner_invite)

        loadingDialog = LoadingDialog(this)

        selfRegisterPresenter = SelfRegisterPresenter(this, applicationContext)

        // input initial
        referalInput    = findViewById(R.id.referal_code)
        nameInput       = findViewById(R.id.full_name)
        numberInput     = findViewById(R.id.whatsapp_number)
        genderInput     = findViewById(R.id.gender)
        occupationInput = findViewById(R.id.occupation)
        companyInput    = findViewById(R.id.company)
        provinceInput   = findViewById(R.id.province)
        cityInput       = findViewById(R.id.city)
        facebookInput   = findViewById(R.id.facebook)
        instagramInput  = findViewById(R.id.instagram)
        olshopInput     = findViewById(R.id.olshop)
        tiktokInput     = findViewById(R.id.tiktok)
        youtubeInput    = findViewById(R.id.youtube)
        emailInput      = findViewById(R.id.email)
        ccpInput        = findViewById(R.id.ccp)

        passwordInput           = findViewById(R.id.password)
        confirmPasswordInput    = findViewById(R.id.confirm_password)

        provinceAdapter = ProvinceAdapter(
            applicationContext,
            R.layout.province_list_item,
            listProvince)

        provinceInput.setAdapter(provinceAdapter)

        provinceInput.onItemClickListener = OnItemClickListener { _, view, _, id ->
            val v = view as TextView
            val name = v.text.toString()
            provinceInput.text = SpannableStringBuilder(name)
            idProvince = id.toInt()
            CoroutineScope(Dispatchers.IO).launch{
                selfRegisterPresenter.getCity(idProvince)
            }
        }

//        numberInput.setText(codePhone)
        
        provinceInput.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                selfRegisterPresenter.getProvince()
            }
        }

        cityAdapter = CityAdapter(
            applicationContext,
            R.layout.province_list_item,
            listCity)

        cityInput.setAdapter(cityAdapter)

        cityInput.onItemClickListener = OnItemClickListener {_, view, _, id ->
            val v = view as TextView
            val name = v.text.toString()
            cityInput.text = SpannableStringBuilder(name)
            idCity = id.toInt()
        }

        val gender = arrayOf("Laki-laki", "Perempuan")
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            applicationContext,
            R.layout.province_list_item,
            gender)

        genderInput.setAdapter(adapter)

        genderInput.onItemClickListener = OnItemClickListener { _, _, _, id ->
            idGender = id.toInt()
        }

        submitInput = findViewById(R.id.submit)

        submitInput.setOnClickListener {
            hideKeyword()
            submitToServer()
        }

        youtubeInput.setOnEditorActionListener(TextView.OnEditorActionListener{ _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                submitInput.callOnClick()
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })

        rootView = window.decorView.rootView

        snackBarMessage     = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG)
        paymentInfoScreen   = Intent(this, PaymentInfoScreen::class.java)

        patternEmail = Pattern.compile(emailPattern)
    }

    // input method
    private fun submitToServer() {
        if(loading) return
        loading = true
        // text input
        val referalText: String     = referalInput.text.toString()
        val nameText: String        = nameInput.text.toString()
        val countryCodeText: String = ccpInput.selectedCountryCode
        val numberText: String      = numberInput.text.toString().filter { it.isDigit() }
        val emailText: String       = emailInput.text.toString()
        val occupationText: String  = occupationInput.text.toString()
        val companyText: String     = companyInput.text.toString()
        val provinceText: String    = provinceInput.text.toString()
        val cityText: String        = cityInput.text.toString()
        val facebookText: String    = facebookInput.text.toString()
        val instagramText: String   = instagramInput.text.toString()
        val olshopText: String      = olshopInput.text.toString()
        val tiktokText: String      = tiktokInput.text.toString()
        val youtubeText: String     = youtubeInput.text.toString()

        val passwordText: String    = passwordInput.text.toString()
        val rePasswordText: String  = confirmPasswordInput.text.toString()

        val fullNumber = countryCodeText + numberText

        if(referalText.isEmpty()) {
            referalInput.error = "Harus masukan kode referal."

            referalInput.requestFocus()

            loading = false

            return
        }

        if (nameText.isEmpty()) {
            nameInput.error = "Harus masukan nama."

            nameInput.requestFocus()

            loading = false

            return
        }

        if(emailText.isEmpty()) {
            emailInput.error = "Harus masukan email."

            emailInput.requestFocus()

            loading = false

            return
        }

        if(!patternEmail.matcher(emailText).matches()) {
            emailInput.error = "Format email tidak sesuai."

            emailInput.requestFocus()

            loading = false

            return
        }

        if(numberText[0] == '0') {
            numberInput.error = "Tidak boleh berawalan 0."

            numberInput.requestFocus()

            loading = false

            return
        }

        if(numberText.isEmpty() || fullNumber.length !in 15 downTo 7) {
            numberInput.error = "Harus masukan nomor yang valid."

            numberInput.requestFocus()

            loading = false

            return
        }

        if(passwordText.isEmpty()) {
            passwordInput.error = "Masukan password"

            passwordInput.requestFocus()

            loading = false

            return
        }

        if(passwordText.trim().length < 6) {
            passwordInput.error = "Password minimal 6 digit"

            passwordInput.requestFocus()

            loading = false

            return
        }

        if(rePasswordText.isEmpty() || passwordText != rePasswordText) {
            confirmPasswordInput.error = "Samakan dengan isi password di atas"

            confirmPasswordInput.requestFocus()

            loading = false

            return
        }

        if (idGender == null || idGender?:-1 < 0) {
            genderInput.error = "Harus masukan gender."

            genderInput.requestFocus()

            loading = false

            return
        }

        if (occupationText.isEmpty()) {
            occupationInput.error = "Harus masukan pekerjaan."

            occupationInput.requestFocus()

            loading = false

            return
        }

        if (companyText.isEmpty()) {
            companyInput.error = "Harus masukan company."

            companyInput.requestFocus()

            loading = false

            return
        }

        if (provinceText.isEmpty() || idProvince == null) {
            provinceInput.error = "Harus masukan provinsi."

            provinceInput.requestFocus()

            loading = false

            return
        }

        if (cityText.isEmpty() || idCity == null) {
            cityInput.error = "Harus masukan kota."

            cityInput.requestFocus()

            loading = false

            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            selfRegisterPresenter.postAccount(
                referalText,
                nameText,
                fullNumber,
                emailText,
                idGender,
                occupationText,
                companyText,
                idProvince,
                idCity,
                facebookText,
                instagramText,
                tiktokText,
                olshopText,
                youtubeText,
                passwordText,
                rePasswordText)
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

            loading = false
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            Snackbar
                .make(rootView, msg?:"Terjadi kesalahan.", Snackbar.LENGTH_SHORT)
                .show()

            loading = false
        }
    }

    override suspend fun onAccountIsAvaiable(msg: String, id: Long) {
        withContext(Dispatchers.Main) {
            with(snackBarMessage) {
                setText(msg)

                duration = Snackbar.LENGTH_INDEFINITE

                setAction("Lihat info") { _ ->
                    paymentInfoScreen.putExtra("id", id)
                    startActivity(paymentInfoScreen)
                }
            }

            snackBarMessage.show()
        }
    }

    override suspend fun onSuccess(data: JSONObject) {
        withContext(Dispatchers.Main) {
            withContext(Dispatchers.Main) {
                val id      = data.getLong("id")
                val referal = data.getString("referal")
                val name    = data.getString("name")
                val date    = data.getString("date")
                val code    = data.getString("code")
                val wa      = data.getString("wa")
                val total   = data.getInt("total")
                val price   = data.getInt("price")

                val paymentInfoActivity = Intent(applicationContext, PaymentDetail::class.java)

                paymentInfoActivity.putExtra("price", price)
                paymentInfoActivity.putExtra("code", code)
                paymentInfoActivity.putExtra("total", total)
                paymentInfoActivity.putExtra("referal", referal)
                paymentInfoActivity.putExtra("name", name)
                paymentInfoActivity.putExtra("wa", wa)
                paymentInfoActivity.putExtra("date", date)

                Authentication(applicationContext).save(Authentication.IDLASTREGISTER, id)

                loading = false

                startActivity(paymentInfoActivity)
                finish()
            }
        }
    }

    override suspend fun onProvince(data: JSONObject) {
        val lists = data.getJSONArray("provinces")

        listProvince.clear()

        for (i in 0 until lists.length()) {
            val list = lists.getJSONObject(i)

            listProvince.add(Province(list.getInt("id"), list.getString("name")))
        }

        withContext(Dispatchers.Main) {
            provinceAdapter.notifyDataSetChanged()
        }
    }

    override suspend fun onCity(data: JSONObject) {
        val lists = data.getJSONArray("cities")

        listCity.clear()

        for (i in 0 until lists.length()) {
            val list = lists.getJSONObject(i)

            listCity.add(
                City(
                    list.getInt("id"),
                    list.getInt("province_id"),
                    list.getString("name")
                )
            )
        }

        withContext(Dispatchers.Main) {
            cityAdapter.notifyDataSetChanged()
        }
    }

    override suspend fun onBank(data: JSONObject) {
        val lists = data.getJSONArray("bank")

        listBank.clear()

        for (i in 0 until lists.length()) {
            val list = lists.getJSONObject(i)

            listBank.add(
                Bank(
                    list.getInt("id"),
                    list.getString("name"),
                    list.getInt("digit_limit")
                )
            )
        }

        withContext(Dispatchers.Main) {
            bankAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        selfRegisterPresenter.onDestroy()
    }
}