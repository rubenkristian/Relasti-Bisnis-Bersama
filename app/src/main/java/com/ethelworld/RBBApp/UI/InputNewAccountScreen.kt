package com.ethelworld.RBBApp.UI

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.ethelworld.RBBApp.Adapter.BankAdapter
import com.ethelworld.RBBApp.Adapter.CityAdapter
import com.ethelworld.RBBApp.Adapter.ProvinceAdapter
import com.ethelworld.RBBApp.Item.Bank
import com.ethelworld.RBBApp.Item.City
import com.ethelworld.RBBApp.Item.Province
import com.ethelworld.RBBApp.Presenter.InputAccountPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.InputAccountView
import com.ethelworld.RBBApp.tools.LoadingDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.hbb20.CountryCodePicker
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.regex.Pattern

class InputNewAccountScreen : ParentAppActivity(), InputAccountView.View {
    private lateinit var inputAccountPresenter: InputAccountPresenter
    private lateinit var loadingDialog: LoadingDialog

    private var listProvince: ArrayList<Province?> = ArrayList()
    private var listCity: ArrayList<City> = ArrayList()
    private var listBank: ArrayList<Bank> = ArrayList()

    private var cityAdapter: CityAdapter? = null
    private var provinceAdapter: ProvinceAdapter? = null
    private var bankAdapter: BankAdapter? = null

    private var idProvince: Int? = null
    private var idCity: Int? = null
    private var idBank: Int? = null
    private var idGender: Int? = null
    private var digitLength: Int = 0

    private val emailPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"
    private lateinit var patternEmail: Pattern

    // input
    private lateinit var nameInput : TextInputEditText
    private lateinit var numberInput : TextInputEditText
    private lateinit var genderInput : AutoCompleteTextView
    private lateinit var occupationInput : TextInputEditText
    private lateinit var companyInput : TextInputEditText
    private lateinit var provinceInput : AutoCompleteTextView
    private lateinit var cityInput : AutoCompleteTextView
    private lateinit var bankInput : AutoCompleteTextView
    private lateinit var bankNumberInput : TextInputEditText
    private lateinit var bankNameInput : TextInputEditText
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

