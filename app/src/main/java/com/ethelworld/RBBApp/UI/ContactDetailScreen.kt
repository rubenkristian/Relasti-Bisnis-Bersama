package com.ethelworld.RBBApp.UI

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewStub
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ethelworld.RBBApp.Database.EthelDBHelper
import com.ethelworld.RBBApp.Item.Contact
import com.ethelworld.RBBApp.Presenter.ContactDetailPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.ContactDetailView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

class ContactDetailScreen : ParentAppActivity(), ContactDetailView.View {
    private lateinit var facebookLayout: View
    private lateinit var instagramLayout: View
    private lateinit var olshopLayout: View
    private lateinit var tiktokLayout: View
    private lateinit var youtubeLayout: View

    private lateinit var occupationTextView: TextView
    private lateinit var companyTextView: TextView
    private lateinit var cityTextView: TextView
    private lateinit var provinceTextView: TextView
    private lateinit var facebookTextView: TextView
    private lateinit var instagramTextView: TextView
    private lateinit var olshopTextView: TextView
    private lateinit var tiktokTextView: TextView
    private lateinit var youtubeTextView: TextView

    private lateinit var whatsappButton: ImageButton

    private lateinit var loadingView: View
    private lateinit var contactViewStub: ViewStub
    private lateinit var contactLayout: View

    private lateinit var contactDetailPresenter: ContactDetailPresenter

    private lateinit var selfName: String
    private lateinit var selfOccupation: String
    private lateinit var selfAddress: String

    // basic information
    private var id: Long?               = 0
    private var idUser: Long?           = 0
    private var name: String?           = null
    private var phoneContact: String?   = null
    private var city: String?           = null
    private var isSave: Boolean         = false

    private lateinit var registerResultActivity : ActivityResultLauncher<Intent>
    private lateinit var url: String

