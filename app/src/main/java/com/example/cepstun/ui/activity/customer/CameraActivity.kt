package com.example.cepstun.ui.activity.customer

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
import com.example.cepstun.utils.createCustomTempFile
import java.io.File

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null

    private lateinit var selectedModel: String

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    private var fromProfile: Boolean = false

    private var tempFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fromProfile = intent.getBooleanExtra(FROM_PROFILE, false)

        binding.apply {

            if (fromProfile){
                CVStraight.visibility = View.GONE
                CVCurly.visibility = View.GONE
                CVWavy.visibility = View.GONE
                IBGallery.visibility = View.GONE
            }

            CVStraight.setOnClickListener {
                CVStraight.strokeWidth = 15
                CVCurly.strokeWidth = 0
                CVWavy.strokeWidth = 0
                selectedModel = "Straight"
            }

            CVCurly.setOnClickListener {
                CVStraight.strokeWidth = 0
                CVCurly.strokeWidth = 15
                CVWavy.strokeWidth = 0
                selectedModel = "Curly"
            }

            CVWavy.setOnClickListener {
                CVStraight.strokeWidth = 0
                CVCurly.strokeWidth = 0
                CVWavy.strokeWidth = 15
                selectedModel = "Wavy"
            }

            IBCaptureImage.setOnClickListener {
                binding.PBLoad.visibility = View.VISIBLE
                binding.LottieAV.playAnimation()
                if (fromProfile){
                    takePhoto()
                } else {
                    if (::selectedModel.isInitialized) {
                        takePhoto()
                    } else {
                        Toast.makeText(this@CameraActivity,
                            getString(R.string.please_select_hair_model_first), Toast.LENGTH_SHORT).show()
                        binding.LottieAV.cancelAnimation()
                        binding.PBLoad.visibility = View.GONE
                    }
                }
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

        tempFile?.let {
            if (it.exists()) {
                it.delete()
            }
        }

        tempFile = createCustomTempFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile!!).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this@CameraActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    if (fromProfile){
                        val intent = Intent()
                        intent.putExtra(EXTRA_CAMERAX_IMAGE, output.savedUri.toString())
                        setResult(CAMERAX_RESULT, intent)
                        binding.LottieAV.cancelAnimation()
                        binding.PBLoad.visibility = View.GONE
                        finish()
                    }else {
                        binding.LottieAV.cancelAnimation()
                        binding.PBLoad.visibility = View.GONE
                        moveToAIRecommendation(output.savedUri.toString())
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    binding.LottieAV.cancelAnimation()
                    binding.PBLoad.visibility = View.GONE
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
        val intent = Intent(this@CameraActivity, AIRecommendationActivity::class.java)
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
        // for ai
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val MODEL_TYPE = "model_type"

        // for take picture to profile
        const val CAMERAX_RESULT = 200
        const val FROM_PROFILE = "fromProfile"
    }
}