    private var loading : Boolean = false

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_new_account_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.invite_account)

        inputAccountPresenter = InputAccountPresenter(this, applicationContext)

        rootView        = window.decorView.rootView
        loadingDialog   = LoadingDialog(this)
        // input initial
        nameInput       = findViewById(R.id.full_name)
        numberInput     = findViewById(R.id.whatsapp_number)
        genderInput     = findViewById(R.id.gender)
        occupationInput = findViewById(R.id.occupation)
        companyInput    = findViewById(R.id.company)
        provinceInput   = findViewById(R.id.province)
        cityInput       = findViewById(R.id.city)
        bankInput       = findViewById(R.id.bank)
        bankNameInput   = findViewById(R.id.bank_name)
        bankNumberInput = findViewById(R.id.bank_number)
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
                inputAccountPresenter.getCity(idProvince)
            }
        }

        provinceInput.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                inputAccountPresenter.getProvince()
            }
        }

        bankInput.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                inputAccountPresenter.getBank()
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

        bankAdapter = BankAdapter(
            applicationContext,
            R.layout.province_list_item,
            listBank)

        bankInput.setAdapter(bankAdapter)

        bankInput.onItemClickListener = OnItemClickListener { obj, _, position, _ ->
            val selected = obj.adapter.getItem(position) as Bank
            bankInput.text = SpannableStringBuilder(selected.name)
            idBank = selected.id
            digitLength = selected.digitLen
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

        youtubeInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                submitInput.callOnClick()
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })

        patternEmail = Pattern.compile(emailPattern)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // input method
    private fun submitToServer() {
        if(loading) return
        loading = true
        // text input
        val nameText: String        = nameInput.text.toString()
        val countryCodeText: String = ccpInput.selectedCountryCode
        val numberText: String      = numberInput.text.toString().filter { it.isDigit() }
        val emailText: String       = emailInput.text.toString()
        val occupationText: String  = occupationInput.text.toString()
        val companyText: String     = companyInput.text.toString()
        val provinceText: String    = provinceInput.text.toString()
        val cityText: String        = cityInput.text.toString()
        val bankText: String        = bankInput.text.toString()
        val bankNumberText: String  = bankNumberInput.text.toString()
        val bankNameText: String    = bankNameInput.text.toString()
        val facebookText: String    = facebookInput.text.toString()
        val instagramText: String   = instagramInput.text.toString()
        val olshopText: String      = olshopInput.text.toString()
        val tiktokText: String      = tiktokInput.text.toString()
        val youtubeText: String     = youtubeInput.text.toString()
        val passwordText: String    = passwordInput.text.toString()
        val rePasswordText: String  = confirmPasswordInput.text.toString()

        val fullNumber = countryCodeText + numberText

        if(nameText.isEmpty()) {
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

        if(idGender == null || idGender!! < 0) {
            genderInput.error = "Harus masukan gender."
            genderInput.requestFocus()
            loading = false
            return
        }

        if(occupationText.isEmpty()) {
            occupationInput.error = "Harus masukan pekerjaan."
            occupationInput.requestFocus()
            loading = false
            return
        }

        if(companyText.isEmpty()) {
            companyInput.error = "Harus masukan company."
            companyInput.requestFocus()
            loading = false
            return
        }

        if(provinceText.isEmpty() || idProvince == null) {
            provinceInput.error = "Harus masukan provinsi."
            provinceInput.requestFocus()
            loading = false
            return
        }

        if(cityText.isEmpty() || idCity == null) {
            cityInput.error = "Harus masukan kota."
            cityInput.requestFocus()
            loading = false
            return
        }

        if(bankText.isEmpty() || idBank == null) {
            bankInput.error = "Harus masukan nama bank."
            bankInput.requestFocus()
            loading = false
            return
        }

        if(bankNameText.isEmpty()) {
            bankNameInput.error = "Harus masukan nama akun bank."
            bankNameInput.requestFocus()
            loading = false
            return
        }

        if(bankNumberText.isEmpty()) {
            bankNumberInput.error = "Harus masukan nomor akun bank."
            bankNumberInput.requestFocus()
            loading = false
            return
        }

        if(bankNumberText.length != digitLength) {
            bankNumberInput.error = "Melebihi batasan, maksimal $digitLength digit"
            bankNumberInput.requestFocus()
            loading = false
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            inputAccountPresenter.postAccount(
                nameText,
                fullNumber,
                emailText,
                idGender,
                occupationText,
                companyText,
                idProvince,
                idCity,
                idBank,
                bankNumberText,
                bankNameText,
                facebookText,
                instagramText,
                tiktokText,
                olshopText,
                youtubeText,
                passwordText,
                rePasswordText)
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
            loading = false
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            Snackbar
                .make(
                    rootView,
                    msg?:"Terjadi kesalahan.",
                    Snackbar.LENGTH_SHORT)
                .show()
            loading = false
        }
    }

    override suspend fun onProvince(data: JSONObject) {
        val lists = data.getJSONArray("provinces")
        listProvince.clear()

        for (i in 0 until lists.length()) {
            val list = lists.getJSONObject(i)
            listProvince.add(
                Province(
                    list.getInt("id"),
                    list.getString("name")))
        }

        withContext(Dispatchers.Main) {
            provinceAdapter?.notifyDataSetChanged()
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
            cityAdapter?.notifyDataSetChanged()
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
                ))
        }

        withContext(Dispatchers.Main) {
            bankAdapter?.notifyDataSetChanged()
        }
    }

    override suspend fun onSuccess(data: JSONObject) {
        withContext(Dispatchers.Main) {
            val price       = data.getInt("price")
            val total       = data.getInt("total")
            val code        = data.getString("code")
            val referal     = data.getString("referal")
            val name        = data.getString("name")
            val wa          = data.getString("wa")
            val date        = data.getString("date")

            val paymentInfoActivity = Intent(applicationContext, PaymentDetail::class.java)

            paymentInfoActivity.putExtra("price", price)
            paymentInfoActivity.putExtra("code", code)
            paymentInfoActivity.putExtra("total", total)
            paymentInfoActivity.putExtra("referal", referal)
            paymentInfoActivity.putExtra("name", name)
            paymentInfoActivity.putExtra("wa", wa)
            paymentInfoActivity.putExtra("date", date)

            loading = false
            startActivity(paymentInfoActivity)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        inputAccountPresenter.onDestroy()
    }
}