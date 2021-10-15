package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.ethelworld.RBBApp.Database.EthelDBHelper
import com.ethelworld.RBBApp.Item.UserContact
import com.ethelworld.RBBApp.View.ContactRecoveryView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.DownloadFile
import com.ethelworld.RBBApp.tools.network.OnDownloadContactListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import java.util.*

class ContactRecoveryPresenter(
    private var view: ContactRecoveryView.View?,
    private var context: Context?,
    private var ethelDBHelper: EthelDBHelper?
): ContactRecoveryView.Presenter, OnDownloadContactListener {
    override fun contactRecovery() {
        view?.showLoading()

        val auth    = Authentication(context)
        val referal = auth.getValueString(Authentication.REFERAL)?.toLowerCase(Locale.ROOT)
        val token   = auth.getValueString(Authentication.TOKEN)

        val download = DownloadFile(
            this,
            "${ReqAPI.apiPoint}recover?id_member=$referal",
            token,
            context?.filesDir
        )

        download.execute()
    }

    override fun updateProgress(progress: Int?, status: Int?) {
        val textStatus = arrayOf(
            "Mengunduh file pemulihan kontak...",
            "Melakukan pemulihan kontak...",
            "Pemulihan kontak selesai")

        view?.progressLoading(progress, textStatus[status!!])
    }

    override fun addContact(contact: UserContact) {
        ethelDBHelper?.addContact(
            contact.id,
            "",
            contact.name,
            contact.occupation,
            contact.company,
            contact.province,
            contact.city,
            contact.wa)
    }

    override fun onSuccess(filename: String?) {
        view?.hideLoading()
        view?.onSuccess()
    }

    fun onDestroy() {
        view = null
        ethelDBHelper = null
    }
}