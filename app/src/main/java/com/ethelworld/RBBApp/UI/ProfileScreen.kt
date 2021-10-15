package com.ethelworld.RBBApp.UI

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewStub
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.ethelworld.RBBApp.Adapter.BankAdapter
import com.ethelworld.RBBApp.Item.Bank
import com.ethelworld.RBBApp.Presenter.ProfilePresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.ProfileView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import org.json.JSONObject

class ProfileScreen : ParentAppActivity(), ProfileView.View {
    private lateinit var profilePresenter: ProfilePresenter

    private var listBank: ArrayList<Bank> = ArrayList()

    private var cityId: Int = 0
    private var provinceId: Int = 0
    private var bankId: Int = 0
    private var digitLength: Int = 0
    private var bankNameText: String = ""
    private var bankNumberText: String = ""

    private lateinit var initialView: TextView
    private lateinit var phoneView: TextView
    private lateinit var nameView: TextView
    private lateinit var usernameView: TextView
    private lateinit var waInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var occupationInput: TextInputEditText
    private lateinit var companyInput: TextInputEditText
    private lateinit var provinceInput: TextInputEditText
    private lateinit var cityInput: TextInputEditText
    private lateinit var accountNumberInput: TextInputEditText
    private lateinit var accountNameInput: TextInputEditText
    private lateinit var bankNameInput: AutoCompleteTextView
    private lateinit var facebookInput: TextInputEditText
    private lateinit var instagramInput: TextInputEditText
    private lateinit var olshopInput: TextInputEditText
    private lateinit var tiktokInput: TextInputEditText
    private lateinit var youtubeInput: TextInputEditText
    private lateinit var editInfoImage: ImageView
    private lateinit var saveInfoButton: MaterialButton
    private var editProgress: ProgressBar? = null
    private lateinit var editBankInfoImage: ImageView
    private lateinit var saveBankButton: MaterialButton
    private var bankProgress: ProgressBar? = null

    private lateinit var bankAdapter: BankAdapter

    private lateinit var loadingView: View
    private lateinit var profileViewStub: ViewStub
    private lateinit var profileLayout: View

    private var editStatus: Boolean = false
    private var editBankStatus: Boolean = false
    private lateinit var fbValue: String
    private lateinit var igValue: String
    private lateinit var olshopValue: String
    private lateinit var tiktokValue: String
    private lateinit var ytValue: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.title = getString(R.string.profile)

        profilePresenter = ProfilePresenter(this, applicationContext)

        loadingView         = findViewById(R.id.loading)
        profileViewStub     = findViewById(R.id.profile)

