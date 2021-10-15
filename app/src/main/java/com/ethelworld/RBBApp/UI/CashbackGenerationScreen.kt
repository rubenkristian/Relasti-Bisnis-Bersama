package com.ethelworld.RBBApp.UI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ethelworld.RBBApp.Adapter.GenerationBonusAdapter
import com.ethelworld.RBBApp.Item.BonusGeneration
import com.ethelworld.RBBApp.Presenter.BonusGenerationPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.BonusGenerationView
import com.ethelworld.RBBApp.tools.EndlessScrollListener
import com.ethelworld.RBBApp.tools.OnLoadMoreListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CashbackGenerationScreen : ParentAppActivity(), BonusGenerationView.View {
    private var bonusGenerationList: ArrayList<BonusGeneration?> = ArrayList()
    private lateinit var bonusGenerationPresenter: BonusGenerationPresenter
    private lateinit var generationBonusAdapter: GenerationBonusAdapter
    private lateinit var scrollListener: EndlessScrollListener
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var totalBonusText: TextView

    private var actionBar: ActionBar? = null

    private var loading: Boolean = false
    private lateinit var bonusGenerationRecView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var detailBonusGenerationActivity: Intent

    private lateinit var rootView: View

    val localCurr = Locale("in", "ID")
    private lateinit var rpFormat: NumberFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashback_generation_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.cashback_keturunan)

        rpFormat = NumberFormat.getCurrencyInstance(localCurr)
        rpFormat.maximumFractionDigits = 0

        bonusGenerationRecView = findViewById(R.id.bonus_generation_list)
        totalBonusText = findViewById(R.id.total_bonus)
        setLayoutManager()
        setScrollListener()

        bonusGenerationPresenter = BonusGenerationPresenter(this, applicationContext)

        detailBonusGenerationActivity = Intent(this, DetailBonusGenerationScreen::class.java)
        generationBonusAdapter = GenerationBonusAdapter { bonusGeneration ->
            detailBonusGenerationActivity.putExtra(
                "title",
                "Bonus Keturunan ${bonusGeneration?.generationIndex}")
            detailBonusGenerationActivity.putExtra(
                "id",
                bonusGeneration?.generationIndex)
            startActivity(detailBonusGenerationActivity)
        }

        bonusGenerationRecView.apply {
            adapter = generationBonusAdapter
        }
        generationBonusAdapter.updateBonusGeneration(bonusGenerationList.toMutableList())

        swipeRefreshLayout = findViewById(R.id.refresh_swipe)
        swipeRefreshLayout.setOnRefreshListener {
            refreshBonusGeneration()
        }

        loadBonusGeneration()

        CoroutineScope(Dispatchers.IO).launch {
            bonusGenerationPresenter.getTotalBonus()
        }

        rootView = window.decorView.rootView
    }

    private fun loadBonusGeneration() {
        if(!loading) {
            loading = true
            CoroutineScope(Dispatchers.IO).launch {
                bonusGenerationPresenter.getListBonusgeneration()
            }
        }
    }

    private fun refreshBonusGeneration() {
        if(!loading) {
            loading = true
            bonusGenerationList.clear()
            generationBonusAdapter.updateBonusGeneration(bonusGenerationList.toMutableList())
            CoroutineScope(Dispatchers.IO).launch {
                bonusGenerationPresenter.getListBonusgeneration()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setLayoutManager() {
        layoutManager = LinearLayoutManager(applicationContext)
        bonusGenerationRecView.layoutManager = layoutManager
        bonusGenerationRecView.setHasFixedSize(true)
    }

    private fun setScrollListener() {
        layoutManager = LinearLayoutManager(applicationContext)
        scrollListener = EndlessScrollListener(layoutManager as LinearLayoutManager)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
//                LoadMorePartner()
            }

            override fun onTheTop() {

            }
        })

        bonusGenerationRecView.addOnScrollListener(scrollListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override suspend fun showLoading() {
        loading = true
        withContext(Dispatchers.Main) {
            bonusGenerationList.add(null)
            generationBonusAdapter.addLoadingView(bonusGenerationList.toMutableList())
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            val listSize = bonusGenerationList.size
            if(listSize > 0) {
                if(bonusGenerationList[listSize - 1] == null) {
                    bonusGenerationList.removeAt(listSize - 1)
                }
            }
            generationBonusAdapter.removeLoadingView(bonusGenerationList.toMutableList())
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            val snackbar = Snackbar.make(rootView, msg.toString(), Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Muat ulang") {
                refreshBonusGeneration()
            }
            snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
            snackbar.show()
            totalBonusText.text = msg
        }
    }

    override suspend fun onSuccess(bonusGenerations: ArrayList<BonusGeneration?>) {
        bonusGenerationList.addAll(bonusGenerations)
        withContext(Dispatchers.Main) {
            generationBonusAdapter.updateBonusGeneration(bonusGenerationList.toMutableList())
            scrollListener.setLoaded()

            loading = false
        }
    }

    override suspend fun onTotalSuccess(total: Long) {
        withContext(Dispatchers.Main) {
//            Log.i("TOTALBONUS", rpFormat.format(total))
            totalBonusText.text = rpFormat.format(total)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bonusGenerationPresenter.onDestroy()
    }
}