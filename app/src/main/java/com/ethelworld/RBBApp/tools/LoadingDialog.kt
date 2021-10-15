package com.ethelworld.RBBApp.tools

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.ethelworld.RBBApp.R

class LoadingDialog(private val context: Context) {
    var alertDialog: AlertDialog? = null

    fun startLoadingDialog(isCancelAble: Boolean) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.AlertDialogCustom)

        val inflater: LayoutInflater = LayoutInflater.from(context)
        builder.setView(inflater.inflate(R.layout.loading_dialog, null))
        builder.setCancelable(isCancelAble)

        alertDialog = builder.create()
        alertDialog?.show()
    }

    fun dismissDialog() {
        alertDialog?.dismiss()
    }
}