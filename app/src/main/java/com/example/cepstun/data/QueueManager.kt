package com.example.cepstun.data

import android.content.Context
import com.example.cepstun.data.local.Queue

object QueueManager {
    private const val BARBER_ID = "barberId"
    private const val YOUR_QUEUE = "yourQueue"
    private const val PREFS_NAME = "favoritesPrefs"

    private fun getPreferences(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun addQueue(context: Context, queue: Queue) {
        val editor = getPreferences(context).edit()
        editor.putString(BARBER_ID, queue.barberID)
        editor.putString(YOUR_QUEUE, queue.yourQueue)
        editor.apply()
    }

    fun getQueue(context: Context): Queue {
        val preferences = getPreferences(context)
        val barberId = preferences.getString(BARBER_ID, null)
        val yourQueue = preferences.getString(YOUR_QUEUE, null)
        return Queue(barberId?:"", yourQueue?:"")
    }

    fun deleteQueue(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(BARBER_ID)
        editor.remove(YOUR_QUEUE)
        editor.apply()
    }
}