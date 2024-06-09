package com.example.cepstun.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberData
import com.example.cepstun.data.local.BarberDataList.barberDataValue
import com.example.cepstun.data.local.BarberDataList.image
import com.example.cepstun.data.local.BarberDataList.rating
import com.example.cepstun.data.local.Image
import com.example.cepstun.data.local.Rating

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.cepstun.databinding.ActivityBarbershopBinding
import com.example.cepstun.helper.convertColorToHue
import com.example.cepstun.helper.convertDpToPixel
import com.example.cepstun.viewModel.BarbershopViewModel
import com.example.cepstun.viewModel.ViewModelFactory
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
    private lateinit var barberRating: Rating
    private lateinit var barberImage: Image

    private lateinit var barberId: String

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationBarber: LatLng

    private lateinit var imageSlider: ImageSlider

    private val viewModel: BarbershopViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

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

                val marginBottom = 20f + ((275f - 20f) * slideOffset)
                val layoutParams = binding.MVmap.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = convertDpToPixel(marginBottom, this@BarbershopActivity)
                binding.MVmap.layoutParams = layoutParams
            }

            override fun onStateChanged(bottomSheet: View, newState: Int){}
        })

        barberId = intent.extras?.getString(ID_BARBER)!!

        viewModel.cekBarberQueue(barberId)

        binding.apply {

            barberData = barberDataValue.find { it.id == barberId }!!

            TVTittleBarber.text = barberData.name
            TVLocationBarber.text = barberData.location

            barberRating = rating.find { it.id == barberId }!!
            val averageRating = barberRating.ratingScore.average()
            val roundedRating = Math.round(averageRating * 10) / 10.0
            TVRate.text = getString(R.string.rate, roundedRating.toString())
            RBRate.rating = roundedRating.toFloat()


//            RBRate.rating = barberData.rate.toFloat()
//            TVRate.text = getString(R.string.rate, barberData.rate.toString())

            LLRate.setOnClickListener {
                Intent(this@BarbershopActivity, RatingActivity::class.java).also { intent ->
                    intent.putExtra(RatingActivity.ID_BARBER, barberId)
                    startActivity(intent)
                }
            }

            barberImage = image.find { it.id == barberId }!!

            val imageList = ArrayList<SlideModel>() // Create image list

            for (imageUrl in barberImage.picture) {
                imageList.add(SlideModel(imageUrl, ScaleTypes.CENTER_CROP))
            }

            imageSlider = ISImage
            imageSlider.setImageList(imageList)


            mMapView = MVmap
            mMapView.onCreate(savedInstanceState)
            mMapView.getMapAsync(this@BarbershopActivity)

            BOrder.setOnClickListener {
                Intent(this@BarbershopActivity, ChooseModelActivity::class.java).also {intent->
                    intent.putExtra(ChooseModelActivity.ID_BARBER, barberId)
                    startActivity(intent)
                }
            }
        }

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, null)

        lat = barberData.lat
        lon = barberData.lon

        viewModel.allQueue.observe(this) {
            if (it != null) {
                binding.TVQueue.text = it.toString()
            } else {
                binding.TVQueue.text = "0"
            }
        }

        viewModel.message.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

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
        const val ID_BARBER = "id_barber"
    }
}