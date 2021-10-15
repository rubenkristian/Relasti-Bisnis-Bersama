package com.ethelworld.RBBApp.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Adapter.CountryCodeAdapter
import com.ethelworld.RBBApp.Item.CountryCode
import com.ethelworld.RBBApp.R

class CountryCodeList : AppCompatActivity() {
    private lateinit var countryList: RecyclerView
    private lateinit var countryListAdapter: CountryCodeAdapter

    private val countryArray: ArrayList<CountryCode> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_code_list)

        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setListCountryCode()

        countryListAdapter = CountryCodeAdapter(applicationContext, countryArray) {
            val intent = Intent()
            intent.putExtra("name", it.name)
            intent.putExtra("code", it.code)
            setResult(RESULT_OK, intent)
            finish()
        }

        countryList = findViewById(R.id.country_list)
        countryList.adapter = countryListAdapter
        countryListAdapter.notifyDataSetChanged()
    }

    private fun setListCountryCode() {
        val countryList = resources.getStringArray(R.array.CountryCodes)
        for((index, country) in countryList.withIndex()) {
            val g = country.trim().split(",")
            countryArray.add(CountryCode(index, g[1], g[0]))
        }
    }
}