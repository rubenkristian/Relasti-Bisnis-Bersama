package com.ethelworld.RBBApp.UI

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ethelworld.RBBApp.Adapter.DetailBonusAdapter
import com.ethelworld.RBBApp.Item.BonusGenerationItem
import com.ethelworld.RBBApp.Presenter.DetailBonusGenerationPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.DetailBonusGenerationView
import com.ethelworld.RBBApp.tools.EndlessScrollListener
import com.ethelworld.RBBApp.tools.OnLoadMoreListener
import kotlinx.coroutines.*

class DetailBonusGenerationScreen : ParentAppActivity(), DetailBonusGenerationView.View {
    private var detailBonusGenerationItem: ArrayList<BonusGenerationItem?> = ArrayList()

    private lateinit var bonusGenerationPresenter: DetailBonusGenerationPresenter
    private lateinit var scrollListener: EndlessScrollListener
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var detailBonusAdapter: DetailBonusAdapter

    private lateinit var detailRV: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var generationIndex: Int? = 0
    var page: Int = 1
    private var loading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_bonus_generation_screen)

        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = intent.extras?.get("title") as CharSequence?
        generationIndex = intent.extras?.getInt("id")

        detailRV = findViewById(R.id.detail_bonus_list)

        setLayoutManager()
        setScrollListener()

        bonusGenerationPresenter = DetailBonusGenerationPresenter(this, applicationContext)
        detailBonusAdapter = DetailBonusAdapter{

        }

        detailRV.adapter = detailBonusAdapter
        detailBonusAdapter.updateBonusGeneration(detailBonusGenerationItem.toMutableList())

        swipeRefreshLayout = findViewById(R.id.refresh_swipe)
        swipeRefreshLayout.setOnRefreshListener {
            refreshBonusGeneration()
        }

        CoroutineScope(Dispatchers.IO).launch {
            getItemBonus()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun refreshBonusGeneration() {
        if(!loading) {
            loading = true
            page = 1

            detailBonusGenerationItem.clear()
            detailBonusAdapter.updateBonusGeneration(detailBonusGenerationItem.toMutableList())

            CoroutineScope(Dispatchers.IO).launch {
                bonusGenerationPresenter.getListDetialBonusGeneration(generationIndex!!, page, 20)
            }

            swipeRefreshLayout.isRefreshing = false
        }
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            loading = true
            detailBonusGenerationItem.add(null)
            detailBonusAdapter.addLoadingView(detailBonusGenerationItem.toMutableList())
        }
    }

    override suspend fun hideLoading() {
        val listSize = detailBonusGenerationItem.size
        if(listSize > 0) {
            if(detailBonusGenerationItem[listSize - 1] == null) {
                detailBonusGenerationItem.removeAt(listSize - 1)
            }
        }
        withContext(Dispatchers.Main) {
            loading = false
            detailBonusAdapter.removeLoadingView(detailBonusGenerationItem.toMutableList())
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            loading = false
        }
    }

    override suspend fun onSuccess(bonusGenerations: ArrayList<BonusGenerationItem?>) {
        detailBonusGenerationItem.addAll(bonusGenerations)
        withContext(Dispatchers.Main) {
            detailBonusAdapter.updateBonusGeneration(detailBonusGenerationItem.toMutableList())
            scrollListener.setLoaded()

            loading = false
            page++
        }
    }

    private fun setLayoutManager() {
        layoutManager = LinearLayoutManager(applicationContext)
        detailRV.layoutManager = layoutManager
        detailRV.setHasFixedSize(true)
    }

    private fun setScrollListener() {
        layoutManager = LinearLayoutManager(applicationContext)
        scrollListener = EndlessScrollListener(layoutManager as LinearLayoutManager)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                CoroutineScope(Dispatchers.IO).launch {
                    getItemBonus()
                }
            }

            override fun onTheTop() {

            }
        })

        detailRV.addOnScrollListener(scrollListener)
    }

    private suspend fun getItemBonus() {
        if(!loading) {
            loading = true
            bonusGenerationPresenter.getListDetialBonusGeneration(generationIndex!!, page, 20)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bonusGenerationPresenter.onDestroy()
    }
}