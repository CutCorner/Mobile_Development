package com.example.cepstun.ui.fragment.customer

import android.app.AlertDialog
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
import com.example.cepstun.R
import com.example.cepstun.databinding.FragmentMenuProfileCustomerBinding
import com.example.cepstun.ui.activity.ChangeLanguageActivity
import com.example.cepstun.ui.activity.ChangeThemesActivity
import com.example.cepstun.ui.activity.customer.FavoriteActivity
import com.example.cepstun.ui.activity.customer.PersonalDataActivity
import com.example.cepstun.ui.activity.PrivacySecurityActivity
import com.example.cepstun.ui.activity.customer.NotificationActivity
import com.example.cepstun.viewModel.MenuProfileCustomerViewModel
import com.example.cepstun.viewModel.ViewModelFactory


class MenuProfileCustomerFragment : Fragment() {
    private var _binding: FragmentMenuProfileCustomerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MenuProfileCustomerViewModel by viewModels {
        ViewModelFactory.getInstance(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentMenuProfileCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            BTMyBarbershopFavorite.setOnClickListener {
                Intent(requireContext(), FavoriteActivity::class.java).also {
                    startActivity(it)
                }
            }

            BTThemes.setOnClickListener {
                Intent(requireContext(), ChangeThemesActivity::class.java).also {
                    startActivity(it)
                }
            }

            BTChangeLanguage.setOnClickListener {
                Intent(requireContext(), ChangeLanguageActivity::class.java).also {
                    startActivity(it)
                }
            }

            BTNotification.setOnClickListener {
                Intent(requireContext(), NotificationActivity::class.java).also {
                    startActivity(it)
                }
            }

            MBLogout.setOnClickListener {
                val queue = viewModel.getQueue()
                if (queue.barberID != "" && queue.yourQueue != "") {
                    Toast.makeText(requireContext(),
                        getString(R.string.you_cannot_leave_before_completing_the_order), Toast.LENGTH_SHORT).show()
                } else {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setTitle(getString(R.string.logging_alert_title))
                    alertDialogBuilder
                        .setMessage(getString(R.string.logout_alert_message))
                        .setIcon(R.drawable.logo_icon_cir)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes)) { _, _->
                            viewModel.deleteAllCusHistory()
                            viewModel.deleteAllFavorite()
                            viewModel.deleteAllNotification()
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

        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}