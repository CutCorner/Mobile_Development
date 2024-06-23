package com.example.cepstun.ui.activity.customer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityChooseModelBinding
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.data.local.Model
import com.example.cepstun.ui.adapter.customer.ModelBarberAdapter
import com.example.cepstun.viewModel.ChooseModelViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class ChooseModelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseModelBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var modelAdapter: ModelBarberAdapter

    private val viewModel: ChooseModelViewModel by viewModels{
        ViewModelFactory.getInstance(this.application)
    }

    var idBarber: String? = null

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingStatusBar()

        idBarber = intent.getStringExtra(ID_BARBER)

        modelAdapter = ModelBarberAdapter()

        binding.apply {
            recyclerView = RVModelBarber

            if (idBarber != null) {
                viewModel.getModels(idBarber!!)

                viewModel.models.observe(this@ChooseModelActivity) { models ->
                    modelAdapter.submitList(models)
                    setRecyclerView()
                }
            }

            MBUseAI.setOnClickListener{
                if (!allPermissionsGranted()) {
                    requestPermissionWithDexter()
                } else {
                    moveToCamera()
                }
            }

            IBBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            viewModel.isLoading.observe(this@ChooseModelActivity) {
                val load = PBLoad
                val lottie = LottieAV
                if (it) {
                    load.visibility = View.VISIBLE
                    lottie.playAnimation()
                } else {
                    load.visibility = View.GONE
                    lottie.cancelAnimation()
                }
            }

        }

    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = modelAdapter

        modelAdapter.setOnItemClickCallback(object : ModelBarberAdapter.OnItemClickCallback {
            override fun onItemClicked(model: Model) {
                Intent(this@ChooseModelActivity, CheckoutActivity::class.java).also { intent ->
                    intent.putExtra(CheckoutActivity.SELECTED_MODEL, model)
                    intent.putExtra(CheckoutActivity.SELECTED_BARBER, idBarber)
                    startActivity(intent)
                }
            }
        })
    }

    private fun requestPermissionWithDexter() {
        Dexter.withContext(this)
            .withPermission(REQUIRED_PERMISSION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    Toast.makeText(this@ChooseModelActivity, getString(R.string.permission_agree), Toast.LENGTH_LONG).show()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    val snackBar = Snackbar.make(
                        binding.root,
                        R.string.permission_denied,
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction(getString(R.string.settings)) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    snackBar.show()
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    @Suppress("DEPRECATION")
    private fun settingStatusBar() {
        window.statusBarColor = getColor(R.color.brown)
        window.decorView.systemUiVisibility = 0
    }

    private fun moveToCamera() {
        Intent(this, CameraActivity::class.java).also { intent ->
            startActivity(intent)
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val ID_BARBER = "id_barber"
    }
}