package com.example.cepstun.ui.activity.customer

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityPersonalDataBinding
import com.example.cepstun.utils.reduceFileImage
import com.example.cepstun.utils.uriToFile
import com.example.cepstun.ui.activity.customer.CameraActivity.Companion.CAMERAX_RESULT
import com.example.cepstun.viewModel.PersonalDataViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PersonalDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalDataBinding

    private lateinit var dialog: BottomSheetDialog
    private var currentImageUri: Uri? = null

    private val calendar = Calendar.getInstance()

    private val viewModel: PersonalDataViewModel by viewModels {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            IBEditPhoto.setOnClickListener {
                if (!allPermissionsGranted()) {
                    requestPermissionWithDexter()
                } else {
                    showBottomSheetDialog()
                }
            }

            viewModel.database.observe(this@PersonalDataActivity) {
                Glide.with(this@PersonalDataActivity).load(it?.photo).apply(
                    RequestOptions().circleCrop().override(250, 250)
                ).into(CIVProfileUser)

                ETFullName.setText(it?.fname)
                ETPhoneNumber.setText(it?.phone)
                ETEmailAddress.setText(it?.email)
                ETDateOfBirth.setText(it?.dateOfBirth)
                PSVGender.text = it?.gender
            }

            ETDateOfBirth.setOnClickListener {
                val datePickerDialog = DatePickerDialog(
                    this@PersonalDataActivity,
                    { _, year: Int, month: Int, dayOfMonth: Int ->
                        val selectedDate: Calendar = Calendar.getInstance()
                        selectedDate.set(year, month, dayOfMonth)
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val formattedDate = dateFormat.format(selectedDate.time)
                        ETDateOfBirth.setText(formattedDate)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }

            BSave.setOnClickListener {
                val photoFile = if (currentImageUri != null) {
                    uriToFile(currentImageUri!!, this@PersonalDataActivity).reduceFileImage()
                } else {
                    null
                }
                viewModel.updateDatabase(
                    if (ETFullName.text.toString() == "") null else ETFullName.text.toString(),
                    if (ETPhoneNumber.text.toString() == "") null else ETPhoneNumber.text.toString(),
                    if (ETDateOfBirth.text.toString() == "") null else ETDateOfBirth.text.toString(),
                    if (PSVGender.text.toString() == "") null else PSVGender.text.toString(),
                    photoFile
                )
            }

            IBBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            viewModel.isLoading.observe(this@PersonalDataActivity) {
                val load = PBLoad
                load.visibility = if (it) View.VISIBLE else View.GONE
                if (it) load.playAnimation() else load.cancelAnimation()
            }
        }

        viewModel.message.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun showBottomSheetDialog() {
        @SuppressLint("InflateParams") val bottomSheetView =
            layoutInflater.inflate(R.layout.bottom_sheet, null)
        dialog = BottomSheetDialog(this@PersonalDataActivity, R.style.BottomsheetDialogTheme)
        dialog.setContentView(bottomSheetView)
        dialog.show()
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
        if (it.resultCode == CAMERAX_RESULT) {
            it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri().let { uri ->
                startCropImage(uri!!)
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
            startCropImage(uri)
        } else {
            Toast.makeText(this, getString(R.string.no_media_selected), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.CIVProfileUser.setImageURI(it)
        }
        if (this::dialog.isInitialized) {
            dialog.dismiss()
        }
    }

    private fun startCropImage(uri: Uri) {
        val tempFile = File(externalCacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        val destinationUri = Uri.fromFile(tempFile)

        val options = UCrop.Options().apply {// compress size
            setCompressionQuality(100)
        }

        UCrop.of(uri, destinationUri).withAspectRatio(5f, 5f).withMaxResultSize(800, 800)
            .withOptions(options).start(this, uCropResultLauncher)

        tempFile.deleteOnExit()
    }

    private val uCropResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val resultUri = UCrop.getOutput(result.data!!)
                if (resultUri != null) {
                    currentImageUri = resultUri
                    showImage()
                } else {
                    Toast.makeText(this, getString(R.string.crop_error), Toast.LENGTH_SHORT).show()
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                Toast.makeText(
                    this, cropError?.message ?: getString(R.string.crop_error), Toast.LENGTH_SHORT
                ).show()
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
                        this@PersonalDataActivity,
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


    companion object {
        private const val REQUIRED_PERMISSION1 = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION2 = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val REQUIRED_PERMISSION3 = Manifest.permission.READ_MEDIA_IMAGES
    }
}