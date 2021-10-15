package com.ethelworld.RBBApp.tools.user

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class UserData {
    val USER_ID = "USERID"
    val USER_NAME = "USERNAME"
    val USER_PHONE_NUMBER = "USERPHONENUMBER"
    val USER_LOGGED = "USERLOGGED"
    val USER_STATUS = "USERSTATUS"
    val USER_STATUS_EXPIRED = "USERSTATUSEXPIRED"
    val USER_OCCUPATION = "USEROCCUPATION"
    val USER_STATUS_TAC = "USERSTATUSTAC"

    fun defaultPreference(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun customPreference(context: Context, name: String): SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit){
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.username
        get() = getString(USER_NAME, "")
        set(value) {
            editMe {
                it.putString(USER_NAME, value)
            }
        }

    var SharedPreferences.phonenumber
        get() = getString(USER_PHONE_NUMBER, "")
        set(value) {
            editMe {
                it.putString(USER_PHONE_NUMBER, value)
            }
        }

    var SharedPreferences.userlogged
        get() = getBoolean(USER_LOGGED, false)
        set(value) {
            editMe {
                it.putBoolean(USER_LOGGED, value)
            }
        }

    var SharedPreferences.userstatus
        get() = getInt(USER_STATUS, 0)
        set(value) {
            editMe {
                it.putInt(USER_STATUS, value)
            }
        }

    var SharedPreferences.userid
        get() = getInt(USER_ID, 0)
        set(value) {
            editMe {
                it.putInt(USER_ID, value)
            }
        }

    var SharedPreferences.userstatusexpired
        get() = getString(USER_STATUS_EXPIRED, "")
        set(value) {
            editMe {
                it.putString(USER_STATUS_EXPIRED, value)
            }
        }

    var SharedPreferences.useroccupation
        get() = getString(USER_OCCUPATION, "")
        set(value) {
            editMe {
                it.putString(USER_OCCUPATION, value)
            }
        }

    var SharedPreferences.userstatustac
        get() = getBoolean(USER_STATUS_TAC, false)
        set(value) {
            editMe {
                it.putBoolean(USER_STATUS_TAC, value)
            }
        }
}