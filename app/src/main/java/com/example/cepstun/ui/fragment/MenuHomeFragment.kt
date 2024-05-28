package com.example.cepstun.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.databinding.FragmentMenuHomeBinding
import com.example.cepstun.ui.adapter.BarberAdapter
import com.example.cepstun.utils.showToast
import com.example.cepstun.viewModel.MenuHomeViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException
import java.util.Locale

@Suppress("DEPRECATION")
class MenuHomeFragment : Fragment() {
    private var _binding: FragmentMenuHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: BarberAdapter

    private val viewModel: MenuHomeViewModel by viewModels {
        ViewModelFactory.getInstance(this.requireContext())
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION1
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    REQUIRED_PERMISSION2
                ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionWithDexter()
        } else {
            checkLocationSettingsAndRetrieveLocation()
        }

        viewModel.database.observe(viewLifecycleOwner) { userDatabase ->
            Glide.with(requireContext())
                .load(userDatabase?.photo)
                .apply(
                    RequestOptions()
                        .circleCrop()
                        .override(250, 250)
                )
                .into(binding.civProfileImage)
        }

        binding.LLLocation.setOnClickListener {
            checkLocationSettingsAndRetrieveLocation()
        }

        recyclerView = binding.RVBarber

        // sementara ngambil data dari object dulu sambil nunggu model ML dan data CC
        adapter = BarberAdapter(BarberDataList.barberDataValue)

        showRecyclerList()
    }

    private fun showRecyclerList() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun requestPermissionWithDexter() {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        requireContext().showToast(getString(R.string.permission_agree), Toast.LENGTH_SHORT)
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        requireContext().showToast(getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun checkLocationSettingsAndRetrieveLocation() {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.gps_off),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (isAdded) { // Check if fragment is currently added to its activity
                            location?.let {
                                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                                try {
                                    val addressList =
                                        geocoder.getFromLocation(it.latitude, it.longitude, 1)
                                    if (addressList != null && addressList.size > 0) {
                                        val subLocality = addressList[0].subLocality
                                        val locality = addressList[0].locality
                                        if (subLocality != null && locality != null) {
                                            binding.TVLocation.text = (getString(
                                                R.string.district,
                                                subLocality,
                                                locality
                                            ))
                                        } else {
                                            binding.TVLocation.text = getString(R.string.remote_place)
                                        }
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.error_location_MenuHome, e.printStackTrace()),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } ?: run {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.error_location2_MenuHome),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION1 = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val REQUIRED_PERMISSION2 = Manifest.permission.ACCESS_FINE_LOCATION
    }
}