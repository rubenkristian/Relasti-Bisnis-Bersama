package com.ethelworld.RBBApp.UI

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ethelworld.RBBApp.Adapter.WithdrawHistoryAdapter
import com.ethelworld.RBBApp.Item.WithdrawHistory
import com.ethelworld.RBBApp.Presenter.WithdrawHistoryPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.WithdrawHistoryView
import com.ethelworld.RBBApp.tools.EndlessScrollListener
import com.ethelworld.RBBApp.tools.OnLoadMoreListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class WithdrawHistoryScreen : ParentAppActivity(), WithdrawHistoryView.View {
    private val withdrawHistoryList: ArrayList<WithdrawHistory?> = ArrayList()

    private lateinit var withdrawHistoryPresenter: WithdrawHistoryPresenter
    private lateinit var withdrawHistoryAdapter: WithdrawHistoryAdapter
    private lateinit var layoutManagerRV: RecyclerView.LayoutManager
    private lateinit var scrollListener: EndlessScrollListener
    private lateinit var totalWithdrawText: TextView

    private lateinit var withdrawHistoryRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView

    private lateinit var querySearchListener: SearchView.OnQueryTextListener

    private var searchStore: String = ""

    private var searchJob: Job? = null

    private var page: Int = 1
    private var loading: Boolean = false

    lateinit var rootView: View

    val localCurr = Locale("in", "ID")
    private lateinit var rpFormat: NumberFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_history_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Withdraw History"

        rpFormat = NumberFormat.getCurrencyInstance(localCurr)

        rpFormat.maximumFractionDigits = 0

        withdrawHistoryPresenter    = WithdrawHistoryPresenter(this, applicationContext)
        withdrawHistoryRecyclerView = findViewById(R.id.withdraw_history_list)

        totalWithdrawText = findViewById(R.id.total_withdraw)

        setLayoutManager()
        setScrollListener()

        withdrawHistoryAdapter = WithdrawHistoryAdapter {

        }

        withdrawHistoryRecyclerView.adapter = withdrawHistoryAdapter

        withdrawHistoryAdapter.updateWithdrawHistory(withdrawHistoryList.toMutableList())

        swipeRefreshLayout = findViewById(R.id.refresh_swipe)

        swipeRefreshLayout.isEnabled    = false
        swipeRefreshLayout.isRefreshing = false

        swipeRefreshLayout.setOnRefreshListener {
            refreshWithdrawHistory()
        }

        CoroutineScope(Dispatchers.IO).launch {
            loadFirstWithdrawHistory()
        }

        CoroutineScope(Dispatchers.IO).launch {
            withdrawHistoryPresenter.getTotalWithdraw()
        }

        rootView = window.decorView.rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)

        val searchItem      = menu?.findItem(R.id.search_list)
        val downloadItem    = menu?.findItem(R.id.download_withdraw_history)

        searchView = SearchView(this)

        searchView.queryHint = "Cari Withdraw..."

        querySearchListener = object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()

                searchStore = newText ?: ""
                page = 1

                withdrawHistoryList.clear()
                withdrawHistoryAdapter.updateWithdrawHistory(withdrawHistoryList.toMutableList())

                searchJob = CoroutineScope(Dispatchers.IO).launch {
                    withdrawHistoryPresenter.getWithdrawHistory(searchStore, page, 20)
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

        withdrawHistoryRecyclerView.apply {
            layoutManager = layoutManagerRV

            setHasFixedSize(true)
        }
    }

    private fun setScrollListener() {
        scrollListener = EndlessScrollListener(layoutManagerRV as LinearLayoutManager)

        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if(!loading) {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadMoreWithdrawHistory()
                    }
                }
            }

            override fun onTheTop() {

            }
        })

        withdrawHistoryRecyclerView.addOnScrollListener(scrollListener)
    }

    private fun refreshWithdrawHistory() {
        if(!loading) {
            loading = true
            page = 1

            withdrawHistoryList.clear()
            withdrawHistoryAdapter.updateWithdrawHistory(withdrawHistoryList.toMutableList())

            CoroutineScope(Dispatchers.IO).launch {
                withdrawHistoryPresenter.getWithdrawHistory(searchStore, page, 20)
            }

            swipeRefreshLayout.isRefreshing = false
        }
    }

    private suspend fun loadMoreWithdrawHistory() {
        if(!loading) {
            loading = true
            withdrawHistoryPresenter.getWithdrawHistory(searchStore, page, 20)
        }
    }

    private suspend fun loadFirstWithdrawHistory() {
        if(!loading) {
            loading = true

            withdrawHistoryPresenter.getWithdrawHistory(searchStore, page, 20)
        }
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            loading = true

            withdrawHistoryList.add(null)
            withdrawHistoryAdapter.addLoadingView(withdrawHistoryList.toMutableList())
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            loading = false

            val listSize = withdrawHistoryList.size

            if(listSize > 0) {
                if(withdrawHistoryList[listSize - 1] == null) {
                    withdrawHistoryList.removeAt(listSize - 1)
                }
            }

            withdrawHistoryAdapter.removeLoadingView(withdrawHistoryList.toMutableList())
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            val snackbar = Snackbar.make(rootView, msg?:"Terjadi kesalahan.", Snackbar.LENGTH_LONG)

            snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE

            snackbar.setAction("Muat ulang") {
                refreshWithdrawHistory()
            }

            snackbar.show()

            totalWithdrawText.text = msg
        }
    }

    override suspend fun onSuccess(generations: ArrayList<WithdrawHistory?>) {
        withdrawHistoryList.addAll(generations)

        withContext(Dispatchers.Main) {
            withdrawHistoryAdapter.updateWithdrawHistory(withdrawHistoryList.toMutableList())
            scrollListener.setLoaded()

            loading = false

            page++
        }
    }

    override suspend fun onTotalSuccess(total: Long) {
        withContext(Dispatchers.Main) {
            totalWithdrawText.text = rpFormat.format(total)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        withdrawHistoryPresenter.onDestroy()
    }
}