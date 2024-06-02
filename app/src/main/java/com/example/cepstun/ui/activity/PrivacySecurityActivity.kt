package com.example.cepstun.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityPrivacySecurityBinding

class PrivacySecurityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacySecurityBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacySecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView = binding.WVPrivacySecurity
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:alert('Privacy Policy and Security Example')")
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView, url: String, message: String, result: android.webkit.JsResult): Boolean {
                Toast.makeText(this@PrivacySecurityActivity, message, Toast.LENGTH_LONG).show()
                result.confirm()
                return true
            }
        }

        webView.loadUrl("https://www.privacypolicyonline.com/live.php?token=ZsQ2o22PRApilVHItTmRzVQOIj4TBkxM")

    }
}