        CoroutineScope(Dispatchers.IO).launch {
            profileRequest()
        }
    }

    private suspend fun profileRequest() {
        profilePresenter.getProfile()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if(editStatus) {
            if(editStatus) {
                editChange()
            }

            if(editBankStatus) {
                editBankChange()
            }

        } else {
            super.onBackPressed()
        }
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            editProgress?.visibility = View.VISIBLE
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
//            swipeRefreshLayout.isRefreshing = false
            editProgress?.visibility = View.GONE
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            errorShow(msg)
        }
    }

    private fun errorShow(msg: String?) {
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

    override suspend fun onSuccess(token: JSONObject) {
        val username            = token.getString("username")
        val name                = token.getString("fullname")
        val wa                  = token.getString("wa")
        val email               = token.getString("email")
        val occupation          = token.getString("occupation")
        val company             = token.getString("company")
        val province            = token.getString("province")
        provinceId              = token.getInt("id_province")
        val city                = token.getString("city")
        cityId                  = token.getInt("id_city")
        bankNumberText          = token.getString("bank_account_number")
        bankNameText            = token.getString("bank_account_name")
        val bankName            = token.getString("bank")
        bankId                  = token.getInt("id_bank")

        fbValue     = token.getString("fb")
        igValue     = token.getString("ig")
        olshopValue = token.getString("olshop")
        tiktokValue = token.getString("tiktok")
        ytValue     = token.getString("yt")

        withContext(Dispatchers.Main) {
            profileLayout = profileViewStub.inflate()

            initialView         = findViewById(R.id.initial_name)
            nameView            = findViewById(R.id.full_name)
            usernameView        = findViewById(R.id.username)
            phoneView           = findViewById(R.id.phoneNumber)
            waInput             = findViewById(R.id.whatsapp_number)
            emailInput          = findViewById(R.id.email_address)
            occupationInput     = findViewById(R.id.occupation)
            companyInput        = findViewById(R.id.company)
            provinceInput       = findViewById(R.id.province)
            cityInput           = findViewById(R.id.city)
            accountNumberInput  = findViewById(R.id.account_number)
            accountNameInput    = findViewById(R.id.account_name)
            bankNameInput       = findViewById(R.id.bank_name)
            facebookInput       = findViewById(R.id.facebook)
            instagramInput      = findViewById(R.id.instagram)
            olshopInput         = findViewById(R.id.olshop)
            tiktokInput         = findViewById(R.id.tiktok)
            youtubeInput        = findViewById(R.id.youtube)
            editInfoImage       = findViewById(R.id.edit_info)
            saveInfoButton      = findViewById(R.id.save_info)
            editProgress        = findViewById(R.id.edit_progress)
            editBankInfoImage   = findViewById(R.id.edit_bank)
            saveBankButton      = findViewById(R.id.save_bank)
            bankProgress        = findViewById(R.id.edit_bank_progress)

            bankAdapter = BankAdapter(applicationContext, R.layout.province_list_item, listBank)

            bankNameInput.setAdapter(bankAdapter)

            bankNameInput.onItemClickListener = AdapterView.OnItemClickListener { obj, _, position, _ ->
                val selected = obj.adapter.getItem(position) as Bank

                bankNameInput.text = SpannableStringBuilder(selected.name)

                bankId      = selected.id
                digitLength = selected.digitLen
            }

            usernameView.text       = username
            nameView.text           = name
            initialView.text        = name[0].toUpperCase().toString()
            phoneView.text          = wa
            waInput.text            = SpannableStringBuilder(wa)
            emailInput.text         = SpannableStringBuilder(email)
            occupationInput.text    = SpannableStringBuilder(occupation)
            companyInput.text       = SpannableStringBuilder(company)
            provinceInput.text      = SpannableStringBuilder(province)
            cityInput.text          = SpannableStringBuilder(city)
            accountNumberInput.text = SpannableStringBuilder(bankNumberText)
            accountNameInput.text   = SpannableStringBuilder(bankNameText)
            bankNameInput.text      = SpannableStringBuilder(bankName)

            facebookInput.text  = SpannableStringBuilder(fbValue)
            instagramInput.text = SpannableStringBuilder(igValue)
            olshopInput.text    = SpannableStringBuilder(olshopValue)
            tiktokInput.text    = SpannableStringBuilder(tiktokValue)
            youtubeInput.text   = SpannableStringBuilder(ytValue)

            youtubeInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    saveInfoButton.callOnClick()
                    return@OnEditorActionListener true
                }
                return@OnEditorActionListener false
            })

            editInfoImage.setOnClickListener {
                editChange()
            }

            editBankInfoImage.setOnClickListener {
                editBankChange()
            }

            bankNameInput.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    profilePresenter.getBank()
                }
            }

            saveBankButton.setOnClickListener {
                bankNameText    = accountNameInput.text.toString()
                bankNumberText  = accountNumberInput.text.toString()

                if(bankNameInput.text.toString().isEmpty() || bankId == 0) {
                    bankNameInput.error = "Harus masukan nama bank."

                    bankNameInput.requestFocus()

                    return@setOnClickListener
                }
                
                if(bankNameText.isEmpty()) {
                    accountNameInput.error = "Harus masukan nama akun bank."

                    accountNameInput.requestFocus()

                    return@setOnClickListener
                }

                if(bankNumberText.isEmpty()) {
                    accountNumberInput.error = "Harus masukan nomor akun bank."

                    accountNumberInput.requestFocus()

                    return@setOnClickListener
                }

                if(bankNumberText.length != digitLength) {
                    accountNumberInput.error = "Harus memasukan $digitLength digit angka"

                    accountNumberInput.requestFocus()

                    return@setOnClickListener
                }

                saveBankButton.visibility   = View.GONE
                bankProgress?.visibility    = View.VISIBLE

                CoroutineScope(Dispatchers.IO).launch {
                    profilePresenter.saveBank(
                        bankId,
                        bankNumberText,
                        bankNameText
                    )
                }
            }

            saveInfoButton.setOnClickListener {
                saveInfoButton.visibility   = View.GONE
                editProgress?.visibility    = View.VISIBLE

                CoroutineScope(Dispatchers.IO).launch {
                    profilePresenter.updateInfo(
                        facebookInput.text.toString(),
                        instagramInput.text.toString(),
                        olshopInput.text.toString(),
                        tiktokInput.text.toString(),
                        youtubeInput.text.toString())
                }
            }

            if(bankId == 0 || bankNameText.trim().isEmpty() || bankNumberText.trim().isEmpty()) {
                editBankInfoImage.visibility = View.VISIBLE
            } else {
                editBankInfoImage.visibility = View.GONE
            }

            Handler(Looper.getMainLooper()).postDelayed(
            {
                loadingView.visibility = View.GONE
            }, 100)
