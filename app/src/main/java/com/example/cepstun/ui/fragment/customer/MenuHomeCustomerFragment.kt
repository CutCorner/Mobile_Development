package com.example.cepstun.ui.fragment.customer

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.databinding.FragmentMenuHomeCustomerBinding
import com.example.cepstun.ui.activity.customer.BarbershopActivity
import com.example.cepstun.ui.activity.customer.BarbershopActivity.Companion.ID_BARBER
import com.example.cepstun.ui.activity.customer.FavoriteActivity
import com.example.cepstun.ui.activity.customer.NotificationActivity
import com.example.cepstun.ui.adapter.customer.BarberAdapter
import com.example.cepstun.ui.adapter.customer.BarberSearchAdapter
import com.example.cepstun.utils.showToast
import com.example.cepstun.viewModel.MenuHomeCustomerViewModel
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
    private lateinit var recyclerView2: RecyclerView

    private lateinit var adapter: BarberAdapter
    private lateinit var adapter2: BarberSearchAdapter

    private val viewModel: MenuHomeCustomerViewModel by viewModels {
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

        adapter = BarberAdapter()
        adapter2 = BarberSearchAdapter()

        viewModel.getMessage()

        viewModel.feedbackData.observe(viewLifecycleOwner) { data ->
            data?.let {
                val status = it.status
                if (status == "Pesanan Selesai") {
                    val inflater = layoutInflater
                    val dialogLayout = inflater.inflate(R.layout.dialog_review, null)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setView(dialogLayout)

                    val dialog = builder.create()

                    val nameBarber = dialogLayout.findViewById<TextView>(R.id.TVNameBarber)
                    val nameModel = dialogLayout.findViewById<TextView>(R.id.TVModel)
                    val nameAddOn = dialogLayout.findViewById<TextView>(R.id.TVAddons)
                    val closeButton = dialogLayout.findViewById<ImageButton>(R.id.IBClose)
                    val tvRate = dialogLayout.findViewById<TextView>(R.id.TVRate)

                    nameBarber.text = it.nameBarber
                    nameModel.text = it.modelBarber
                    nameAddOn.text = it.addOnBarber

                    val ratingBar = dialogLayout.findViewById<RatingBar>(R.id.RBRate)
                    ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                        tvRate.text = getString(R.string._5, rating.toString())
                    }

                    val okButton = dialogLayout.findViewById<Button>(R.id.MBOke)
                    okButton.setOnClickListener {
                        val reviewEditText = dialogLayout.findViewById<EditText>(R.id.ETReview)

                        val review = reviewEditText.text.toString()
                        val rating = ratingBar.rating

                        if (rating == 0f) {
                            Toast.makeText(requireContext(),
                                getString(R.string.please_provide_a_rating), Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.sendFeedback(data.idBarber, review, rating, data.modelBarber, data.addOnBarber)
                            dialog.dismiss()
                        }
                    }

                    closeButton.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                } else if (status == "Pesanan Di Batalkan") {
                    val inflater = layoutInflater
                    val dialogLayout = inflater.inflate(R.layout.dialog_canceled, null)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setView(dialogLayout)

                    val dialog = builder.create()

                    val nameBarber = dialogLayout.findViewById<TextView>(R.id.TVNameBarber)
                    val nameModel = dialogLayout.findViewById<TextView>(R.id.TVModel)
                    val nameAddOn = dialogLayout.findViewById<TextView>(R.id.TVAddons)
                    val reasonCanceled = dialogLayout.findViewById<TextView>(R.id.TVReason)
                    val closeButton = dialogLayout.findViewById<ImageButton>(R.id.IBClose)

                    nameBarber.text = it.nameBarber
                    nameModel.text = it.modelBarber
                    nameAddOn.text = it.addOnBarber
                    reasonCanceled.text = it.message

                    val okButton = dialogLayout.findViewById<Button>(R.id.MBOke)
                    okButton.setOnClickListener {
                            dialog.dismiss()
                    }

                    closeButton.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }
        }

        binding.apply {

            LLLocation.setOnClickListener {
                checkLocationSettingsAndRetrieveLocation()
            }

            btnFavorite.setOnClickListener {
                Intent(requireContext(), FavoriteActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }

            btnNotification.setOnClickListener {
                Intent(requireContext(), NotificationActivity::class.java).also {intent->
                    startActivity(intent)
                }
            }

            SVSearchBarber.setupWithSearchBar(SBSearchBarber)
            SVSearchBarber
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    recyclerView2 = RVBarber2
                    viewModel.findBarbershop(SVSearchBarber.text.toString())
                    viewModel.listBarber2.observe(viewLifecycleOwner) { barberData ->
                        adapter2.submitList(barberData)
                        recyclerView2.adapter = adapter2
                        setRecyclerList()
                    }
                    false
                }

            binding.SVSearchBarber.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    // Do nothing
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // Do nothing
                }

                override fun afterTextChanged(s: Editable) {
                    binding.RVBarber2.visibility = if (s.toString().isNullOrEmpty()) View.GONE else View.VISIBLE
                }
            })
        }

        recyclerView = binding.RVBarber

        viewModel.getListBarber()
        viewModel.listBarber.observe(viewLifecycleOwner) { barberData ->
            adapter.submitList(barberData)
            recyclerView.adapter = adapter
            setRecyclerList()
        }

        viewModel.isLoading.observe(viewLifecycleOwner){
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

    private fun setRecyclerList() {
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
                    Toast.makeText(requireContext(),
                        getString(R.string.you_are_currently_in_the_queue_check_the_order_menu_and_cancel_for_another_order), Toast.LENGTH_SHORT).show()
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

        adapter2.setOnItemClickCallback(object : BarberSearchAdapter.OnBarberClickListener {

            override fun onBarberClick(barberId: String) {
                lottie.playAnimation()
                load.visibility = View.VISIBLE
                val queue = viewModel.getQueue()
                if (queue.barberID != "" && queue.yourQueue != "") {
                    lottie.cancelAnimation()
                    load.visibility = View.GONE
                    Toast.makeText(requireContext(),
                        getString(R.string.you_are_currently_in_the_queue_check_the_order_menu_and_cancel_for_another_order), Toast.LENGTH_SHORT).show()
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
//                viewModel.getListBarber()
            }

            viewModel.permissionRequired.observe(viewLifecycleOwner) { isRequired ->
                if (isRequired) {
                    requestPermissionWithDexter()
                }
            }

            viewModel.message.observe(viewLifecycleOwner) { error ->
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