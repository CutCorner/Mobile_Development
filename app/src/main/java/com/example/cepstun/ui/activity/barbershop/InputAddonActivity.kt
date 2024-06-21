package com.example.cepstun.ui.activity.barbershop

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cepstun.R
import com.example.cepstun.data.local.AddAddOn
import com.example.cepstun.databinding.ActivityInputAddonBinding
import com.example.cepstun.ui.adapter.barbershop.AddAddOnBarberAdapter
import com.example.cepstun.viewModel.InputAddOnViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class InputAddonActivity: AppCompatActivity() {

    private lateinit var binding: ActivityInputAddonBinding

    private lateinit var adapter: AddAddOnBarberAdapter

    private val viewModel: InputAddOnViewModel by viewModels{
        ViewModelFactory.getInstance(this.application)
    }

    private lateinit var idBarber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputAddonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingStatusBar()

        idBarber = intent.getStringExtra(EXTRA_BARBER_ID).toString()

        binding.apply {

            adapter = AddAddOnBarberAdapter { model ->
                val updatedList = adapter.currentList.filter { it != model }
                adapter.submitList(updatedList)
            }
            binding.RVAddOn.adapter = adapter

            FABAdd.setOnClickListener {
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.dialog_add_addon, null)

                val builder = AlertDialog.Builder(this@InputAddonActivity)
                builder.setView(dialogLayout)

                val nameEditText = dialogLayout.findViewById<EditText>(R.id.ETNameAddOn)
                val priceEditText = dialogLayout.findViewById<EditText>(R.id.ETPriceAddOn)

                val dialog = builder.create()

                val okButton = dialogLayout.findViewById<Button>(R.id.MBOke)
                okButton.setOnClickListener {
                    val name = nameEditText.text.toString()
                    val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0

                    val isDuplicate = adapter.currentList.any { it.name == name }

                    if (name == "" || price == 0.0) {
                        Toast.makeText(this@InputAddonActivity, getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
                    } else if (isDuplicate) {
                        Toast.makeText(this@InputAddonActivity,
                            getString(R.string.data_must_not_be_the_same_as_existing), Toast.LENGTH_SHORT).show()
                    } else {
                        val model = AddAddOn(name = name, price = price)
                        adapter.submitList(adapter.currentList + model)
                        dialog.dismiss()
                    }

                }

                val closeButton = dialogLayout.findViewById<ImageButton>(R.id.IBClose)
                closeButton.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }

            MBContinue.setOnClickListener {
                if (adapter.currentList.isEmpty()){
                    val alertDialogBuilder = AlertDialog.Builder(this@InputAddonActivity)
                    alertDialogBuilder.setTitle(getString(R.string.no_add_on_added))
                    alertDialogBuilder
                        .setMessage(getString(R.string.are_you_sure_you_don_t_want_to_add_an_addon_service))
                        .setIcon(R.drawable.logo_icon_cir)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes)) { _, _->
                            saveAddOn()
                        }
                        .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                            dialog.cancel()
                        }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                } else {
                    saveAddOn()
                }
            }

        }

        viewModel.message.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        val load = binding.PBLoad
        val lottie = binding.LottieAV

        viewModel.isLoading.observe(this){
            if (it){
                lottie.playAnimation()
                load.visibility = View.VISIBLE
            } else {
                lottie.cancelAnimation()
                load.visibility = View.GONE
            }
        }
    }

    private fun saveAddOn() {
        val alertDialogBuilder = AlertDialog.Builder(this@InputAddonActivity)
        alertDialogBuilder.setTitle(getString(R.string.add_addon_service_to_your_barbershop))
        alertDialogBuilder
            .setMessage(getString(R.string.types_of_addon_service_will_be_added_to_your_barbershop_make_sure_it_s_appropriate_you_cannot_return_to_this_page_after_continuing))
            .setIcon(R.drawable.logo_icon_cir)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _->
                viewModel.saveAddOn(adapter.currentList, idBarber)
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()
    }

    @Suppress("DEPRECATION")
    private fun settingStatusBar() {
        window.statusBarColor = getColor(R.color.brown)
        window.decorView.systemUiVisibility = 0
    }

    companion object {
        const val EXTRA_BARBER_ID = "extra_barbershop_id"
    }
}