//            swipeRefreshLayout.visibility = View.VISIBLE
        }
    }

    override suspend fun onUpdateSuccess(token: JSONObject) {
        fbValue     = token.getString("fb")
        igValue     = token.getString("ig")
        olshopValue = token.getString("olshop")
        tiktokValue = token.getString("tiktok")
        ytValue     = token.getString("yt")

        withContext(Dispatchers.Main) {
            facebookInput.text  = SpannableStringBuilder(fbValue)
            instagramInput.text = SpannableStringBuilder(igValue)
            olshopInput.text    = SpannableStringBuilder(olshopValue)
            tiktokInput.text    = SpannableStringBuilder(tiktokValue)
            youtubeInput.text   = SpannableStringBuilder(ytValue)

            editChange()
        }
    }

    override suspend fun onBank(data: JSONObject) {
        withContext(Dispatchers.Main) {
            val lists = data.getJSONArray("bank")

            listBank.clear()

            for (i in 0 until lists.length()) {
                val list = lists.getJSONObject(i)
                val bank = Bank(
                    list.getInt("id"),
                    list.getString("name"),
                    list.getInt("digit_limit"))

                listBank
                    .add(bank)
            }

            bankAdapter.notifyDataSetChanged()
        }
    }

    override suspend fun onUpdateBank(data: JSONObject) {
        bankNameText        = data.getString("name")
        bankNumberText      = data.getString("number")
        bankId              = data.getInt("bank")

        withContext(Dispatchers.Main) {
            accountNumberInput.text = SpannableStringBuilder(bankNumberText)
            accountNameInput.text   = SpannableStringBuilder(bankNameText)

            editBankChange()
        }
    }

    fun editChange() {
        editStatus = !editStatus

        facebookInput.isFocusable   = editStatus
        instagramInput.isFocusable  = editStatus
        tiktokInput.isFocusable     = editStatus
        olshopInput.isFocusable     = editStatus
        youtubeInput.isFocusable    = editStatus

        facebookInput.isEnabled     = editStatus
        instagramInput.isEnabled    = editStatus
        olshopInput.isEnabled       = editStatus
        tiktokInput.isEnabled       = editStatus
        youtubeInput.isEnabled      = editStatus

        if(editStatus) {
            facebookInput.isClickable     = editStatus
            instagramInput.isClickable    = editStatus
            olshopInput.isClickable       = editStatus
            tiktokInput.isClickable       = editStatus
            youtubeInput.isClickable      = editStatus

            facebookInput.isFocusableInTouchMode     = editStatus
            instagramInput.isFocusableInTouchMode    = editStatus
            olshopInput.isFocusableInTouchMode       = editStatus
            tiktokInput.isFocusableInTouchMode       = editStatus
            youtubeInput.isFocusableInTouchMode      = editStatus

            editInfoImage.setImageResource(R.drawable.icon_close)

            saveInfoButton.visibility = View.VISIBLE

            facebookInput.requestFocus()
        } else {
            editInfoImage.setImageResource(R.drawable.icon_edit)

            saveInfoButton.visibility = View.GONE
        }
    }

    fun editBankChange() {
        editBankStatus = !editBankStatus

        bankNameInput.isFocusable       = editBankStatus
        accountNumberInput.isFocusable  = editBankStatus
        accountNameInput.isFocusable    = editBankStatus

        bankNameInput.isEnabled         = editBankStatus
        accountNumberInput.isEnabled    = editBankStatus
        accountNameInput.isEnabled      = editBankStatus

        if(editBankStatus) {
            bankNameInput.isClickable       = editBankStatus
            accountNumberInput.isClickable  = editBankStatus
            accountNameInput.isClickable    = editBankStatus

            bankNameInput.isFocusableInTouchMode        = editBankStatus
            accountNumberInput.isFocusableInTouchMode   = editBankStatus
            accountNameInput.isFocusableInTouchMode     = editBankStatus

            editBankInfoImage.setImageResource(R.drawable.icon_close)

            saveBankButton.visibility = View.VISIBLE

            bankNameInput.requestFocus()
        } else {
            editBankInfoImage.setImageResource(R.drawable.icon_edit)

            saveBankButton.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        profilePresenter.onDestroy()
    }

//    companion object {
//        const val facebookLink = "https://www.facebook.com/"
//        const val instagramLink = "https://www.instagram.com/"
//        const val tiktokLink = "https://www.tiktok.com/"
//        const val youtubeLink = "https://www.youtube.com/c/"
//        const val facebookLinkSeperator = "https://www.facebook.com"
//        const val instagramLinkSeperator = "https://www.instagram.com"
//        const val tiktokLinkSeperator = "https://www.tiktok.com"
//        const val youtubeLinkSeperator = "https://www.youtube.com/c"
//    }
}