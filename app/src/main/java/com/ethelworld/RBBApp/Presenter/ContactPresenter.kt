package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.DBInterface.OnSelected
import com.ethelworld.RBBApp.Database.EthelDBHelper
import com.ethelworld.RBBApp.Item.Contact
import com.ethelworld.RBBApp.View.ContactView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject

class ContactPresenter(
    private var view: ContactView.View?,
    private var context: Context?):
    ContactView.Presenter,
    OnStringListener,
    OnSelected {
    private var api: ReqAPI = ReqAPI(context)
    private val limit: Int = 20

    private var synccount: Int = 0
    private var lastsync: String = ""

    private var auth: Authentication? = null

    companion object {
        const val TAG = "CONTACTREQ"
    }

    init {
        auth = Authentication(context)
    }

    override suspend fun syncContactFromServer() {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        synccount = auth?.getValueInt(Authentication.SYNCCOUNT)!!
        lastsync = auth?.getValueString(Authentication.LASTSYNC)!!

//        Log.i("CONTACT", lastsync)
        data["id"] = auth?.getValueInt(Authentication.ID).toString()
        data["synccount"] = synccount.toString()
        data["lasysync"] = lastsync

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}synccontacts",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun listContactLocal(search: String?, page: Int) {
//        Log.i("CONTACPRESENTER", "LOCAL CONTACT")
        val ethelDB = EthelDBHelper(context)

        ethelDB.getListContact(
            search,
            (page - 1)*limit,
            limit,
            this)
    }

    override suspend fun onSuccess(result: String) {
        val ethelDB     = EthelDBHelper(context)
        val contacts    = ArrayList<Contact?>()

        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val newContacts = resjson.getJSONArray("contacts")

                for (i in 0 until newContacts.length()) {
                    val contact = newContacts.getJSONObject(i)

                    val contactItem = ethelDB.addContact(
                        contact.getLong("id"),
//                        contact.getString("img"),
                        "",
                        contact.getString("fn"),
                        contact.getString("occ"),
                        contact.getString("com"),
                        contact.getString("pn"),
                        contact.getString("cn"),
                        contact.getString("wa")
                    )

                    contacts.add(contactItem)
                }

                auth?.save(Authentication.SYNCCOUNT, (synccount + 1))
//                Log.i("CONTACT", resjson.getString("last_sync"))
                auth?.save(Authentication.LASTSYNC, resjson.getString("last_sync"))
                view?.onSuccess(contacts)
//                Log.i("CONTACPRESENTER", "On Success CONTACT SERVER")
            } else{
//                Log.i("CONTACPRESENTER", "ERROR SERVER")
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
//            Log.i("CONTACPRESENTER", "ERROR JSON")
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onError(error: VolleyError?) {
        view?.hideLoading()
        view?.showError(error.hashCode(), error?.message)
    }

    override suspend fun onError(code: Int, errmsg: String?) {
        view?.hideLoading()
        view?.showError(code, errmsg)
    }

    override suspend fun onError(errmsg: String?) {
        view?.hideLoading()
        view?.showError(500, errmsg)
    }

    override suspend fun onSelectedFinish(contacts: ArrayList<Contact?>) {
//        Log.i("CONTACPRESENTER", "SELECT FINISH")
        view?.hideLoading()
        view?.onSuccessLocal(contacts)
    }

    override suspend fun onEnd() {
//        Log.i("CONTACPRESENTER", "END")
        view?.hideLoading()
        view?.onContactLocalEnd()
    }

    fun onDestroy() {
        view    = null
        context = null

        api.cancelRequest(TAG)
    }
}