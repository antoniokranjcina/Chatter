package com.ak.chatter.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardBehaviour {

    fun showKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus: View? = activity.currentFocus
        if (currentFocus != null) {
            inputMethodManager.showSoftInput(currentFocus, 0)
        }
    }

    fun closeKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus: View? = activity.currentFocus
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            currentFocus.clearFocus()
        }
    }

}