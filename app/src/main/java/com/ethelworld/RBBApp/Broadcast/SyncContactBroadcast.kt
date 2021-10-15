package com.ethelworld.RBBApp.Broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ethelworld.RBBApp.View.ContactView

class SyncContactBroadcast(private val view: ContactView.View): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
//        if (intent != null) {
//            if(intent.action.equals("SYNC_SUCCESS")) {
//            } else {
//
//            }
//        }
    }
}