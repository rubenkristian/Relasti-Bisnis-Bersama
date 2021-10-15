package com.ethelworld.RBBApp.UI

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ethelworld.RBBApp.Adapter.GenerationAdapter
import com.ethelworld.RBBApp.Item.Generation
import com.ethelworld.RBBApp.Presenter.PartnerGeneration
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.Parent.ParentAppActivity
import com.ethelworld.RBBApp.View.PartnerGenerationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class PartnerGeneration : ParentAppActivity(), PartnerGenerationView.View{
    private val generationList: ArrayList<Generation?> = ArrayList()
    private lateinit var generationAdapter: GenerationAdapter
    private lateinit var layoutManagerRV: RecyclerView.LayoutManager
    private lateinit var partnerGeneration: PartnerGeneration

    var page: Int = 1
    private var loading: Boolean = false

    private lateinit var partnerGenerationRV: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var registerResultActivity: ActivityResultLauncher<Intent>

    private lateinit var pendingAccountScreen: Intent
    private lateinit var invitePartnerScreen: Intent

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        registerResultActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(PartnerAreaScreen.REQUEST_CODE_INPUT, result)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_generation)
        setSupportActionBar(findViewById(R.id.toolbar))

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.title = getString(R.string.partner_area)

        partnerGeneration = PartnerGeneration(this, applicationContext)

        partnerGenerationRV = findViewById(R.id.generation_list)

        setLayoutManager()

        generationAdapter = GenerationAdapter(WeakReference(applicationContext)) { generation ->
            val generationUserList = Intent(this, PartnerAreaScreen::class.java)

            generationUserList.putExtra("title", "Keturunan ${generation?.generationIndex}")
            generationUserList.putExtra("generation", generation?.generationIndex)

            startActivity(generationUserList)
        }

        partnerGenerationRV.adapter = generationAdapter

        generationAdapter.updatePartner(generationList.toMutableList())

        swipeRefreshLayout = findViewById(R.id.refresh_swipe)

        swipeRefreshLayout.setOnRefreshListener {
            refreshPartner()
        }

        findViewById<Button>(R.id.invite_partner).setOnClickListener {
            registerResultActivity.launch(invitePartnerScreen)
        }

        findViewById<Button>(R.id.partner_wait).setOnClickListener {
            startActivity(pendingAccountScreen)
        }

        CoroutineScope(Dispatchers.IO).launch {
            loadFirstPartner()
        }

        invitePartnerScreen     = Intent(this, InputNewAccountScreen::class.java)
        pendingAccountScreen    = Intent(this, PendingAccountScreen::class.java)

        rootView = window.decorView.rootView
    }

    private fun onActivityResult(requestCode: Short, result: ActivityResult) {
        if(requestCode == PartnerAreaScreen.REQUEST_CODE_INPUT) {
            when(result.resultCode) {
                Activity.RESULT_OK -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        refreshPartner()
                    }
                }
            }
        }
    }

    private suspend fun loadFirstPartner() {
        if(!loading) {
            loading = true

            partnerGeneration.getGenerationInfo()
        }
    }

    private fun setLayoutManager() {
        layoutManagerRV = LinearLayoutManager(applicationContext)

        partnerGenerationRV.apply {
            layoutManager = layoutManagerRV

            setHasFixedSize(true)
        }
    }

    private fun refreshPartner() {
        if(!loading) {
            loading = true
            page    = 0

            generationList.clear()

            generationAdapter.updatePartner(generationList.toMutableList())

            CoroutineScope(Dispatchers.IO).launch {
                partnerGeneration.getGenerationInfo()
            }
        }

        swipeRefreshLayout.isRefreshing = false
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            loading = true

            generationList.add(null)
            generationAdapter.addLoadingView(generationList.toMutableList())
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            val listSize = generationList.size

            if(listSize > 0) {
                if(generationList[listSize - 1] == null) {
                    generationList.removeAt(listSize - 1)
                }
            }

            generationAdapter.removeLoadingView(generationList.toMutableList())
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            val snackbar = Snackbar
                .make(rootView, msg?:"Terjadi kesalahan.", Snackbar.LENGTH_SHORT)

            snackbar.setAction("Muat ulang") { refreshPartner() }.show()
        }
    }

    override suspend fun onSuccess(generations: ArrayList<Generation?>) {
        generationList.addAll(generations)

        withContext(Dispatchers.Main) {
            generationAdapter.updatePartner(generationList.toMutableList())

//            partnerGenerationRV.post {
//                generationAdapter.notifyDataSetChanged()
//            }

            loading = false
            page++
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        partnerGeneration.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }
}