package com.example.cepstun.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberData
import com.example.cepstun.data.local.BarberDataList.barberDataValue
import com.example.cepstun.data.local.entity.HistoryCustomer
import com.example.cepstun.databinding.ActivityBarberLocationBinding
import com.example.cepstun.helper.convertColorToHue
import com.example.cepstun.helper.convertDpToPixel
import com.example.cepstun.service.QueueService
import com.example.cepstun.viewModel.BarberLocationViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BarberLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView

    private val viewModel: BarberLocationViewModel by viewModels {
        ViewModelFactory.getInstance(this.application)
    }

    private lateinit var binding: ActivityBarberLocationBinding

    private lateinit var barberId: String
    private lateinit var barberData: BarberData
    private lateinit var yourQueue: String

    private lateinit var intentService: Intent

    private var queueService: QueueService? = null

    private var serviceBound = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationBarber: LatLng

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            getMyLocation()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as QueueService.LocalBinder
            queueService = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarberLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0, 0, 0, insets.bottom)
            view.layoutParams = layoutParams
            WindowInsetsCompat.CONSUMED
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        BottomSheetBehavior.from(binding.CLQueue)
            .addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.MVmap.alpha = 1 - slideOffset * 0.6f

                    val marginBottom = 20f + ((365f - 20f) * slideOffset)
                    val layoutParams = binding.MVmap.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.bottomMargin =
                        convertDpToPixel(marginBottom, this@BarberLocationActivity)
                    binding.MVmap.layoutParams = layoutParams
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {}
            })

        if (intent.extras == null) {
            viewModel.getQueue().also {
                barberId = it.barberID
                yourQueue = it.yourQueue
            }
            queueService?.stopSound()
        } else {
            barberId = intent.extras?.getString(ID_BARBER)!!
            yourQueue = intent.extras?.getString(YOUR_QUEUE)!!
            viewModel.saveQueue(barberId, yourQueue)
        }

        intentService = Intent(this@BarberLocationActivity, QueueService::class.java)
        stopService(intentService)

        viewModel.observeQueue(barberId, yourQueue.toInt())

        intentService.putExtra(QueueService.YOUR_QUEUE, yourQueue)
        intentService.putExtra(QueueService.BARBER_ID, barberId)
        ContextCompat.startForegroundService(this@BarberLocationActivity, intentService)

        barberData = barberDataValue.find { it.id == barberId }!!
        locationBarber = LatLng(barberData.lat, barberData.lon)


        binding.apply {

            TVBarberName.text = barberData.name
            TVLocationBarber.text = barberData.location
            TVYourQueue.text = yourQueue

            Glide.with(this@BarberLocationActivity).load(barberData.logo).into(IBBarberFocus)

            IBBarberFocus.setOnClickListener {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationBarber, 15f))
            }

            viewModel.combinedQueueData.observe(this@BarberLocationActivity) { (currentQueue, remainingQueue) ->
                if (currentQueue == -1) {
                    stopService(intentService)
                    viewModel.deleteQueue()
                    Intent(this@BarberLocationActivity, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    TVCurrentQueue.text = currentQueue.toString()
                    TVRemainingQueue.text = remainingQueue.toString()
                }
            }

            viewModel.dialog.observe(this@BarberLocationActivity) {
                val builder = AlertDialog.Builder(this@BarberLocationActivity)
                builder.setTitle("Warning!!")
                builder.setMessage(it)
                builder.setCancelable(false)
                builder.setPositiveButton("OK") { _, _ ->
                    queueService?.stopSound()
                }
                val dialog = builder.create()
                dialog.show()
            }

            BCancel.setOnClickListener {

                viewModel.cancelQueue(barberId, yourQueue.toInt())
                viewModel.deleteQueue()
                queueService?.stopSound()

                viewModel.order.observe(this@BarberLocationActivity) { order ->
                    val history = HistoryCustomer(
                        idBarber = barberData.id,
                        nameBarber = barberData.name,
                        logoBarber = barberData.logo,
                        modelBarber = order!!.model,
                        addOnBarber = order.addon,
                        priceBarber = order.price.toInt(),
                        status = "Batal"
                    )

                    viewModel.insertCusHistory(history)
                }

                stopService(intentService)

                Intent(this@BarberLocationActivity, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }

            }

            mMapView = MVmap
            mMapView.onCreate(savedInstanceState)
            mMapView.getMapAsync(this@BarberLocationActivity)

        }

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, null)

//        lat = barberData.lat
//        lon = barberData.lon
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.addMarker(
            MarkerOptions().position(locationBarber).title(barberData.name)
                .snippet(barberData.location).icon(
                    BitmapDescriptorFactory.defaultMarker(
                        ContextCompat.getColor(
                            this, R.color.brown
                        ).convertColorToHue()
                    )
                )
        ).let {
            it?.showInfoWindow()
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationBarber, 15f))

        getMyLocation()

    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
        if (!serviceBound) {
            Intent(this, QueueService::class.java).also { intent ->
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }
        queueService?.stopSound()
        Log.d("Barber", "Memanggil stop Sound")
    }

    override fun onPause() {
        mMapView.onPause()
//        queueService?.stop = false
        queueService?.startSoundNextQueue(viewModel.remainingQueue.value ?: 0)
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        Intent(this, QueueService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        Intent(this, QueueService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            Log.d("Activity", "Service Connected")
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.setPadding(0, 50, 0, 100)

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val myLatLng = LatLng(location.latitude, location.longitude)

                    val builder = LatLngBounds.Builder()
                    builder.include(myLatLng)
                    builder.include(locationBarber)
                    val bounds = builder.build()

                    val width = resources.displayMetrics.widthPixels
                    val height = resources.displayMetrics.heightPixels
                    val padding = (width * 0.20).toInt()

                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    companion object {
        const val ID_BARBER = "id_barber"
        const val YOUR_QUEUE = "your_queue"
    }
}