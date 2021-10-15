package com.ethelworld.RBBApp.UI

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.ViewPager
import com.ethelworld.RBBApp.Adapter.ViewPagerAdapter
import com.ethelworld.RBBApp.Database.EthelDBHelper
import com.ethelworld.RBBApp.Fragment.AllContactFragment
import com.ethelworld.RBBApp.Fragment.HomeFragment
import com.ethelworld.RBBApp.Fragment.SystemUpgradeFragment
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import java.util.*

class MainScreen : ParentAppActivity() {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var pages: Array<Fragment>

    private val homeFragment: HomeFragment = HomeFragment()
    private val allContactFragment: AllContactFragment = AllContactFragment()
    private val systemUpgradeFragment: SystemUpgradeFragment = SystemUpgradeFragment()

    private lateinit var partnerAreaScreen: Intent
    private lateinit var settingScreen: Intent
    private lateinit var aboutScreen: Intent
    private lateinit var profileScreen: Intent
    private lateinit var splashScreen: Intent
    private lateinit var starRewardScreen: Intent
    private lateinit var shareIntent: Intent

    private lateinit var ethelDB: EthelDBHelper

    private lateinit var logoMain: ImageView
    private lateinit var navView: NavigationView

    private lateinit var installStateUpdateListener: InstallStateUpdatedListener
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var reviewManager: ReviewManager

    private lateinit var authentication: Authentication

    private var reviewInfo: ReviewInfo? = null

    private lateinit var snackbar: Snackbar

    private lateinit var rootView: View

    private lateinit var referalString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        MobileAds.initialize(this){}

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        toolbar.overflowIcon = ContextCompat.getDrawable(applicationContext, R.drawable.logo)
        drawer = findViewById(R.id.drawer_layout)

        logoMain = findViewById(R.id.logo_main)

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""

        val tabName: Array<String> = arrayOf(
            "Beranda",
            "Kontak",
            "Cashback")

        pages = arrayOf(
            homeFragment,
            allContactFragment,
            systemUpgradeFragment)

        val adapter = ViewPagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
            tabName,
            pages)

        val viewPager: ViewPager = findViewById(R.id.pager_layout)
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = 3

        findViewById<TabLayout>(R.id.tab_layout).setupWithViewPager(viewPager)

        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener {item: MenuItem ->
            Handler(Looper.getMainLooper()).postDelayed({
                when(item.itemId){
                    R.id.star_reward -> {
                        startActivity(starRewardScreen)
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    R.id.partner_area -> {
                        startActivity(partnerAreaScreen)
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    R.id.settings -> {
                        startActivity(settingScreen)
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    R.id.about -> {
                        startActivity(aboutScreen)
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    R.id.share -> {
                        startActivity(shareIntent)
                    }
                    R.id.logout -> {
                        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
                        with(builder){
                            setTitle("Keluar")
                            setMessage("Yakin ingin keluar?")
                            setPositiveButton("Keluar") { _, _ ->
                                Authentication(applicationContext).clearSharedPreference()
                                ethelDB.dropTable()
                                splashScreen.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(splashScreen)
                                finish()
                            }
                            setNegativeButton("Tidak") { dialog, _ ->
                                dialog.dismiss()
                            }
                        }
                        val logoutAlert = builder.create()
                        logoutAlert.show()
                    }
                }
            }, 100)
            true
        }

        authentication          = Authentication(applicationContext)
        val headerView: View    = navView.getHeaderView(0)
        val nameText            = headerView.findViewById<TextView>(R.id.fullname)
        val referalText         = headerView.findViewById<TextView>(R.id.referal)

        val nameString      = authentication.getValueString(Authentication.NAME)
        referalString       = authentication.getValueString(Authentication.REFERAL).toString().toUpperCase(Locale.ROOT)

        nameText.text       = nameString
        referalText.text    = referalString

        headerView.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(profileScreen)
            }, 100)
            drawer.closeDrawer(GravityCompat.START)
        }

        partnerAreaScreen   = Intent(this, PartnerGeneration::class.java)
        settingScreen       = Intent(this, SettingScreen::class.java)
        aboutScreen         = Intent(this, AboutScreen::class.java)
        profileScreen       = Intent(this, ProfileScreen::class.java)
        splashScreen        = Intent(this, SplashScreen::class.java)
        starRewardScreen    = Intent(this, RewardChangeScreen::class.java)

        ethelDB = EthelDBHelper(applicationContext)

        if(authentication.getValueInt(Authentication.SYNCCOUNT) == 0) {
            ethelDB.dropTable()
        }

        installStateUpdateListener = InstallStateUpdatedListener {
            when(it.installStatus()) {
                InstallStatus.DOWNLOADED -> {

                }
                InstallStatus.INSTALLED -> {
                    appUpdateManager.unregisterListener(installStateUpdateListener)
                }
                else -> {

                }
            }
        }

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(installStateUpdateListener)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener {appUpdateInfo ->
            when(appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    val updateTypes = arrayOf(AppUpdateType.FLEXIBLE, AppUpdateType.IMMEDIATE)
                    run loop@{
                        updateTypes.forEach{ type ->
                            if(appUpdateInfo.isUpdateTypeAllowed(type)) {
                                appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    type,
                                    this,
                                    REQUEST_UPDATE_CODE)
                                return@loop
                            }
                        }
                    }
                }
                else -> {

                }
            }
        }

        reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager.requestReviewFlow()

        request.addOnCompleteListener{ req ->
            reviewInfo = if(req.isSuccessful) {
                req.result
            } else {
                null
            }
        }

        val shareApp = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT,
            "Relasi Bisnis Bersama (RBB) adalah " +
                    "Aplikasi untuk membantu mempertemukan para pebisnis " +
                    "untuk dapat saling menghubungi satu sama lain dimana para pebisnis tersebut " +
                    "memiliki bidang bisnis masing2 yang dapat saling tukar pendapat " +
                    "ataupun menjadi calon relasi bisnis dari pertemuan lewat applikasi ini , " +
                    "contoh agen properti, asuransi, bisnis online, dan lain2 yg berbeda bidang " +
                    "bisa saling menambahkan relasinya lewat aplikasi ini. " +
                    "\nMasukan kode referal: $referalString ketika mendaftar" +
                    "\nDapatkan aplikasi ini " +
                    "di https://play.google.com/store/apps/details?id=com.ethelworld.RBBApp")
        }

        shareIntent = Intent.createChooser(shareApp, "Bagikan ke orang")

        rootView = window.decorView.rootView

        snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG)

        Handler(Looper.getMainLooper()).postDelayed({
            reviewInfo?.let { it ->
                val flow = reviewManager.launchReviewFlow(this@MainScreen, it)
                flow.addOnCompleteListener {
                    snackbar.setText("Terimakasih telah melakukan review")
                    snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    snackbar.duration = Snackbar.LENGTH_LONG
                    snackbar.show()
                }
                flow.addOnFailureListener { ex->
                    ex.message?.let { it1 ->
                        snackbar.setText(it1)
                        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                        snackbar.duration = Snackbar.LENGTH_LONG
                        snackbar.show()
                    }
                }
            }
        }, 3000)

        checkItemUpdate()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            if(drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }
            return true
        }

        return false
    }

    fun checkItemUpdate() {
        val navmenu = navView.menu
        navmenu.findItem(R.id.star_reward).isVisible =
            authentication.getValueBoolean("starrewarditem", false) // item reward star is hided, if starrewarditem is true it will show so just need to change some value when online to active this item list, in the future this item will show forever after some update come later
    }

    companion object {
        const val REQUEST_UPDATE_CODE = 1
    }
}