    private lateinit var snackbar: Snackbar

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        registerResultActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(CONTACT_REQUEST, result)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail_screen)

        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        id              = intent.getLongExtra("id", 0)
        idUser          = intent.getLongExtra("id_user", 0)
        name            = intent.getStringExtra("name")
        city            = intent.getStringExtra("city")
        phoneContact    = intent.getStringExtra("phoneNumber")

        actionBar?.title = name

        findViewById<TextView>(R.id.initialBar).text = name?.get(0).toString().capitalize(Locale.ROOT)
        findViewById<TextView>(R.id.detail).text = phoneContact
        contactDetailPresenter = ContactDetailPresenter(this, applicationContext)

        loadingView = findViewById(R.id.loading)
        contactViewStub = findViewById(R.id.contact)

        val authentication = Authentication(applicationContext)
        selfName        = authentication.getValueString(Authentication.NAME).toString()
        selfOccupation  = authentication.getValueString(Authentication.OCCUPATION).toString()
        selfAddress     = authentication.getValueString(Authentication.ADDRESS).toString()

        CoroutineScope(Dispatchers.IO).launch {
            contactDetailPresenter.getContactDetail(id)
        }

        rootView = window.decorView.rootView

        snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG)

        url = "whatsapp://send?" +
                "phone=+$phoneContact&" +
                "text=Halo rekan RBB,\n" +
                "nama saya $selfName \n" +
                "pekerjaan $selfOccupation \n" +
                "alamat $selfAddress\n\nSaling save ya kak \uD83D\uDC4D"
    }

    private fun saveContact() {
        val contactIntent = Intent(ContactsContract.Intents.Insert.ACTION)
        contactIntent.type = ContactsContract.RawContacts.CONTENT_TYPE

        contactIntent
            .putExtra(
                ContactsContract.Intents.Insert.NAME,
                "REKAN $name $city")
            .putExtra(
                ContactsContract.Intents.Insert.PHONE,
                "+$phoneContact")

        registerResultActivity.launch(contactIntent)
    }

    private fun onActivityResult(requestCode: Short, result: ActivityResult) {
        if(requestCode == CONTACT_REQUEST) {
            when(result.resultCode) {
                Activity.RESULT_OK -> {
                    val ethelDB = EthelDBHelper(applicationContext)
                    isSave = ethelDB.setContactSaved(id!!)
                    snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    if(isSave) {
                        snackbar.setText("Kontak berhasil disimpan")
                        invalidateOptionsMenu()
                    } else {
                        snackbar.setText("Kontak gagal disimpan")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    snackbar.setText("Batal menyimpan kontak")
                }
            }
        }
        snackbar.show()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.contact_detail, menu)

        val saveMenu = menu?.findItem(R.id.save_contact)
        saveMenu?.isVisible = !isSave
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save_contact -> {
                saveContact()
            }
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
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

    override suspend fun showError(msg: String?) {
        withContext(Dispatchers.Main) {
            with(snackbar) {
                if (msg != null) {
                    setText(msg)
                }
                duration = Snackbar.LENGTH_INDEFINITE

                setAction("Muat ulang") {
                    CoroutineScope(Dispatchers.IO).launch {
                        contactDetailPresenter.getOtherInfo(idUser)
                    }
                }
            }
            snackbar.show()
        }
    }

    override suspend fun onSuccess(contact: Contact?) {
        withContext(Dispatchers.Main) {
            if (contact == null) {
                return@withContext
            }
            contactLayout = contactViewStub.inflate()

            facebookLayout  = findViewById(R.id.facebook_link)
            instagramLayout = findViewById(R.id.instagram_link)
            olshopLayout    = findViewById(R.id.olshop_link)
            tiktokLayout    = findViewById(R.id.tiktok_link)
            youtubeLayout   = findViewById(R.id.youtube_link)

            occupationTextView  = findViewById(R.id.occupation)
            companyTextView     = findViewById(R.id.company)
            cityTextView        = findViewById(R.id.city)
            provinceTextView    = findViewById(R.id.province)
            facebookTextView    = findViewById(R.id.facebook)
            instagramTextView   = findViewById(R.id.instagram)
            olshopTextView      = findViewById(R.id.olshop)
            tiktokTextView      = findViewById(R.id.tiktok)
            youtubeTextView     = findViewById(R.id.youtube)
            whatsappButton      = findViewById(R.id.whatsapp)

            isSave                  = contact.isSave == true
            occupationTextView.text = contact.occupation
            companyTextView.text    = contact.company
            cityTextView.text       = contact.city
            provinceTextView.text   = contact.province
            facebookTextView.text   = contact.facebook
            instagramTextView.text  = contact.instagram
            olshopTextView.text     = contact.olshop
            tiktokTextView.text     = contact.tiktok
            youtubeTextView.text    = contact.youtube

            whatsappButton.setOnClickListener{
                val whatsAppMessage = Intent(Intent.ACTION_VIEW)
                whatsAppMessage.data = Uri.parse(url)
                startActivity(whatsAppMessage)
            }
            invalidateOptionsMenu()
            loadingView.visibility = View.GONE

            CoroutineScope(Dispatchers.IO).launch {
                contactDetailPresenter.getOtherInfo(idUser)
            }
        }
    }

    override suspend fun onOthweInfoSuccess(result: JSONObject) {
        withContext(Dispatchers.Main) {
            val facebookAccount     = facebookLink + result.getString("fb")
            val instagramAccount    = instagramLink + result.getString("ig")
            val olshopAccount       = result.getString("olshop")
            val tiktokAccount       = tiktokLink + result.getString("tiktok")
            val youtubeAccount      = youtubeLink + result.getString("yt")

            facebookTextView.text   = facebookAccount
            instagramTextView.text  = instagramAccount
            olshopTextView.text     = olshopAccount
            tiktokTextView.text     = tiktokAccount
            youtubeTextView.text    = youtubeAccount

            facebookLayout.setOnClickListener{
                if(facebookAccount.replace(facebookLink, "").isEmpty()) {
                    with(snackbar) {
                        setText("Akun Facebook tidak tersedia.")
                        duration = Snackbar.LENGTH_SHORT
                        animationMode = Snackbar.ANIMATION_MODE_SLIDE
                        show()
                    }
                } else {
                    val facebook = Intent(Intent.ACTION_VIEW, Uri.parse(facebookAccount))
                    startActivity(facebook)
                }
            }

            instagramLayout.setOnClickListener {
                if(instagramAccount.replace(instagramLink, "").isEmpty()) {
                    with(snackbar) {
                        setText("Akun Instagram tidak tersedia.")
                        duration = Snackbar.LENGTH_SHORT
                        animationMode = Snackbar.ANIMATION_MODE_SLIDE
                        show()
                    }
                } else {
                    val instagram = Intent(Intent.ACTION_VIEW, Uri.parse(instagramAccount))
                    startActivity(instagram)
                }
            }

            olshopLayout.setOnClickListener {
                if(olshopAccount.isEmpty()) {
                    with(snackbar) {
                        setText("Akun Olshop tidak tersedia.")
                        duration = Snackbar.LENGTH_SHORT
                        animationMode = Snackbar.ANIMATION_MODE_SLIDE
                        show()
                    }
                } else {
                    if(Patterns.WEB_URL.matcher(olshopAccount).matches()) {
                        val olshop = Intent(Intent.ACTION_VIEW, Uri.parse(olshopAccount))
                        startActivity(olshop)
                    } else {
                        Toast.makeText(applicationContext, "Akun Olshop tidak tesedia.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            tiktokLayout.setOnClickListener {
                if(tiktokAccount.replace(tiktokLink, "").isEmpty()) {
                    with(snackbar) {
                        setText("Akun TikTok tidak tersedia.")
                        duration = Snackbar.LENGTH_SHORT
                        animationMode = Snackbar.ANIMATION_MODE_SLIDE
                        show()
                    }
                } else {
                    val tiktok = Intent(Intent.ACTION_VIEW, Uri.parse(tiktokAccount))
                    startActivity(tiktok)
                }
            }

            youtubeLayout.setOnClickListener {
                if(youtubeAccount.replace(youtubeLink, "").isEmpty()) {
                    with(snackbar) {
                        setText("Akun YouTube tidak tersedia.")
                        duration = Snackbar.LENGTH_SHORT
                        animationMode = Snackbar.ANIMATION_MODE_SLIDE
                        show()
                    }
                } else {
                    val youtube = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeAccount))
                    startActivity(youtube)
                }
            }
        }
    }

    companion object {
        const val CONTACT_REQUEST: Short = 1
        const val facebookLink = "https://facebook.com/"
        const val instagramLink = "https://instagram.com/"
        const val olshopLink = "https://facebook.com/"
        const val tiktokLink = "https://tiktok.com/"
        const val youtubeLink = "https://youtube.com/channel/"
    }

    override fun onDestroy() {
        super.onDestroy()
        contactDetailPresenter.onDestroy()
    }
}