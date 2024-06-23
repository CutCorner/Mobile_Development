package com.example.cepstun.ui.activity.barbershop

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LONGITUDE
import com.adevinta.leku.LocationPickerActivity
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityRegisterBarberBinding
import com.example.cepstun.ui.activity.customer.CameraActivity
import com.example.cepstun.ui.adapter.barbershop.AddImageAdapter
import com.example.cepstun.viewModel.RegisterBarberViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class RegisterBarberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBarberBinding

    private lateinit var dialogBottom: BottomSheetDialog

    private val imageUris = ArrayList<Uri>()
    private var logo: Uri? = null
    private lateinit var adapter: AddImageAdapter

    private var lat :Double? = null
    private var lon :Double? = null

    private val viewModel: RegisterBarberViewModel by viewModels {
        ViewModelFactory.getInstance(this.application)
    }

    private fun allPermissionsGranted() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, REQUIRED_PERMISSION1
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, REQUIRED_PERMISSION3
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this, REQUIRED_PERMISSION1
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, REQUIRED_PERMISSION2
            ) == PackageManager.PERMISSION_GRANTED
        }

    private val lekuActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            try {
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    lat = data?.getDoubleExtra(LATITUDE, 0.0)
                    lon = data?.getDoubleExtra(LONGITUDE, 0.0)
                    binding.ETCoordinate.setText(
                        getString(
                            R.string.map_coordinate,
                            lat.toString(),
                            lon.toString()
                        )
//                        "$lat,$lon"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this,
                    getString(R.string.error_map, e.printStackTrace()), Toast.LENGTH_SHORT).show()
            } finally {
                binding.PBLoad.visibility = View.GONE
                binding.LottieAV.cancelAnimation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBarberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingStatusBar()

        val load = binding.PBLoad
        val lottie = binding.LottieAV

        binding.apply {

            CVLogo.setOnClickListener {
                if (!allPermissionsGranted()) {
                    requestPermissionWithDexter()
                } else {
                    openGalleryForLogo()
                }
            }

            ETCoordinate.setOnClickListener {
                lottie.playAnimation()
                load.visibility = View.VISIBLE
                val locationPickerIntent = LocationPickerActivity.Builder(this@RegisterBarberActivity)
                    .withDefaultLocaleSearchZone()
                    .shouldReturnOkOnBackPressed()
                    .withGooglePlacesEnabled()
                    .withGoogleTimeZoneEnabled()
                    .withVoiceSearchHidden()
                    .withUnnamedRoadHidden()
                    .withSearchBarHidden()
                    .build()

                lekuActivityResultLauncher.launch(locationPickerIntent)
            }

            ETPhotoBarber.setOnClickListener {
                if (imageUris.size < 3) {
                    if (!allPermissionsGranted()) {
                        requestPermissionWithDexter()
                    } else {
                        showBottomSheetDialog()
                    }
                } else {
                    Toast.makeText(this@RegisterBarberActivity,
                        getString(R.string.you_can_only_select_up_to_3_images), Toast.LENGTH_SHORT).show()
                }
            }

            BContinue.setOnClickListener {
                val name = ETName.text.toString().trim()
                val address = ETAddress.text.toString().trim()
                val coordinate = ETCoordinate.text.toString().trim()
                val telephone = ETTelephone.text.toString().trim()

                if (imageUris.isEmpty() || logo == null || name == "" || address == "" || coordinate == "" || telephone == "" ) {
                    Toast.makeText(this@RegisterBarberActivity,
                        getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
                } else {
                    val alertDialogBuilder = AlertDialog.Builder(this@RegisterBarberActivity)
                    alertDialogBuilder.setTitle(getString(R.string.register_a_barbershop))
                    alertDialogBuilder
                        .setMessage(getString(R.string.your_barber_will_be_listed_here_soon_check_the_information_you_provide_and_make_sure_it_s_correct_you_cannot_return_to_this_page_after_continuing))
                        .setIcon(R.drawable.logo_icon_cir)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes)) { _, _->
                            viewModel.registerBarber(name, logo!!, lat!!, lon!!, address, imageUris, telephone)
                        }
                        .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                            dialog.cancel()
                        }
                    val alertDialog = alertDialogBuilder.create()

                    alertDialog.show()
                }
            }

            adapter = AddImageAdapter { uri ->
                imageUris.remove(uri)
                setAdapter()
            }
            RVImage.adapter = adapter
        }

        viewModel.message.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this){
            if (it){
                lottie.playAnimation()
                load.visibility = View.VISIBLE
            } else {
                lottie.cancelAnimation()
                load.visibility = View.GONE
            }
        }

    }

    private fun requestPermissionWithDexter() {
        Dexter.withContext(this).withPermissions(
            REQUIRED_PERMISSION1, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                REQUIRED_PERMISSION3
            } else {
                REQUIRED_PERMISSION2
            }
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    Toast.makeText(
                        this@RegisterBarberActivity,
                        getString(R.string.permission_agree),
                        Toast.LENGTH_LONG
                    ).show()
                }

                if (report.isAnyPermissionPermanentlyDenied) {
                    val snackBar = Snackbar.make(
                        binding.root, R.string.permission_denied, Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction(getString(R.string.settings)) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    snackBar.show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>, token: PermissionToken
            ) {
                token.continuePermissionRequest()
            }
        }).check()
    }



    private fun showBottomSheetDialog() {
        @SuppressLint("InflateParams") val bottomSheetView =
            layoutInflater.inflate(R.layout.bottom_sheet, null)
        dialogBottom = BottomSheetDialog(this@RegisterBarberActivity, R.style.BottomsheetDialogTheme)
        dialogBottom.setContentView(bottomSheetView)
        dialogBottom.show()
    }

    @Suppress("UNUSED_PARAMETER")
    fun openCamera(view: View) {
        Intent(this, CameraActivity::class.java).also {
            it.putExtra(CameraActivity.FROM_PROFILE, true)
            launcherIntentCameraX.launch(it)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERAX_RESULT) {
            it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri().let { uri ->
                imageUris.add(uri!!)
                dialogBottom.dismiss()
                setAdapter()
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun openGallery(view: View) {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUris.add(uri)
            dialogBottom.dismiss()
            setAdapter()
        } else {
            Toast.makeText(this, getString(R.string.no_media_selected), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGalleryForLogo(){
        launcherGalleryForLogo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGalleryForLogo = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            logo = uri
            binding.IVLogoBarber.setImageURI(logo)
        } else {
            Toast.makeText(this, getString(R.string.no_media_selected), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAdapter() {
        adapter.submitList(imageUris.toList())
        binding.TVHintPhotoBarber.text =
            getString(R.string.you_have_selected_images, imageUris.size.toString())
    }

    @Suppress("DEPRECATION")
    private fun settingStatusBar() {
        window.statusBarColor = getColor(R.color.brown)
        window.decorView.systemUiVisibility = 0
    }

    companion object {
        private const val REQUIRED_PERMISSION1 = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION2 = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val REQUIRED_PERMISSION3 = Manifest.permission.READ_MEDIA_IMAGES
    }
}