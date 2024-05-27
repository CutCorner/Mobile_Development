package com.example.cepstun.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberData

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.cepstun.databinding.ActivityBarbershopBinding
import com.example.cepstun.helper.convertColorToHue
import com.example.cepstun.helper.convertDpToPixel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BarbershopActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView

    private lateinit var binding: ActivityBarbershopBinding

    private var lat: Double? = null
    private var lon: Double? = null

    private lateinit var barberData: BarberData

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationBarber: LatLng

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBarbershopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingStatusBar()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        BottomSheetBehavior.from(binding.CLQueue)
            .addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset:Float) {
                binding.MVmap.alpha = 1 - slideOffset * 0.6f

                val marginBottom = 20f + ((235f - 20f) * slideOffset)
                val layoutParams = binding.MVmap.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = convertDpToPixel(marginBottom, this@BarbershopActivity)
                binding.MVmap.layoutParams = layoutParams
            }

            override fun onStateChanged(bottomSheet: View, newState: Int){}
        })


        @Suppress("DEPRECATION")
        barberData = intent.extras?.getParcelable(DATA_BARBER)!!

        binding.apply {
            TVTittleBarber.text = barberData.name
            TVLocationBarber.text = barberData.location
            RBRate.rating = barberData.rate.toFloat()
            TVRate.text = getString(R.string.rate, barberData.rate.toString())
            Glide.with(this@BarbershopActivity.applicationContext)
                .load(barberData.image)
                .centerCrop()
                .into(IVImage)

            mMapView = MVmap
            mMapView.onCreate(savedInstanceState)
            mMapView.getMapAsync(this@BarbershopActivity)

            BOrder.setOnClickListener {
                Intent(this@BarbershopActivity, ChooseModelActivity::class.java).also {
                    intent.putExtra(ChooseModelActivity.ID_BARBER, barberData.id)
                    startActivity(it)
                }
            }
        }

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, null)

        lat = barberData.lat
        lon = barberData.lon

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isMapToolbarEnabled = true

        locationBarber = LatLng(lat!!, lon!!)
        mMap.addMarker(
            MarkerOptions()
                .position(locationBarber)
                .title(barberData.name)
                .snippet(barberData.location)
                .icon(BitmapDescriptorFactory.defaultMarker(ContextCompat.getColor(this, R.color.brown).convertColorToHue()))
        ).let {
            it?.showInfoWindow()
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationBarber, 15f))

        getMyLocation()

    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
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
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.setPadding(0,50,0,50)
        } else {
            requestPermissionLauncher.launch(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    @Suppress("DEPRECATION")
    private fun settingStatusBar() {
        window.statusBarColor = getColor(R.color.brown)
        window.decorView.systemUiVisibility = 0
    }

    companion object{
        const val DATA_BARBER = "data_barber"
    }
}