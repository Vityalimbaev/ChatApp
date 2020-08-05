package com.example.chatapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat


const val KEY_USER_NAME = "userName"
const val KEY_RECIPIENT_ID = "recipientId"
const val KEY_SENDER_ID = "senderId"
const val KEY_RECIPIENT_NAME = "recipientName"

data class IntentData(val key:String, val value:String)

fun <T> startNewActivity(context: Context, activity : Class<T>, vararg intentValues: IntentData){
    val intent = Intent(context, activity)
    for (values in intentValues){
        intent.putExtra(values.key, values.value)
    }
    context.startActivity(intent)
}

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}