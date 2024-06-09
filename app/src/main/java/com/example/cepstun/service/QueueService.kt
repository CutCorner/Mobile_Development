package com.example.cepstun.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentResolver.SCHEME_ANDROID_RESOURCE
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.local.room.CustomerDatabase
import com.example.cepstun.ui.activity.BarberLocationActivity
import com.example.cepstun.viewModel.BarberLocationViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.io.IOException

class QueueService : LifecycleService() {

    private lateinit var viewModel: BarberLocationViewModel
    private lateinit var database: RepositoryDatabase
    private lateinit var history: RepositoryHistory

    private lateinit var yourQueue: String
    private lateinit var barberId: String

    private var mediaPlayer2: MediaPlayer? = null

    var stop : Boolean = false
    private var isReady2: Boolean = false

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "CutCorner Queue"

        const val YOUR_QUEUE = "your_queue"
        const val BARBER_ID = "barber_id"
    }

    override fun onCreate() {
        super.onCreate()
        database = RepositoryDatabase.getInstance(Firebase.database)
        val room = CustomerDatabase.getInstance(this)
        val histCus = room.historyCustomerDao()
        history = RepositoryHistory.getInstance(histCus)
        viewModel = BarberLocationViewModel(history, database, application)

        init()
    }

    private fun init() {
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        mediaPlayer2 = MediaPlayer()
        mediaPlayer2?.also{
            it.setAudioAttributes(attribute)
            it.isLooping = true
        }

        val afd2 = applicationContext.resources.openRawResourceFd(R.raw.cut_corner_bell)
        try {
            mediaPlayer2?.setDataSource(afd2.fileDescriptor, afd2.startOffset, afd2.length)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mediaPlayer2?.setOnPreparedListener {
            mediaPlayer2?.start()
            isReady2 = true
        }
        mediaPlayer2?.setOnErrorListener { _, _, _ -> false }
    }

    inner class LocalBinder : Binder() {
        fun getService(): QueueService = this@QueueService
    }

    @SuppressLint("MissingSuperCall")
    override fun onBind(intent: Intent): IBinder {
        return LocalBinder()
    }

    @SuppressLint("MissingSuperCall")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (mediaPlayer2?.isPlaying == true){
            stopSoundNextQueue()
        } else {
            init()
        }
        viewModel.combinedQueueData.removeObservers(this)

        if (intent?.extras == null) {
            viewModel.getQueue().also {
                barberId = it.barberID
                yourQueue = it.yourQueue
            }
        } else {
            barberId = intent.getStringExtra(BARBER_ID)!!
            yourQueue = intent.getStringExtra(YOUR_QUEUE)!!
        }

        var notification = buildNotification("Masuk Dalam Antrian")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Log.d("Start Forground", "Untuk Android diatas Upside Down")
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            Log.d("Start Forground", "Untuk Android dibawahnya Upside Down")
            startForeground(NOTIFICATION_ID, notification)
        }

        viewModel.observeQueue(barberId, yourQueue.toInt())

        viewModel.combinedQueueData.observeForever { (currentQueue, remainingQueue) ->
            Log.d("QueueService", "observe berjalan berapa kali??")

            if (currentQueue == -1){
                stopSoundNextQueue()
                stop = true
                notification = buildNotification(
                    "Anda Keluar Antrian"
                )

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    stopForeground(STOP_FOREGROUND_DETACH)
                } else {
                    stopForeground(true)
                }
                stopSelf()
//                stopSound()
            } else {

                notification = buildNotification(
                   "Sisa Antrian : ${remainingQueue}. Antrian anda $yourQueue dan posisi sekarang $currentQueue"
                )

                if (remainingQueue == 1){
                    if (!stop){
                        startSoundNextQueue(remainingQueue)
                    } else {
                        stopSoundNextQueue()
                    }
                    notification = buildNotification(
                        "Sisa Antrian : ${remainingQueue}. Antrian anda $yourQueue dan posisi sekarang $currentQueue. Segera ke tempat pangkas!!"
                    )
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                Log.d("Start Forground", "Untuk Android diatas Upside Down")
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                Log.d("Start Forground", "Untuk Android dibawahnya Upside Down")
                startForeground(NOTIFICATION_ID, notification)
            }

        }
        return START_STICKY
    }
    fun startSoundNextQueue(remainingQueue: Int){
//        if (remainingQueue == 1 && !stop && mediaPlayer2?.isPlaying == false) {
        if (remainingQueue == 1 && !stop ) {
            if (mediaPlayer2?.isPlaying == true) {
//                mediaPlayer2?.stop()
                mediaPlayer2?.pause()
//                mediaPlayer2?.reset()
            } else if (!isReady2){
                mediaPlayer2?.prepareAsync()
            } else {
//                mediaPlayer2?.reset()
                mediaPlayer2?.start()
            }
        }
        Log.d("QueueService", "start Sound 2 terpangggil")
    }

    fun stopSoundNextQueue() {
        if (mediaPlayer2?.isPlaying == true){
            mediaPlayer2?.pause()
        }
//        mediaPlayer2?.stop()
//        mediaPlayer2?.reset()
//        isReady2 = false
        Log.d("QueueService", "stop Sound 2 terpangggil")
    }

    fun stopSound(){
        Log.d("QueueService", "stop Sound2 Terpanggil dari Barber")
        stop = true
        if (mediaPlayer2?.isPlaying == true){
            mediaPlayer2?.pause()
        }
//        mediaPlayer2?.reset()
//        isReady2 = false
    }

//    private fun buildNotification(message: String): Notification {
//        val notificationIntent = Intent(this, BarberLocationActivity::class.java)
//        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 23) {
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        } else {
//            PendingIntent.FLAG_UPDATE_CURRENT
//        }
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlags)
//
////        val customSoundUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.cut_corner_scissors)
//        val customSoundUri = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/${R.raw.cut_corner_scissors}")
//
//        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(getString(R.string.tittle_foreground_notification))
//            .setContentText(message)
////            .setOnlyAlertOnce(true)
//            .setSound(customSoundUri)
//            .setSmallIcon(R.drawable.logo_icon)
//            .setContentIntent(pendingIntent)
//
//        val audioAttributes = AudioAttributes.Builder()
//            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//            .build()
//
//        val mNotificationManager =
//            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            notificationBuilder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            channel.description = CHANNEL_NAME
//            channel.enableVibration(true)
//            channel.setSound(customSoundUri, audioAttributes)
//            notificationBuilder.setChannelId(CHANNEL_ID)
//            mNotificationManager.createNotificationChannel(channel)
//        }
//
//        return notificationBuilder.build()
//    }

    private fun buildNotification(message: String): Notification {
        val notificationIntent = Intent(this, BarberLocationActivity::class.java)
        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlags)

//        val customSoundUri = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/${R.raw.cut_corner_scissors}")

        val notificationTitle = resources.getString(R.string.tittle_foreground_notification)
        val notificationIcon = R.drawable.logo_icon_cir

        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notificationTitle)
            .setContentText(message)
//            .setSound(customSoundUri)
            .setSmallIcon(notificationIcon)
            .setContentIntent(pendingIntent)

//        val audioAttributes = AudioAttributes.Builder()
//            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationBuilder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_NAME
//            channel.enableVibration(true)
//            channel.setSound(customSoundUri, audioAttributes)
            notificationBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }

    override fun onDestroy() {
        stopSoundNextQueue()
        viewModel.combinedQueueData.removeObservers(this)
        mediaPlayer2?.stop()
        mediaPlayer2?.release()
        mediaPlayer2 = null
        Log.d("Service", "Service Stoped")
        super.onDestroy()
    }

}