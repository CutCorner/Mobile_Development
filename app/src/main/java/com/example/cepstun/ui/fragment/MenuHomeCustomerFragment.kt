package com.example.cepstun.ui.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberDataList.barberDataValue
import com.example.cepstun.data.local.BarberDataList.rating
import com.example.cepstun.databinding.FragmentMenuHomeCustomerBinding
import com.example.cepstun.ui.activity.BarbershopActivity
import com.example.cepstun.ui.activity.BarbershopActivity.Companion.ID_BARBER
import com.example.cepstun.ui.adapter.BarberAdapter
import com.example.cepstun.utils.showToast
import com.example.cepstun.viewModel.MenuHomeViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MenuHomeCustomerFragment : Fragment() {
    private var _binding: FragmentMenuHomeCustomerBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: BarberAdapter

    private val viewModel: MenuHomeViewModel by viewModels {
        ViewModelFactory.getInstance(this.requireContext())
    }

    private fun allPermissionsGranted() = ActivityCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION1
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        requireContext(),
        REQUIRED_PERMISSION2
    ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Toast.makeText(requireContext(), getString(R.string.permission_agree), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuHomeCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionWithDexter()
        } else {
            checkLocationSettingsAndRetrieveLocation()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        }

        binding.LLLocation.setOnClickListener {
            checkLocationSettingsAndRetrieveLocation()
        }

        recyclerView = binding.RVBarber

        // sementara ngambil data dari object dulu sambil nunggu model ML dan data CC
        adapter = BarberAdapter(barberDataValue, rating)

        showRecyclerList()
    }

    private fun showRecyclerList() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
//        val itemDecoration =
//            DividerItemDecoration(requireContext(), (layoutManager as LinearLayoutManager).orientation)
//        recyclerView.addItemDecoration(itemDecoration)


        recyclerView.adapter = adapter

        val load = binding.PBLoad
        val lottie = binding.LottieAV

        adapter.setOnItemClickCallback(object : BarberAdapter.OnBarberClickListener {

            override fun onBarberClick(barberId: String) {
                lottie.playAnimation()
                load.visibility = View.VISIBLE
                val queue = viewModel.getQueue()
                if (queue.barberID != "" && queue.yourQueue != "") {
                    lottie.cancelAnimation()
                    load.visibility = View.GONE
                    Toast.makeText(requireContext(), "Anda sudah memiliki antrian, cek di menu order", Toast.LENGTH_SHORT).show()
                } else {
                    lottie.cancelAnimation()
                    load.visibility = View.GONE
                    Intent(requireContext(), BarbershopActivity::class.java).also { intent ->
                        intent.putExtra(ID_BARBER, barberId)
                        startActivity(intent)
                    }
                }
            }
        })
    }

    private fun requestPermissionWithDexter() {
        Dexter.withContext(requireContext())
            .withPermissions(
                REQUIRED_PERMISSION1,
                REQUIRED_PERMISSION2
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        requireContext().showToast(getString(R.string.permission_agree), Toast.LENGTH_SHORT)
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        val snackBar = Snackbar.make(
                            binding.root, // replace with your root view
                            R.string.permission_denied, // replace with your permission denied message
                            Snackbar.LENGTH_LONG
                        )
                        snackBar.setAction(getString(R.string.settings)) { // replace with your "Settings" string resource
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", requireContext().packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        snackBar.show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun checkLocationSettingsAndRetrieveLocation() {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(requireContext(),getString(R.string.gps_off),Toast.LENGTH_SHORT).show()
        } else {
            viewModel.getLastLocation()

            viewModel.address.observe(viewLifecycleOwner) { address ->
                binding.TVLocation.text = address
            }

            viewModel.permissionRequired.observe(viewLifecycleOwner) { isRequired ->
                if (isRequired) {
                    requestPermissionWithDexter()
                }
            }

            viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
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