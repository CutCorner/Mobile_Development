package com.example.cepstun.ui.activity.barbershop

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.data.local.AddAddOn
import com.example.cepstun.databinding.ActivityChangeAddonBinding
import com.example.cepstun.ui.adapter.barbershop.ChangeAddOnAdapter
import com.example.cepstun.viewModel.ChangeAddOnViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class ChangeAddOnActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChangeAddonBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChangeAddOnAdapter

    private val viewModel: ChangeAddOnViewModel by viewModels{
        ViewModelFactory.getInstance(this.application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeAddonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChangeAddOnAdapter()
        recyclerView = binding.RVAddOn

        viewModel.getBarberData()

        viewModel.barberData.observe(this) {resultData ->
            resultData.store.forEach { storeData ->
                if (storeData.addons.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    binding.TVEmpty.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    binding.TVEmpty.visibility = View.GONE
                    adapter.submitList(storeData.addons)
                    recyclerView.adapter = adapter
                }

                adapter.setOnItemClickCallback(object : ChangeAddOnAdapter.OnChangeClickListener {
                    override fun onDeleteClick(name: String) {
                        val inflater = layoutInflater
                        val dialogLayout = inflater.inflate(R.layout.dialog_delete_addon, null)

                        val builder = AlertDialog.Builder(this@ChangeAddOnActivity)
                        builder.setView(dialogLayout)

                        val dialog = builder.create()

                        val closeDialog = dialogLayout.findViewById<ImageButton>(R.id.IBClose)
                        closeDialog.setOnClickListener {
                            dialog.dismiss()
                        }

                        val addonName = dialogLayout.findViewById<TextView>(R.id.TVAddonName)
                        addonName.text = name

                        val okButton = dialogLayout.findViewById<Button>(R.id.deleteButton)
                        okButton.setOnClickListener {
                            viewModel.deleteAddOn(name)
                            dialog.dismiss()
                        }

                        dialog.show()
                    }
                })
            }
        }

        viewModel.message.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this){
            if (it){
                binding.LottieAV.playAnimation()
                binding.PBLoad.visibility = View.VISIBLE
            } else {
                binding.LottieAV.cancelAnimation()
                binding.PBLoad.visibility = View.GONE
            }
        }

        binding.MBAddAddOn.setOnClickListener {
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.dialog_add_addon, null)

            val builder = AlertDialog.Builder(this)
            builder.setView(dialogLayout)

            val nameEditText = dialogLayout.findViewById<EditText>(R.id.ETNameAddOn)
            val priceEditText = dialogLayout.findViewById<EditText>(R.id.ETPriceAddOn)

            val dialog = builder.create()

            val okButton = dialogLayout.findViewById<Button>(R.id.MBOke)
            okButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0

                val isDuplicate = adapter.currentList.any { it.name == name }

                if (name == "" || price == 0.0 ){
                    Toast.makeText(this@ChangeAddOnActivity, getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
                } else if(isDuplicate) {
                    Toast.makeText(this@ChangeAddOnActivity,
                        getString(R.string.data_must_not_be_the_same_as_existing), Toast.LENGTH_SHORT).show()
                } else {
                    binding.LottieAV.playAnimation()
                    binding.PBLoad.visibility = View.VISIBLE
                    viewModel.addAddOn(AddAddOn(name = name, price = price))
                    dialog.dismiss()
                }
            }

            val closeButton = dialogLayout.findViewById<ImageButton>(R.id.IBClose)
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.IBBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}