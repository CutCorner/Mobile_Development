package com.example.cepstun.ui.activity.barbershop

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.recyclerview.widget.RecyclerView
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LONGITUDE
import com.adevinta.leku.LocationPickerActivity
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.remote.dataClass.ImageItem
import com.example.cepstun.databinding.ActivityEditDataBarbershopBinding
import com.example.cepstun.ui.activity.customer.CameraActivity
import com.example.cepstun.ui.adapter.barbershop.ChangeImageAdapter
import com.example.cepstun.utils.getFullImageUrl
import com.example.cepstun.viewModel.EditDataBarbershopViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class EditDataBarbershopActivity: AppCompatActivity() {
    private lateinit var binding: ActivityEditDataBarbershopBinding

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

    private var lat :Double? = null
    private var lon :Double? = null

    private val imageItems = ArrayList<ImageItem>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var combinedImageAdapter: ChangeImageAdapter


    private lateinit var dialogBottom: BottomSheetDialog
    private var logo: Uri? = null

    private val viewModel: EditDataBarbershopViewModel by viewModels{
        ViewModelFactory.getInstance(this.application)
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
        binding = ActivityEditDataBarbershopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val load = binding.PBLoad
        val lottie = binding.LottieAV

        combinedImageAdapter = ChangeImageAdapter { item ->
            when (item) {
                is ImageItem.UriImage -> {
                    imageItems.remove(item)
                    setAdapter()
                }
                is ImageItem.UrlImage -> {
                    imageItems.remove(item)
                    setAdapter()
                }
            }
        }

        recyclerView = binding.RVImage
        recyclerView.adapter = combinedImageAdapter


        viewModel.getBarberData()
        binding.apply {

            viewModel.barberData.observe(this@EditDataBarbershopActivity){barberData ->
                lat = barberData.store[0].lat
                lon = barberData.store[0].long

                ETName.setText(barberData.store[0].name)
                ETAddress.setText(barberData.store[0].location)
                ETTelephone.setText(barberData.store[0].phone)
                ETCoordinate.setText(
                    getString(
                        R.string.map_coordinate,
                        lat.toString(),
                        lon.toString()
                    ))

                Glide.with(this@EditDataBarbershopActivity)
                    .load(barberData.store[0].imgSrc.getFullImageUrl())
                    .placeholder(R.drawable.logo_placeholder)
                    .circleCrop()
                    .into(CIVLogoBarber)
                logo = Uri.parse(barberData.store[0].imgSrc)

                val thumbnails = barberData.store[0].thumbnail
                if (thumbnails != null) {
                    for (thumbnail in thumbnails) {
                        imageItems.add(ImageItem.UrlImage(thumbnail.imgSrc!!))
                    }
                    combinedImageAdapter.submitList(imageItems.toList())
                    TVHintPhotoBarber.text =
                        getString(R.string.you_have_selected_images, combinedImageAdapter.itemCount.toString())
                }

            }

            IBEditPhoto.setOnClickListener {
                if (!allPermissionsGranted()) {
                    requestPermissionWithDexter()
                } else {
                    openGalleryForLogo()
                }
            }

            ETCoordinate.setOnClickListener {
                lottie.playAnimation()
                load.visibility = View.VISIBLE
                val locationPickerIntent = LocationPickerActivity.Builder(this@EditDataBarbershopActivity)
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
                if (combinedImageAdapter.itemCount < 3) {
                    if (!allPermissionsGranted()) {
                        requestPermissionWithDexter()
                    } else {
                        showBottomSheetDialog()
                    }
                } else {
                    Toast.makeText(this@EditDataBarbershopActivity,
                        getString(R.string.you_can_only_select_up_to_3_images), Toast.LENGTH_SHORT).show()
                }
            }

            BSave.setOnClickListener {

                val name = ETName.text.toString()
                val address = ETAddress.text.toString()
                val telephone = ETTelephone.text.toString()

                val imageItems = combinedImageAdapter.currentList

                if (logo != null && lat != null && lon != null && name != "" && address != "" && telephone != "" && imageItems.isNotEmpty()){
                    viewModel.editBarber(name, logo!!, lat!!, lon!!, address, imageItems, telephone)
                } else {
                    Toast.makeText(this@EditDataBarbershopActivity,
                        getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
                }

            }

        }


        viewModel.isLoading.observe(this){
            if (it) {
                load.visibility = View.VISIBLE
                lottie.playAnimation()
            } else {
                load.visibility = View.GONE
                lottie.cancelAnimation()
            }
        }

        viewModel.message.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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
                        this@EditDataBarbershopActivity,
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
        dialogBottom = BottomSheetDialog(this@EditDataBarbershopActivity, R.style.BottomsheetDialogTheme)
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
                imageItems.add(ImageItem.UriImage(uri!!))
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
            imageItems.add(ImageItem.UriImage(uri))
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
            binding.CIVLogoBarber.setImageURI(logo)
        } else {
            Toast.makeText(this, getString(R.string.no_media_selected), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAdapter() {
        combinedImageAdapter.submitList(imageItems.toList())
        combinedImageAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                updateHintText()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                updateHintText()
            }
        })
    }

    private fun updateHintText() {
        binding.TVHintPhotoBarber.text =
            getString(R.string.you_have_selected_images, combinedImageAdapter.itemCount.toString())
    }

    companion object {
        private const val REQUIRED_PERMISSION1 = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION2 = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val REQUIRED_PERMISSION3 = Manifest.permission.READ_MEDIA_IMAGES
    }

}