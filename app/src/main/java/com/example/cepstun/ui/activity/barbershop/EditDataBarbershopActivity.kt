package com.example.cepstun.ui.activity.barbershop

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cepstun.databinding.ActivityEditDataBarbershopBinding
import com.example.cepstun.ui.activity.customer.PersonalDataActivity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDataBarbershopBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    companion object {
        private const val REQUIRED_PERMISSION1 = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION2 = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val REQUIRED_PERMISSION3 = Manifest.permission.READ_MEDIA_IMAGES
    }

}