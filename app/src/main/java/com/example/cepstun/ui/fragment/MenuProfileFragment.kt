package com.example.cepstun.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cepstun.databinding.FragmentMenuProfileBinding
import com.example.cepstun.ui.activity.OnBoardingActivity
import com.example.cepstun.ui.activity.PersonalDataActivity
import com.example.cepstun.ui.activity.PrivacySecurityActivity
import com.example.cepstun.viewModel.MenuHomeViewModel
import com.example.cepstun.viewModel.MenuProfileViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class MenuProfileFragment : Fragment() {
    private var _binding: FragmentMenuProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private val viewModel: MenuProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentMenuProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.apply {

            MBLogout.setOnClickListener {
                viewModel.signOut()
            }

            viewModel.database.observe(viewLifecycleOwner) { userDatabase ->
                Glide.with(requireContext())
                    .load(userDatabase?.photo)
                    .apply(
                        RequestOptions()
                            .circleCrop()
                            .override(250, 250)
                    )
                    .into(CIVProfile)

                TVCustomerName.text = userDatabase?.fname
                TVEmail.text = userDatabase?.email
                TVNumber.text = userDatabase?.phone
            }

            IBEditProfile.setOnClickListener {
                val intent = Intent(requireContext(), PersonalDataActivity::class.java)
                startActivity(intent)
            }


            BPrivacyPolicy.setOnClickListener {
                val intent = Intent(requireContext(), PrivacySecurityActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}