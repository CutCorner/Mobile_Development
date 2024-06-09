package com.example.cepstun.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityMainBinding
import com.example.cepstun.ui.fragment.MenuHistoryFragment
import com.example.cepstun.ui.fragment.MenuHomeBarberFragment
import com.example.cepstun.ui.fragment.MenuHomeCustomerFragment
import com.example.cepstun.ui.fragment.MenuCustomerOrdersFragment
import com.example.cepstun.ui.fragment.MenuProfileFragment
import com.example.cepstun.viewModel.MainViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var backPressed: Long = 0

    private val viewModel: MainViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cekUserLoginAndLevel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED)
                requestPermissionWithDexter()
        }

        viewModel.levelUser.observe(this) { level ->
            when (level) {
                "Customer" -> {
                    binding.BNMenu.menu.clear()
                    binding.BNMenu.inflateMenu(R.menu.bottom_menu_customer)
                    binding.BNMenu.setOnItemSelectedListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.nav_home -> {
                                replaceFragment(MenuHomeCustomerFragment())
                                true
                            }
                            R.id.nav_orders -> {
                                replaceFragment(MenuCustomerOrdersFragment())
                                true
                            }
                            R.id.nav_profile -> {
                                replaceFragment(MenuProfileFragment())
                                true
                            }
                            else -> false
                        }
                    }
                    replaceFragment(MenuHomeCustomerFragment())
                }
                else -> {
                    binding.BNMenu.menu.clear()
                    binding.BNMenu.inflateMenu(R.menu.bottom_menu_barber)
                    binding.BNMenu.setOnItemSelectedListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.nav_home -> {
                                replaceFragment(MenuHomeBarberFragment())
                                true
                            }
                            R.id.nav_history -> {
                                replaceFragment(MenuHistoryFragment())
                                true
                            }
                            R.id.nav_profile -> {
                                replaceFragment(MenuProfileFragment())
                                true
                            }
                            else -> false
                        }
                    }
                    replaceFragment(MenuHomeBarberFragment())
                }
            }
            binding.PBLoad.visibility = View.GONE
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) {
            val load = binding.PBLoad
            load.visibility = if (it) View.VISIBLE else View.GONE
            val lottie = binding.LottieAV
            if (it) lottie.playAnimation() else lottie.cancelAnimation()
        }

        settingStatusBar()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressed + 2000 > System.currentTimeMillis()) {
                    finish()
                    finishAffinity()
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.onBack), Toast.LENGTH_SHORT).show()
                    backPressed = System.currentTimeMillis()
                }
            }
        })
    }

    private fun requestPermissionWithDexter() {
        Dexter.withContext(this)
            .withPermission(REQUIRED_PERMISSION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    Toast.makeText(this@MainActivity, getString(R.string.permission_agree), Toast.LENGTH_LONG).show()
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

    private fun cekUserLoginAndLevel() {
        viewModel.cekUserLoginAndLevel()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.FLContainer, fragment).commit()
    }

    @Suppress("DEPRECATION")
    private fun settingStatusBar() {
        window.statusBarColor = getColor(R.color.brown)
        window.decorView.systemUiVisibility = 0
    }

    companion object{
        private const val REQUIRED_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
    }
}