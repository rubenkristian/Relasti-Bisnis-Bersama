package com.ethelworld.RBBApp.UI.Parent

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

open class ParentAppActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    public fun hideKeyword() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideKeyword()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}