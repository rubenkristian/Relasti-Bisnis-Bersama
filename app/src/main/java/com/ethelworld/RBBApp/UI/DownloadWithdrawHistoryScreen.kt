package com.ethelworld.RBBApp.UI

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.ethelworld.RBBApp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class DownloadWithdrawHistoryScreen : AppCompatActivity() {
    private lateinit var pickDateHistory: MaterialButton
    private lateinit var datePicker: DatePickerDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_withdraw_history_screen)

        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        pickDateHistory = findViewById(R.id.date_range_history)
    }

    private fun showDateDialog() {
        val calender = Calendar.getInstance()
    }
}