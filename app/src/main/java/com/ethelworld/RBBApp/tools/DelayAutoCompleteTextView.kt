package com.ethelworld.RBBApp.tools

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar


class DelayAutoCompleteTextView(
    context: Context,
    attrs: AttributeSet?
) :
    androidx.appcompat.widget.AppCompatAutoCompleteTextView(context, attrs) {
    private var mAutoCompleteDelay: Long =
        DEFAULT_AUTOCOMPLETE_DELAY
    private var mLoadingIndicator: ProgressBar? = null

    private val mHandler: Handler = Handler(Handler.Callback { msg ->
        super@DelayAutoCompleteTextView.performFiltering(
            msg.obj as CharSequence,
            msg.arg1
        )
        true
    })

    fun setLoadingIndicator(progressBar: ProgressBar?) {
        mLoadingIndicator = progressBar
    }

    fun setAutoCompleteDelay(autoCompleteDelay: Long) {
        mAutoCompleteDelay = autoCompleteDelay
    }

    override fun performFiltering(text: CharSequence, keyCode: Int) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator?.visibility = View.VISIBLE
        }
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED)
        mHandler.sendMessageDelayed(
            mHandler.obtainMessage(
                MESSAGE_TEXT_CHANGED,
                text
            ), mAutoCompleteDelay
        )
    }

    override fun onFilterComplete(count: Int) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator?.visibility = View.GONE
        }
        super.onFilterComplete(count)
    }

    companion object {
        private const val MESSAGE_TEXT_CHANGED = 100
        private const val DEFAULT_AUTOCOMPLETE_DELAY: Long = 750

    }
}