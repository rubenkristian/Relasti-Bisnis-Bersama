package com.ethelworld.RBBApp.tools

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import java.lang.StringBuilder
import java.lang.ref.WeakReference

class LinkTextWatcher(private val hostName: String, private val editTextWeakReference: WeakReference<TextInputEditText>): TextWatcher {
    private var editing: Boolean = false
    override fun afterTextChanged(s: Editable?) {
        val editText = editTextWeakReference.get()
        if(!editing) {
            editing = true
            var username = s!!.toString().replace(hostName, "")

            if(username.isBlank()) {
                val original = StringBuilder(hostName).insert(hostName.length, "/")
                editText?.setText(original)
                editText?.setSelection(original.length)
            } else {
                username = StringBuilder(username).insert(0, hostName).toString()
                editText?.setText(username)
                editText?.setSelection(username.length)
            }
            editing = false
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}