package com.example.cepstun.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityMainBinding
import com.example.cepstun.ui.fragment.MenuHistoryFragment
import com.example.cepstun.ui.fragment.MenuHomeBarberFragment
import com.example.cepstun.ui.fragment.MenuHomeCustomerFragment
import com.example.cepstun.ui.fragment.MenuOrdersFragment
import com.example.cepstun.ui.fragment.MenuProfileFragment
import com.example.cepstun.viewModel.MainViewModel
import com.example.cepstun.viewModel.ViewModelFactory

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
                                replaceFragment(MenuOrdersFragment())
                                true
                            }
                            R.id.nav_profile -> {
                                replaceFragment(MenuProfileFragment())
                                true
                            }
                            else -> false
                        }
                    }
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
                }
            }
            binding.PBLoad.visibility = View.GONE
            replaceFragment(MenuHomeCustomerFragment())
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) {
            val load = binding.PBLoad
            load.visibility = if (it) View.VISIBLE else View.GONE
            if (it) load.playAnimation() else load.cancelAnimation()
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
}