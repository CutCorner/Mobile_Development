package com.example.cepstun.ui.fragment.barbershop

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.remote.response.barber.StoreData
import com.example.cepstun.databinding.FragmentMenuProfileBarbershopBinding
import com.example.cepstun.ui.activity.ChangeLanguageActivity
import com.example.cepstun.ui.activity.ChangeThemesActivity
import com.example.cepstun.ui.activity.PrivacySecurityActivity
import com.example.cepstun.ui.activity.barbershop.ChangeModelActivity
import com.example.cepstun.ui.activity.barbershop.ChangeAddOnActivity
import com.example.cepstun.utils.getFullImageUrl
import com.example.cepstun.viewModel.MenuProfileBarberViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class MenuProfileBarbershopFragment : Fragment() {

    private var _binding: FragmentMenuProfileBarbershopBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MenuProfileBarberViewModel by viewModels{
        ViewModelFactory.getInstance(this.requireContext())
    }

    private lateinit var barberData: List<StoreData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuProfileBarbershopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getBarberData()

        binding.apply {

            viewModel.barberData.observe(viewLifecycleOwner){
                barberData = it.store
                TVBarberName.text = barberData.first().name
                TVLocation.text = barberData.first().location
                Glide.with(requireContext())
                    .load(barberData.first().imgSrc.getFullImageUrl())
                    .placeholder(R.drawable.logo_placeholder)
                    .circleCrop()
                    .into(CIVProfile)
            }

            BTChangeModel.setOnClickListener {
                Intent(requireContext(), ChangeModelActivity::class.java).also {
                    startActivity(it)
                }
            }

            BTChangeAddOnAndPrice.setOnClickListener {
                Intent(requireContext(), ChangeAddOnActivity::class.java).also {
                    startActivity(it)
                }
            }

            BPrivacyPolicy.setOnClickListener {
                Intent(requireContext(), PrivacySecurityActivity::class.java).also {
                    startActivity(it)
                }
            }

            BTChangeLanguage.setOnClickListener {
                Intent(requireContext(), ChangeLanguageActivity::class.java).also {
                    startActivity(it)
                }
            }

            BTThemes.setOnClickListener {
                Intent(requireContext(), ChangeThemesActivity::class.java).also {
                    startActivity(it)
                }
            }

            MBLogout.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle(getString(R.string.logging_alert_title))
                alertDialogBuilder
                    .setMessage("Your history will be deleted if you log out")
                    .setIcon(R.drawable.logo_icon_cir)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes)) { _, _->
                        viewModel.deleteAllBarHistory()
                        viewModel.deleteUserLevel()
                        viewModel.signOut()
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.cancel()
                    }
                val alertDialog = alertDialogBuilder.create()

                alertDialog.show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner){
            val load = binding.PBLoad
            val lottie = binding.LottieAV
            if (it) {
                load.visibility = View.VISIBLE
                lottie.playAnimation()
            } else {
                load.visibility = View.GONE
                lottie.cancelAnimation()
            }
        }

        viewModel.message.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}