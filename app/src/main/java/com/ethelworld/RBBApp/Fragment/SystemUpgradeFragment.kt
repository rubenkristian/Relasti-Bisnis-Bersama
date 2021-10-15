package com.ethelworld.RBBApp.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.ethelworld.RBBApp.BuildConfig
import com.ethelworld.RBBApp.Item.TotalBonus
import com.ethelworld.RBBApp.Presenter.TotalBonusPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.*
import com.ethelworld.RBBApp.View.TotalBonusView
import com.ethelworld.RBBApp.tools.OnPageListener
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class SystemUpgradeFragment : Fragment(), TotalBonusView.View, OnPageListener {
    private var isLoadingFirst = false

    private var mRewardedAd: RewardedAd? = null

    private lateinit var totalBonusPresenter: TotalBonusPresenter
    private lateinit var totalBonusOutput: TextView
    private lateinit var totalStarOutput: TextView
    private lateinit var rewardedButton: MaterialButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    //loading
    private lateinit var loadingCashBack: ProgressBar
    private lateinit var loadingStar: ProgressBar

    private val localID: Locale = Locale("in", "ID")
    private lateinit var rpFormat: NumberFormat

    private lateinit var registerResultActivity: ActivityResultLauncher<Intent>

    private lateinit var customReward: View

    override fun onCreate(savedInstanceState: Bundle?) {
        registerResultActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if(result.resultCode == Activity.RESULT_OK) {
                    loadInfo()
                }
            }

        super.onCreate(savedInstanceState)

        rpFormat = NumberFormat.getCurrencyInstance(localID)

        rpFormat.maximumFractionDigits = 0

        customReward = layoutInflater.inflate(R.layout.reward_alert, null)
    }

    private lateinit var historyCashbackGeneration: Intent
    private lateinit var withdrawalScreen: Intent
    private lateinit var withdrawHistoryScreen: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_system_upgrade, container, false)

        val adRequest = AdRequest.Builder().build()
//        var rewardID = "ca-app-pub-3940256099942544/5224354917"
        var rewardID = "ca-app-pub-6288822850551593/2151184680"

        if(!BuildConfig.DEBUG) {
            rewardID = "ca-app-pub-6288822850551593/2151184680"
        }

        RewardedAd.load(requireContext(), rewardID, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(rewardedAd: RewardedAd) {
//                view?.let { v ->
//                    Snackbar.make(v, "Ad was loaded.", Snackbar.LENGTH_LONG).show()
//                }
                mRewardedAd = rewardedAd
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
//                view?.let { v ->
//                    Snackbar.make(v, "Ad failed loaded ${adError.message}.", Snackbar.LENGTH_LONG).show()
//                }
                mRewardedAd = null
            }
        })

        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                view?.let {
                    Snackbar.make(it, "Ad was dismissed", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError?) {
                view?.let {
                    Snackbar.make(it, "Ad failed to show.", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onAdShowedFullScreenContent() {
                view?.let {
                    Snackbar.make(it, "Ad showed fullscreen content.", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        val cashbackGeneration = view.findViewById<CardView>(R.id.cashback)

        cashbackGeneration.setOnClickListener {
            startActivity(historyCashbackGeneration)
        }

        val withdrawal = view.findViewById<MaterialButton>(R.id.withdrawal)

        withdrawal.setOnClickListener {
            registerResultActivity.launch(withdrawalScreen)
//            startActivity(withdrawalScreen)
        }

        val history = view.findViewById<MaterialButton>(R.id.withdrawal_history)

        history.setOnClickListener {
            startActivity(withdrawHistoryScreen)
        }

        loadingCashBack     = view.findViewById(R.id.loading_cashback)
        loadingStar         = view.findViewById(R.id.loading_star)
        swipeRefreshLayout  = view.findViewById(R.id.refresh_swipe)
        totalBonusOutput    = view.findViewById(R.id.total_cashback)
        totalStarOutput     = view.findViewById(R.id.total_star)

        val backgroundImage = view.findViewById<ImageView>(R.id.background_image)

        rewardedButton = view.findViewById(R.id.rewarded_ads)

        rewardedButton.setOnClickListener {
            if(mRewardedAd != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    totalBonusPresenter.checkLastTimeAdsWatch()
                }
            } else {
                view?.let { v ->
                    Snackbar.make(v, "Gagal memuat, periksa koneksi internet.", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        activity?.let { Glide.with(it).load(R.drawable.appbg).into(backgroundImage) }

        withdrawHistoryScreen       = Intent(context, WithdrawHistoryScreen::class.java)
        historyCashbackGeneration   = Intent(context, CashbackGenerationScreen::class.java)
        withdrawalScreen            = Intent(context, Withdrawal::class.java)

        swipeRefreshLayout.setOnRefreshListener {
            loadInfo()
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        if(!isLoadingFirst) {
            totalBonusPresenter = context?.let { TotalBonusPresenter(this, it) }!!

            loadInfo()

            isLoadingFirst = true
        }
    }

    private fun loadInfo() {
        totalBonusOutput.visibility = View.GONE
        totalStarOutput.visibility  = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            totalBonusPresenter.getTotalBonus()
        }
    }

    override fun OnLoad() {
        if(!isLoadingFirst) {
            loadInfo()

            isLoadingFirst = true
        }
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            swipeRefreshLayout.isRefreshing = false

            loadingCashBack.visibility  = View.VISIBLE
            loadingStar.visibility      = View.VISIBLE
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            loadingCashBack.visibility  = View.GONE
            loadingStar.visibility      = View.GONE
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            view?.let {
                Snackbar
                    .make(it, msg?:"Terjadi kesalahan", Snackbar.LENGTH_SHORT)
                    .setAction("Muat ulang") {
                        loadInfo()
                    }
                    .show()
            }
        }
    }

    override suspend fun onSuccess(totalBonus: TotalBonus) {
        withContext(Dispatchers.Main) {
            totalBonusOutput.visibility = View.VISIBLE
            totalStarOutput.visibility  = View.VISIBLE

            totalBonusOutput.text   = rpFormat.format(totalBonus.total)
            totalStarOutput.text    = totalBonus.star.toString()
        }
    }

    override suspend fun onRewardSuccess(total: Long, received: Long) {
        withContext(Dispatchers.Main) {
            showRewardResult("Kamu mendapatkan $received bintang", "Total $total bintang")

            rewardedButton.isEnabled = false
        }
    }

    override suspend fun onCheckLastAdsWatched(status: Boolean, msg: String?) {
        withContext(Dispatchers.Main) {
            if(status) {
                mRewardedAd?.show(requireActivity()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        totalBonusPresenter.getReward()
                    }
                }
            } else {
                view?.let {
                    if (msg != null) {
                        Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun showRewardResult(msg: String, total: String) {
        Glide.with(requireActivity()).load(R.drawable.star_reward).into(customReward.findViewById(R.id.star_reward))

        customReward.findViewById<TextView>(R.id.text_reward).text          = msg
        customReward.findViewById<TextView>(R.id.text_total_reward).text    = total

        val rewardPanel = AlertDialog
            .Builder(requireContext())
            .setView(customReward)

        val dialog = rewardPanel.create()

        dialog.show()

        customReward.findViewById<MaterialButton>(R.id.dismiss_reward).setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        totalBonusPresenter.onDestroy()
    }
}