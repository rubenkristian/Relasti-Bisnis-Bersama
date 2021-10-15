package com.ethelworld.RBBApp.Fragment

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.ethelworld.RBBApp.Presenter.HomePresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.DetailAds
import com.ethelworld.RBBApp.View.HomeView
import com.ethelworld.RBBApp.tools.OnPageListener
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), OnPageListener, HomeView.View {
    private lateinit var pdfDownloadButton: MaterialButton
    private lateinit var token: String

    private lateinit var imageSlider: ImageSlider

    private lateinit var detailAds: Intent

    private lateinit var homePresenter: HomePresenter

    private val imagesList: ArrayList<SlideModel> = ArrayList()

    private lateinit var snackbar: Snackbar

    private var firstLoad = false

    private lateinit var customReward: View

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val authentication  = Authentication(context)

        val accountName     = view.findViewById<TextView>(R.id.name_account)
        val accountReferal  = view.findViewById<TextView>(R.id.referal_account)
        val backgroundImage = view.findViewById<ImageView>(R.id.background_image)

        val greeting        = authentication.getValueString(Authentication.NAME)
        val referal         = authentication.getValueString(Authentication.REFERAL)

        customReward = layoutInflater.inflate(R.layout.reward_alert, null)

        accountName.text    = resources.getString(R.string.greeting, greeting)
        accountReferal.text = referal?.toUpperCase(Locale.ROOT)

        activity?.let { Glide.with(it).load(R.drawable.appbg).into(backgroundImage) }

        token = Authentication(context).getValueString(Authentication.TOKEN).toString()

        pdfDownloadButton = view.findViewById(R.id.download_pdf)

        pdfDownloadButton.setOnClickListener {
            val uri             = Uri.parse("https://rbb-world.com/document.html")
            val openDownload    = Intent(Intent.ACTION_VIEW, uri)

            startActivity(openDownload)
        }

        imageSlider = view.findViewById(R.id.image_slider)
        detailAds   = Intent(context, DetailAds::class.java)

        return view
    }

    override fun onStart() {
        super.onStart()

        if(!firstLoad) {
            firstLoad = true

            homePresenter = HomePresenter(this, context)

            CoroutineScope(Dispatchers.IO).launch {
                homePresenter.getImageAssetSlider()
            }

            CoroutineScope(Dispatchers.IO).launch {
                homePresenter.getBonusReward()
            }
        }
    }

    override fun OnLoad() {
    }

    override suspend fun showLoading() {
        CoroutineScope(Dispatchers.Main).launch {

        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {

        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            if (msg != null) {
                snackbar = view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_LONG) }!!

                with(snackbar) {
                    animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    show()
                }
            }
        }
    }

    override suspend fun onSuccess(images: JSONArray) {
        for (i in 0 until images.length()) {
            val image = images.getJSONObject(i)

            imagesList.add(
                SlideModel(
                    image.getString("url"),
                    image.getString("ctx"))
            )
        }

        withContext(Dispatchers.Main) {
            imageSlider.setImageList(imagesList, ScaleTypes.CENTER_CROP)

            imageSlider.setItemClickListener(object: ItemClickListener {
                override fun onItemSelected(position: Int) {
                    detailAds.putExtra("url", imagesList[position].imageUrl)
                    startActivity(detailAds)
                }
            })
        }
    }

    override suspend fun onBonusSuccess(reward: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            if (msg != null) {
                showRewardResult(msg, reward)
            }
        }
    }

    override suspend fun onClaimBonusSuccess(msg: String?) {
        withContext(Dispatchers.Main) {
            if (msg != null) {
                showClaimSuccess(msg)
            }
        }
    }

    fun showRewardResult(msg: String, reward: Int) {
        Glide.with(requireActivity()).load(R.drawable.gold_reward).into(customReward.findViewById(R.id.star_reward))
        val topText     = customReward.findViewById<TextView>(R.id.text_reward)
        val bottomText  = customReward.findViewById<TextView>(R.id.text_total_reward)

        topText.textSize = 48F
        topText.text = "Selamat!"
        bottomText.text = msg

        val rewardPanel = AlertDialog
            .Builder(requireContext())
            .setView(customReward)
            .setCancelable(false)

        dialog = rewardPanel.create()

        dialog.show()

        val rewardbtn = customReward.findViewById<MaterialButton>(R.id.dismiss_reward)

        rewardbtn.text = "Claim"

        rewardbtn.setOnClickListener {
            rewardbtn.text = "Memuat"
            rewardbtn.isEnabled = false
            CoroutineScope(Dispatchers.IO).launch {
                homePresenter.claimBonusReward(reward)
            }
        }
    }

    fun showClaimSuccess(msg: String) {
        dialog.dismiss()

        snackbar = view?.let { Snackbar.make(it, "Claim berhasil", Snackbar.LENGTH_LONG) }!!

        with(snackbar) {
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        homePresenter.onDestroy()
    }
}