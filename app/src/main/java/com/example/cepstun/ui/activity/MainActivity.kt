package com.example.cepstun.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityMainBinding
import com.example.cepstun.ui.fragment.MenuHistoryFragment
import com.example.cepstun.ui.fragment.MenuHomeFragment
import com.example.cepstun.ui.fragment.MenuNotificationFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var backPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(MenuHomeFragment())

        settingStatusBar()

        binding.BNMenu.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.nav_home -> {
                    replaceFragment(MenuHomeFragment())
                    true
                }
                R.id.nav_history -> {
                    replaceFragment(MenuHistoryFragment())
                    true
                }
                R.id.nav_notification -> {
                    replaceFragment(MenuNotificationFragment())
                    true
                }
                else -> false
            }
        }

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

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.FLContainer, fragment).commit()
    }

    @Suppress("DEPRECATION")
    private fun settingStatusBar() {
        window.statusBarColor = getColor(R.color.brown)
        window.decorView.systemUiVisibility = 0
    }
}