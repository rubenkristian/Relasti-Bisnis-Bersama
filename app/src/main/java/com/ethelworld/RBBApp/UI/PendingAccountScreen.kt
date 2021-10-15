package com.ethelworld.RBBApp.UI

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ethelworld.RBBApp.Adapter.PendingAccountAdapter
import com.ethelworld.RBBApp.Item.PendingAccount
import com.ethelworld.RBBApp.Presenter.PendingAccountPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.PendingAccountView
import com.ethelworld.RBBApp.tools.EndlessScrollListener
import com.ethelworld.RBBApp.tools.OnLoadMoreListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class PendingAccountScreen : ParentAppActivity(), PendingAccountView.View {
    private val pendingAccountList: ArrayList<PendingAccount?> = ArrayList()
    private lateinit var pendingAccountAdapter: PendingAccountAdapter
    private lateinit var pendingAccountPresenter: PendingAccountPresenter
    private lateinit var scrollListener: EndlessScrollListener
    private lateinit var layoutManagerRV: RecyclerView.LayoutManager

    private lateinit var pendingAccountRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var searchView: SearchView
    private lateinit var querySearchListener: SearchView.OnQueryTextListener

    private var searchStore: String = ""

    private var searchJob: Job? = null
    private lateinit var rootView: View

    var page: Int = 1
    private var loading: Boolean = false

    private lateinit var paymentInfoScreen: Intent
    private lateinit var whatsAppMessage: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_account_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Rekan Menunggu"

        pendingAccountPresenter = PendingAccountPresenter(this, applicationContext)

        rootView = window.decorView.rootView

        pendingAccountRecyclerView = findViewById(R.id.pending_list)

        setLayoutManager()
        setScrollListener()

        pendingAccountAdapter = PendingAccountAdapter({ account->
            if (account != null) {
                paymentInfoScreen.putExtra("id", account.id.toLong())
                startActivity(paymentInfoScreen)
            }
        }, { phone ->
            whatsAppMessage.data = Uri.parse("whatsapp://send?" +
                    "phone=+$phone&" +
                    "text=Hallo kak.. Silahkan lakukan verifikasi administrasi " +
                    "dan sertakan bukti transaksi kakak kepada admin untuk mengaktifkan akun kakak, " +
                    "setelah itu bisa menghubungi saya " +
                    "untuk informasi lebih lanjut \uD83D\uDE0A\uD83D\uDE4F")
            startActivity(whatsAppMessage)
        })
        pendingAccountRecyclerView.adapter = pendingAccountAdapter

        pendingAccountAdapter.updatePendingAccount(pendingAccountList.toMutableList())

        swipeRefreshLayout = findViewById(R.id.refresh_swipe)

        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.isRefreshing = false

        swipeRefreshLayout.setOnRefreshListener {
            refreshPendingAccount()
        }

        CoroutineScope(Dispatchers.IO).launch {
            loadFirstPendingAccount()
        }

        paymentInfoScreen   = Intent(this, PaymentInfoScreen::class.java)
        whatsAppMessage     = Intent(Intent.ACTION_VIEW)
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

                pendingAccountList.clear()

                pendingAccountAdapter.updatePendingAccount(pendingAccountList.toMutableList())

                searchJob = CoroutineScope(Dispatchers.IO).launch {
                    pendingAccountPresenter.getPendingAccount(searchStore, page, 20)
                }

                return  true
            }
        }

        searchView.setOnQueryTextListener(querySearchListener)

        searchItem?.actionView = searchView

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    private fun setLayoutManager() {
        layoutManagerRV = LinearLayoutManager(applicationContext)

        pendingAccountRecyclerView.apply {
            layoutManager = layoutManagerRV

            setHasFixedSize(true)
        }
    }

    private fun setScrollListener() {
        scrollListener = EndlessScrollListener(layoutManagerRV as LinearLayoutManager)

        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                CoroutineScope(Dispatchers.IO).launch {
                    loadMorePendingAccount()
                }
            }

            override fun onTheTop() {

            }
        })

        pendingAccountRecyclerView.addOnScrollListener(scrollListener)
    }

    private fun refreshPendingAccount() {
        if(!loading) {
            loading = true
            page = 1

            pendingAccountList.clear()
            pendingAccountAdapter.updatePendingAccount(pendingAccountList.toMutableList())

            CoroutineScope(Dispatchers.IO).launch {
                pendingAccountPresenter.getPendingAccount(searchStore, page, 20)
            }

            swipeRefreshLayout.isRefreshing = false
        }
    }

    private suspend fun loadMorePendingAccount() {
        if(!loading) {
            loading = true

            pendingAccountPresenter.getPendingAccount(searchStore, page, 20)
        }
    }

    private suspend fun loadFirstPendingAccount() {
        if(!loading) {
            loading = true

            pendingAccountPresenter.getPendingAccount(searchStore, page, 20)
        }
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            loading = true

            pendingAccountList.add(null)
            pendingAccountAdapter.addLoadingView(pendingAccountList.toMutableList())
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            loading = false

            val listSize = pendingAccountList.size

            if(listSize > 0) {
                if(pendingAccountList[listSize - 1] == null) {
                    pendingAccountList.removeAt(listSize - 1)
                }
            }

            pendingAccountAdapter.removeLoadingView(pendingAccountList.toMutableList())
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            val snackbar = Snackbar
                .make(rootView, msg?:"Terjadi kesalahan.", Snackbar.LENGTH_SHORT)

            snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE

            snackbar.setAction("Muat ulang") {
                refreshPendingAccount()
            }

            snackbar.show()
        }
    }

    override suspend fun onSuccess(generations: ArrayList<PendingAccount?>) {
        pendingAccountList.addAll(generations)

        withContext(Dispatchers.Main) {
            pendingAccountAdapter.updatePendingAccount(pendingAccountList.toMutableList())
            scrollListener.setLoaded()

            loading = false
            page++
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        pendingAccountPresenter.onDestroy()
    }
}