package com.ethelworld.RBBApp.UI

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.ethelworld.RBBApp.R

class VersionScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.title = getString(R.string.version)

        val pm = applicationContext.packageManager
        var packageInfo: PackageInfo? = null

        try {
            packageInfo = pm.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException){

        }

        val version = packageInfo?.versionName

        findViewById<TextView>(R.id.version_info).text = version
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }
}