package com.ethelworld.RBBApp.UI

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Adapter.RewardChangeAdapter
import com.ethelworld.RBBApp.Item.RewardChange
import com.ethelworld.RBBApp.Presenter.RewardChangePresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.View.RewardChangeView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.ref.WeakReference

class RewardChangeScreen : AppCompatActivity(), RewardChangeView.View {
    private var rewardChangeList: ArrayList<RewardChange?> = ArrayList()
    private lateinit var rewardChangePresenter: RewardChangePresenter

    private lateinit var rewardChangeAdapter: RewardChangeAdapter

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var rewardChangeRecyclerView: RecyclerView
    private lateinit var countStarText: TextView

    private var loading: Boolean = false
    private lateinit var rootView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_change_screen)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        rewardChangePresenter = RewardChangePresenter(this, applicationContext)

        countStarText            = findViewById(R.id.star_count)
        rewardChangeRecyclerView = findViewById(R.id.list_reward)

        setLayoutManager()

        rewardChangeAdapter = RewardChangeAdapter(WeakReference(applicationContext)) { rc ->
            CoroutineScope(Dispatchers.IO).launch {
                if (rc != null) {
                    rewardChangePresenter.submitChangeReward(rc.id, rc.type)
                }
            }
        }

        rewardChangeRecyclerView.adapter = rewardChangeAdapter

        rewardChangeAdapter.updateRewardList(rewardChangeList.toMutableList())

        CoroutineScope(Dispatchers.IO).launch {
            rewardChangePresenter.getRewardChangeList()
        }

        rootView  = window.decorView.rootView
    }

    fun hideKeyword() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyword()
        onBackPressed()

        return true
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            loading = true

            rewardChangeList.add(null)
            rewardChangeAdapter.addLoadingView(rewardChangeList.toMutableList())
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            loading = false

            val listSize = rewardChangeList.size

            if(listSize > 0) {
                if(rewardChangeList[listSize - 1] == null) {
                    rewardChangeList.removeAt(listSize - 1)
                }
            }

            rewardChangeAdapter.removeLoadingView(rewardChangeList.toMutableList())
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            Snackbar.make(
                rootView,
                msg?:"Terjadi kesalahan.",
                Snackbar.LENGTH_SHORT).setAction("Muat ulang") {
                refreshRewardChange() }.show()

            loading = false
        }
    }

    override suspend fun onSuccess(token: JSONObject) {
        withContext(Dispatchers.Main) {
            refreshRewardChange()

            Snackbar
                .make(
                rootView,
                "Permintaan berhasil, sedang dalam proses",
                Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override suspend fun onGetListSuccess(rewardChangeList: ArrayList<RewardChange>, countStar: Int) {
        this.rewardChangeList.addAll(rewardChangeList)

        withContext(Dispatchers.Main) {
            rewardChangeAdapter.updateRewardList(this@RewardChangeScreen.rewardChangeList.toMutableList())

            countStarText.text = countStar.toString()

            loading = false
        }
    }

    private fun setLayoutManager() {
        layoutManager = LinearLayoutManager(applicationContext)

        rewardChangeRecyclerView.layoutManager = layoutManager

        rewardChangeRecyclerView.setHasFixedSize(true)
    }

    private fun refreshRewardChange() {
        if(!loading) {
            loading = true

            rewardChangeList.clear()
            rewardChangeAdapter.updateRewardList(rewardChangeList.toMutableList())

            CoroutineScope(Dispatchers.IO).launch {
                rewardChangePresenter.getRewardChangeList()
            }
        }
    }
}