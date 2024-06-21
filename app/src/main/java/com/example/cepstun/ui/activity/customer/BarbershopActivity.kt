package com.example.cepstun.ui.activity.customer

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
import com.example.cepstun.data.local.entity.customer.FavoriteCustomer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.cepstun.databinding.ActivityBarbershopBinding
import com.example.cepstun.utils.convertColorToHue
import com.example.cepstun.utils.convertDpToPixel
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
    private var roundedRating: Double = 0.0

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

        binding.MVmap.post {
            val initialMarginBottom = 235f
            val initialLayoutParams = binding.MVmap.layoutParams as ViewGroup.MarginLayoutParams
            initialLayoutParams.bottomMargin = convertDpToPixel(initialMarginBottom, this@BarbershopActivity)
            binding.MVmap.layoutParams = initialLayoutParams
            binding.MVmap.alpha = 0.5f
        }

        BottomSheetBehavior.from(binding.CLQueue).apply {

            state = BottomSheetBehavior.STATE_EXPANDED

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.MVmap.alpha = 1 - slideOffset * 0.6f

                    val marginBottom = 80f + ((235f - 80f) * slideOffset)
                    val layoutParams = binding.MVmap.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.bottomMargin = convertDpToPixel(marginBottom, this@BarbershopActivity)
                    binding.MVmap.layoutParams = layoutParams
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {}
            })
        }

        barberId = intent.extras?.getString(ID_BARBER)!!

        viewModel.cekBarberQueueAndOpened(barberId)

        binding.apply {

            viewModel.getDetailBarbershop(barberId)
            viewModel.barberData.observe(this@BarbershopActivity){
                barberData = it
                TVTittleBarber.text = barberData.name
                TVLocationBarber.text = barberData.location

                if (it.rate != null) {
                    TVRate.text = getString(R.string.rate, it.rate.toString())
                    RBRate.rating = it.rate.toFloat()
                } else {
                    val averageRating = it.rating.map { it.ratingScore }.average()
                    roundedRating = Math.round(averageRating * 10.0) / 10.0
                    TVRate.text = getString(R.string.rate, roundedRating.toString())
                    RBRate.rating = roundedRating.toFloat()
                }

                val imageList = ArrayList<SlideModel>()

                for (image in it.image) {
                    imageList.add(SlideModel(image.picture, ScaleTypes.CENTER_CROP))
                }

                imageSlider = ISImage
                imageSlider.setImageList(imageList)

                lat = barberData.lat
                lon = barberData.lon
            }

            LLRate.setOnClickListener {
                Intent(this@BarbershopActivity, RatingActivity::class.java).also { intent ->
                    intent.putExtra(RatingActivity.ID_BARBER, barberId)
                    startActivity(intent)
                }
            }



            mMapView = MVmap
            mMapView.onCreate(savedInstanceState)
            mMapView.getMapAsync(this@BarbershopActivity)

            BOrder.setOnClickListener {
                viewModel.isOpened.observe(this@BarbershopActivity){
                    if (it){
                        Intent(this@BarbershopActivity, ChooseModelActivity::class.java).also { intent ->
                            intent.putExtra(ChooseModelActivity.ID_BARBER, barberId)
                            startActivity(intent)
                        }
                    } else{
                        Toast.makeText(this@BarbershopActivity,
                            getString(R.string.barbershop_is_closed), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            IBFavorite.setOnClickListener {
                val data = FavoriteCustomer(
                    idBarber = barberId,
                    nameBarber = barberData.name,
                    logoBarber = barberData.logo,
                    ratingBarber = roundedRating.toString()

                )
                viewModel.addFavorite(data)
            }

            viewModel.isOpened.observe(this@BarbershopActivity){
                if (it){
                    MCVOpened.apply {
//                        visibility = View.VISIBLE
                        visibility = View.GONE
                        setCardBackgroundColor(ContextCompat.getColor(this@BarbershopActivity, R.color.green))
                    }
                    TVOpened.apply {
                        text = getString(R.string.open)
                        setTextColor(ContextCompat.getColorStateList(this@BarbershopActivity, R.color.black))
                    }
                } else{
                    MCVOpened.apply {
//                        visibility = View.VISIBLE
                        visibility = View.GONE
                        setCardBackgroundColor(ContextCompat.getColor(this@BarbershopActivity, R.color.red))
                    }
                    TVOpened.apply {
                        text = getString(R.string.closed)
                        setTextColor(ContextCompat.getColorStateList(this@BarbershopActivity, R.color.white))
                    }
                }
            }
        }

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, null)

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

        viewModel.isAvailable.observe(this) { isAvailable ->
            val favorite = binding.IBFavorite
            if (isAvailable) {
                favorite.setImageResource(R.drawable.favorite_filled)
            } else {
                favorite.setImageResource(R.drawable.favorite_not_filled)
            }
        }

        viewModel.isLoading.observe(this) {
            val load = binding.PBLoad
            val lottie = binding.LottieAV
            if (it) {
                load.visibility = View.VISIBLE
                lottie.playAnimation()
            } else {
                load.visibility = View.GONE
                lottie.cancelAnimation()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        viewModel.themeSetting.observe(this){isDarkModeActive: Boolean? ->
            if (isDarkModeActive == null || !isDarkModeActive){
                mMap.setMapStyle(null)
            } else if (isDarkModeActive) {
                setMapStyle()
            }
        }

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

    private fun setMapStyle() {
        viewModel.setMapStyle(mMap)
    }

    companion object{
        const val ID_BARBER = "id_barber"
    }
}