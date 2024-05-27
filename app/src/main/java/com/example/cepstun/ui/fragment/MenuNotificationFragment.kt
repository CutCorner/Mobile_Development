package com.example.cepstun.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.cepstun.R
import com.example.cepstun.databinding.FragmentMenuNotificationBinding
import com.example.cepstun.ui.activity.CameraActivity
import com.example.cepstun.ui.activity.OnBoardingActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class MenuNotificationFragment : Fragment() {
    private var _binding: FragmentMenuNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentMenuNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.MBLogout.setOnClickListener {
            auth.signOut()

            Intent(requireContext(), OnBoardingActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                requireContext().startActivity(intent)
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}