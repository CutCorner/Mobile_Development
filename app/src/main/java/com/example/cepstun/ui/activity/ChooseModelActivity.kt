package com.example.cepstun.ui.activity

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.data.local.BarberDataList.model
import com.example.cepstun.data.local.Model
import com.example.cepstun.ui.adapter.ModelBarberAdapter

class ChooseModelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseModelBinding

    private lateinit var idBarber: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var modelAdapter: ModelBarberAdapter

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingStatusBar()

        idBarber = intent.getStringExtra(ID_BARBER).toString()

        recyclerView = binding.RVModelBarber
        modelAdapter = ModelBarberAdapter(idBarber, model)
        setRecyclerView()

        binding.MBUseAI.setOnClickListener{
            if (!allPermissionsGranted()) {
                requestPermissionWithDexter()
            } else {
                moveToCamera()
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
                        binding.root, // replace with your root view
                        R.string.permission_denied, // replace with your permission denied message
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction(getString(R.string.settings)) { // replace with your "Settings" string resource
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