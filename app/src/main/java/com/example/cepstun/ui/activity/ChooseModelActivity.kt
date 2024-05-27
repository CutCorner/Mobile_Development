package com.example.cepstun.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityChooseModelBinding
import com.example.cepstun.ui.fragment.MenuNotificationFragment

class ChooseModelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseModelBinding

    private lateinit var idBarber: String

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingStatusBar()

        idBarber = intent.getStringExtra(ID_BARBER).toString()
        Log.d("ChooseModel", idBarber)

        binding.MBUseAI.setOnClickListener{
            if (!allPermissionsGranted()) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
                moveToCamera()
            } else {
                moveToCamera()
            }
        }
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.permission_request_granted), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.permission_request_denied), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val ID_BARBER = "id_barber"
    }
}