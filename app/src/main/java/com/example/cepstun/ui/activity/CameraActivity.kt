package com.example.cepstun.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityCameraBinding
import com.example.cepstun.helper.createCustomTempFile

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null

    private lateinit var selectedModel: String

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            CVStraight.setOnClickListener {
                CVStraight.setCardBackgroundColor(getColor(R.color.brown))
                TVStraight.setTextColor(getColor(R.color.white))
                CVCurly.setCardBackgroundColor(getColor(R.color.gray))
                TVCurly.setTextColor(getColor(R.color.black))
                CVWavy.setCardBackgroundColor(getColor(R.color.gray))
                TVWavy.setTextColor(getColor(R.color.black))
                selectedModel = "Straight"
            }

            CVCurly.setOnClickListener {
                CVStraight.setCardBackgroundColor(getColor(R.color.gray))
                TVStraight.setTextColor(getColor(R.color.black))
                CVCurly.setCardBackgroundColor(getColor(R.color.brown))
                TVCurly.setTextColor(getColor(R.color.white))
                CVWavy.setCardBackgroundColor(getColor(R.color.gray))
                TVWavy.setTextColor(getColor(R.color.black))
                selectedModel = "Curly"
            }

            CVWavy.setOnClickListener {
                CVStraight.setCardBackgroundColor(getColor(R.color.gray))
                TVStraight.setTextColor(getColor(R.color.black))
                CVCurly.setCardBackgroundColor(getColor(R.color.gray))
                TVCurly.setTextColor(getColor(R.color.black))
                CVWavy.setCardBackgroundColor(getColor(R.color.brown))
                TVWavy.setTextColor(getColor(R.color.white))
                selectedModel = "Wavy"
            }

            IBCaptureImage.setOnClickListener {
                if (::selectedModel.isInitialized) {
                    val load = binding.PBLoad
                    load.visibility = View.VISIBLE
                    load.playAnimation()
                    takePhoto()
                } else Toast.makeText(this@CameraActivity,
                    getString(R.string.please_select_hair_model_first), Toast.LENGTH_SHORT).show()
            }

            IBClose.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            IBFlash.setOnClickListener {
                imageCapture?.let {
                    if (it.flashMode == ImageCapture.FLASH_MODE_ON) {
                        it.flashMode = ImageCapture.FLASH_MODE_OFF
                        IBFlash.setImageResource(R.drawable.ic_flash_off)
                    } else {
                        it.flashMode = ImageCapture.FLASH_MODE_ON
                        IBFlash.setImageResource(R.drawable.ic_flash_on)
                    }
                }
            }

            IBSwitchCamera.setOnClickListener {
                cameraSelector =
                    if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                        IBFlash.visibility = View.GONE
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    }
                    else {
                        IBFlash.visibility = View.VISIBLE
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }
                startCamera()
            }

            IBGallery.setOnClickListener {
                if (::selectedModel.isInitialized) openGallery() else Toast.makeText(this@CameraActivity,
                    getString(R.string.please_select_hair_model_first), Toast.LENGTH_SHORT).show()
            }
        }

    }

    @Suppress("DEPRECATION")
    private fun settingStatusBar() {
        window.statusBarColor = getColor(R.color.brown)
        window.decorView.systemUiVisibility = 0
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    getString(R.string.fail_to_show_camera),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    moveToAIRecommendation(output.savedUri.toString())
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        getString(R.string.fail_to_take_picture),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            moveToAIRecommendation(uri.toString())
        } else {
            Toast.makeText(this, getString(R.string.no_media_selected), Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveToAIRecommendation(uri: String) {
        val intent = Intent(this@CameraActivity, AIRecomendationActivity::class.java)
        intent.putExtra(EXTRA_CAMERAX_IMAGE, uri)
        intent.putExtra(MODEL_TYPE, selectedModel)
        startActivity(intent)
        finish()
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
        settingStatusBar()
    }

    companion object {
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val MODEL_TYPE = "model_type"
    }
}