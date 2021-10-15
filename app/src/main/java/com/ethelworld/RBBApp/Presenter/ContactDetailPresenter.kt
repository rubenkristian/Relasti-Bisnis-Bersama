package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.DBInterface.OnContactSelected
import com.ethelworld.RBBApp.Database.EthelDBHelper
import com.ethelworld.RBBApp.Item.Contact
import com.ethelworld.RBBApp.View.ContactDetailView
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import com.ethelworld.RBBApp.tools.network.RequestResult
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ContactDetailPresenter(
    private var view: ContactDetailView.View?,
    private val context: Context?):
    ContactDetailView.Presenter,
    OnContactSelected,
    OnStringListener {

    private var api: ReqAPI = ReqAPI(context)

    companion object{
        const val TAG = "OTHERINFOREQ"
    }

    override suspend fun getContactDetail(id: Long?) {
        view?.showLoading()

        val ethelDB = EthelDBHelper(context)

        ethelDB.getContactDetail(id!!, this)
    }

    override suspend fun getOtherInfo(id: Long?) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        val requestResult: RequestResult = api.requestAPI(
            TAG,
            Request.Method.GET,
            "${ReqAPI.apiPoint}otherinfo?id=$id",
            data,
            TimeUnit.MILLISECONDS.convert(
                30,
                TimeUnit.MINUTES))

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun onContactSelected(contact: Contact) {
        view?.hideLoading()
        view?.onSuccess(contact)
    }

    override suspend fun onErrorContact(errmsg: String) {
        view?.hideLoading()
        view?.showError(errmsg)
    }

    override suspend fun onSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                view?.onOthweInfoSuccess(resjson.getJSONObject("data"))
            } else {
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onError(error: VolleyError?) {
        view?.hideLoading()
        view?.showError(error?.message)
    }

    override suspend fun onError(code: Int, errmsg: String?) {
        view?.hideLoading()
        view?.showError(errmsg)
    }

    override suspend fun onError(errmsg: String?) {
        view?.hideLoading()
        view?.showError(errmsg)
    }

    public fun onDestroy() {
        view = null
        api.cancelRequest(TAG)
    }
}