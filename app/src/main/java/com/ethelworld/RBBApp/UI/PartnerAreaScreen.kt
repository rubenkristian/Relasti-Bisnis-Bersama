package com.ethelworld.RBBApp.UI

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Adapter.PartnerAdapter
import com.ethelworld.RBBApp.Item.Partner
import com.ethelworld.RBBApp.Presenter.PartnerAreaPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.PartnerAreaView
import com.ethelworld.RBBApp.tools.EndlessScrollListener
import com.ethelworld.RBBApp.tools.OnLoadMoreListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class PartnerAreaScreen : ParentAppActivity(), PartnerAreaView.View {
    private var partnersList: ArrayList<Partner?> = ArrayList()
    private lateinit var partnerAdapter: PartnerAdapter
    private lateinit var scrollListener: EndlessScrollListener
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var partnerAreaPresenter: PartnerAreaPresenter
    private lateinit var partnersRecyclerView: RecyclerView

    private lateinit var searchView: SearchView
    private lateinit var querySearchListener: SearchView.OnQueryTextListener

    private var searchStore: String = ""

    private var searchJob: Job? = null

    private var generationId: Int = 0
    var page: Int = 1
    private var loading: Boolean = false

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_area_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.title = intent.extras?.get("title") as CharSequence?

        generationId = intent.extras?.getInt("generation")!!

        partnersRecyclerView = findViewById(R.id.partner_list)

        setLayoutManager()
        setScrollListener()

        partnerAreaPresenter = PartnerAreaPresenter(this, applicationContext)

        partnerAdapter = PartnerAdapter { partner: Partner? ->
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)

            with(builder){
                setTitle("Info")
                setMessage( if(partner!!.verified){"Terverifikasi"} else {"Tidak terverifikasi"})
                setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
            }

            builder.show()
        }

        partnersRecyclerView.adapter = partnerAdapter

        partnerAdapter.updatePartner(partnersList.toMutableList())

        CoroutineScope(Dispatchers.IO).launch {
            loadFirstPartner()
        }

        rootView  = window.decorView.rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)

        val searchItem = menu?.findItem(R.id.search_list)

        searchView = SearchView(this)

        searchView.queryHint = "Cari Partner..."

        querySearchListener = object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()

                searchStore = newText ?: ""

                page = 1

                partnersList.clear()
                partnerAdapter.updatePartner(partnersList.toMutableList())

                searchJob = CoroutineScope(Dispatchers.IO).launch {
                    partnerAreaPresenter.getPartnerArea(
                        generationId,
                        searchStore,
                        page,
                        20)
                }

                return  true
            }
        }

        searchView.setOnQueryTextListener(querySearchListener)

        searchItem?.actionView = searchView

        return true
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            loading = true

            partnersList.add(null)
            partnerAdapter.addLoadingView(partnersList.toMutableList())
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            loading = false

            val listSize = partnersList.size

            if(listSize > 0) {
                if(partnersList[listSize - 1] == null) {
                    partnersList.removeAt(listSize - 1)
                }
            }

            partnerAdapter.removeLoadingView(partnersList.toMutableList())
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            Snackbar
                .make(
                    rootView,
                    msg?:"Terjadi kesalahan.",
                    Snackbar.LENGTH_SHORT)
                .setAction("Muat ulang") {
                    refreshPartner()
                }
                .show()

                loading = false
        }
    }

    override suspend fun onSuccess(partners: ArrayList<Partner?>) {
        partnersList.addAll(partners)

        withContext(Dispatchers.Main) {
            partnerAdapter.updatePartner(partnersList.toMutableList())
            scrollListener.setLoaded()

            loading = false
            page++
        }
    }

    private fun refreshPartner() {
        if(!loading) {
            loading = true
            page = 1

            partnersList.clear()
            partnerAdapter.updatePartner(partnersList.toMutableList())

            CoroutineScope(Dispatchers.IO).launch {
                partnerAreaPresenter.getPartnerArea(generationId, searchStore, page, 20)
            }
        }
    }

    private suspend fun loadMorePartner() {
        if(!loading) {
            loading = true

            partnerAreaPresenter.getPartnerArea(generationId, searchStore, page, 20)
        }
    }

    private suspend fun loadFirstPartner() {
        if(!loading) {
            loading = true

            partnerAreaPresenter.getPartnerArea(generationId, searchStore, page, 20)
        }
    }

    private fun setLayoutManager() {
        layoutManager = LinearLayoutManager(applicationContext)

        partnersRecyclerView.layoutManager = layoutManager

        partnersRecyclerView.setHasFixedSize(true)
    }

    private fun setScrollListener() {
        layoutManager   = LinearLayoutManager(applicationContext)
        scrollListener  = EndlessScrollListener(layoutManager as LinearLayoutManager)

        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                CoroutineScope(Dispatchers.IO).launch {
                    loadMorePartner()
                }
            }

            override fun onTheTop() {

            }
        })

        partnersRecyclerView.addOnScrollListener(scrollListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        partnerAreaPresenter.onDestroy()
    }

    companion object {
        const val REQUEST_CODE_INPUT: Short = 1
    }
}