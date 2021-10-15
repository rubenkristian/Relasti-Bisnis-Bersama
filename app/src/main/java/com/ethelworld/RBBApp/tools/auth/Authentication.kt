package com.ethelworld.RBBApp.tools.auth

import android.content.Context
import android.content.SharedPreferences

class Authentication(val context: Context?) {
    companion object{
        const val PREFS_NAME = "kotlincodes"
        const val TOKEN: String = "token"
        const val TAC: String = "tac"
        const val NAME: String = "name"
        const val WA: String = "wa"
        const val OCCUPATION: String = "occupation"
        const val ADDRESS: String = "address"
        const val EXPIRED: String = "expired"
        const val REFERAL: String = "referal"
        const val TYPE: String = "type"
        const val ID: String = "id"
        const val RECOVERY: String = "recovery"
        const val CONTACT_READ_GRANTED: String = "contact_granted"
        const val IDLASTREGISTER: String = "idlastregister"
        const val SYNCCOUNT: String = "synccount"
        const val LASTSYNC: String = "lastsync"
        const val LASTADSWATCH: String = "lastadswatch"
        const val CURRENTTIME: String = "currenttime"
        const val UPDATEITEM: String = "updateitem"
        const val STARITEM: String = "staritem"
    }
    private val sharedPref: SharedPreferences = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)!!

    suspend fun checkLog(onAuthListener: OnAuthListener){
        if(isLogged()){
            if(isTACAgreed()){
                onAuthListener.onLogged()
                return
            }
            onAuthListener.tacNotSigned()
        }else{
            onAuthListener.notLogged()
        }
    }

    fun isLogged(): Boolean {
        return if(!sharedPref.contains(TOKEN)){
            false
        }else sharedPref.getString(TOKEN, null) != null
    }

    fun isTACAgreed(): Boolean {
        return if(!sharedPref.contains(TAC)) {
            false
        }else sharedPref.getBoolean(TAC, false)
    }

    fun isContactGranted(): Boolean {
        return if(!sharedPref.contains(CONTACT_READ_GRANTED)) {
            false
        }else sharedPref.getBoolean(CONTACT_READ_GRANTED, false)
    }

    fun isRecoveryDone(): Boolean {
        return if(!sharedPref.contains(RECOVERY)) {
            false
        } else sharedPref.getBoolean(RECOVERY, false)
    }

    fun getStarCount(): Long {
        return if(!sharedPref.contains(STARITEM)) {
            0
        } else sharedPref.getLong(STARITEM, 0)
    }

    fun save(KEY_NAME: String, text: String) {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putString(KEY_NAME, text)

        editor.apply()
    }

    fun save(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putInt(KEY_NAME, value)

        editor.apply()
    }

    fun save(KEY_NAME: String, value: Long) {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putLong(KEY_NAME, value)

        editor.apply()
    }

    fun save(KEY_NAME: String, status: Boolean) {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putBoolean(KEY_NAME, status)

        editor.apply()
    }

    fun getValueString(KEY_NAME: String): String? {

        return sharedPref.getString(KEY_NAME, "")
    }

    fun getValueInt(KEY_NAME: String): Int {

        return sharedPref.getInt(KEY_NAME, 0)
    }

    fun getValueLong(KEY_NAME: String): Long {
        return sharedPref.getLong(KEY_NAME, 0)
    }

    fun getValueBoolean(KEY_NAME: String, defaultValue: Boolean): Boolean {
        return sharedPref.getBoolean(KEY_NAME, defaultValue)
    }

    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        editor.clear()
        editor.apply()
    }

    fun removeValue(KEY_NAME: String) {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.remove(KEY_NAME)
        editor.apply()
    }
}