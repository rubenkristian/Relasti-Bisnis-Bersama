package com.ethelworld.RBBApp.Service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class ContactSyncService: Service(), CoroutineScope {
    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob
//    private lateinit var api: ReqAPI
//    private lateinit var auth: Authentication
//
//    private var synccount: Int = 0
//    private var lastsync: String = ""

    override fun onCreate() {
        super.onCreate()
//        api = ReqAPI(applicationContext)
//        auth = Authentication(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        runBlocking {
//            val data: HashMap<String, String> = HashMap()
//            synccount = auth.getValueInt(Authentication.SYNCCOUNT)
//            lastsync = auth.getValueString(Authentication.LASTSYNC)!!
//
//            data["id"] = auth.getValueInt(Authentication.ID).toString()
//            data["synccount"] = synccount.toString()
//            data["lasysync"] = lastsync
//            val requestResult = api.requestAPI(
//                ContactPresenter.TAG,
//                Request.Method.POST,
//                "${ReqAPI.apiPoint}synccontacts",
//                data)
//            if(requestResult.error!= null){
//                val error = Intent()
//                error.action = "SYNC_ERROR"
//                error.putExtra("message", requestResult.error.message)
//                sendBroadcast(error)
//            } else {
//                onSuccess(requestResult.response)
//            }
//        }
        return START_NOT_STICKY
    }

    suspend fun onSuccess(result: String) {
//        val ethelDB = EthelDBHelper(applicationContext)
//        val contacts = ArrayList<Contact?>()
//
//        val resjson: JSONObject
//        try {
//            resjson = JSONObject(result)
//            if(resjson.getBoolean("status")) {
//                val newContacts = resjson.getJSONArray("contacts")
//                for (i in 0 until newContacts.length()) {
//                    val contact = newContacts.getJSONObject(i)
//
//                    val contactItem = ethelDB.addContact(
//                        contact.getLong("id"),
////                        contact.getString("img"),
//                        "",
//                        contact.getString("fn"),
//                        contact.getString("occ"),
//                        contact.getString("com"),
//                        contact.getString("pn"),
//                        contact.getString("cn"),
//                        contact.getString("wa")
//                    )
//                    contacts.add(contactItem)
//                }
//                auth.save(Authentication.SYNCCOUNT, (synccount + 1))
//
//                auth.save(Authentication.LASTSYNC, resjson.getString("last_sync"))
//
//                val success = Intent()
//                success.action = "SYNC_SUCCESS"
//                sendBroadcast(success)
//            } else{
//                val error = Intent()
//                error.action = "SYNC_FAILED"
//                error.putExtra("message", resjson.getString("msg"))
//                sendBroadcast(error)
//            }
//        } catch (e: JSONException) {
//            val error = Intent()
//            error.action = "SYNC_ERROR"
//            error.putExtra("message", e.message)
//            sendBroadcast(error)
//        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}