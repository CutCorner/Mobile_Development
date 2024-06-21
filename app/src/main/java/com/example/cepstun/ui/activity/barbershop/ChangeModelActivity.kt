package com.example.cepstun.ui.activity.barbershop

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.data.local.AddModel
import com.example.cepstun.databinding.ActivityChangeModelBinding
import com.example.cepstun.ui.adapter.barbershop.ChangeModelAdapter
import com.example.cepstun.viewModel.ChangeModelViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class ChangeModelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeModelBinding

    private lateinit var adapter: ChangeModelAdapter
    private lateinit var recyclerView: RecyclerView

    private val viewModel: ChangeModelViewModel by viewModels{
        ViewModelFactory.getInstance(this.application)
    }

    private lateinit var selectedImageView: ImageView

//    Tambahkan pengecekan permisison dan permintaan permisison untuk mengambil media

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChangeModelAdapter()
        recyclerView = binding.RVModelBarber

        viewModel.getBarberData()

        viewModel.barberData.observe(this) {resultData ->
            resultData.store.forEach { storeData ->
                if (storeData.model.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    binding.TVEmpty.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    binding.TVEmpty.visibility = View.GONE
                    adapter.submitList(storeData.model)
                    recyclerView.adapter = adapter
                }

                adapter.setOnItemClickCallback(object : ChangeModelAdapter.OnChangeClickListener {
                    override fun onDeleteClick(name: String) {
                        val inflater = layoutInflater
                        val dialogLayout = inflater.inflate(R.layout.dialog_delete_model, null)

                        val builder = AlertDialog.Builder(this@ChangeModelActivity)
                        builder.setView(dialogLayout)

                        val dialog = builder.create()

                        val closeDialog = dialogLayout.findViewById<ImageButton>(R.id.IBClose)
                        closeDialog.setOnClickListener {
                            dialog.dismiss()
                        }

                        val modelName = dialogLayout.findViewById<TextView>(R.id.TVModelName)
                        modelName.text = name

                        val okButton = dialogLayout.findViewById<Button>(R.id.deleteButton)
                        okButton.setOnClickListener {
                            viewModel.deleteModel(name)
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

        binding.fabAdd.setOnClickListener {
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.dialog_add_model, null)

            val builder = AlertDialog.Builder(this@ChangeModelActivity)
            builder.setView(dialogLayout)

            val imageView = dialogLayout.findViewById<ImageView>(R.id.IVModel)
            val nameEditText = dialogLayout.findViewById<EditText>(R.id.ETNameModel)
            val priceEditText = dialogLayout.findViewById<EditText>(R.id.ETPriceModel)

            imageView.setOnClickListener {
                openGalleryForModel(imageView)
            }

            val dialog = builder.create()

            val okButton = dialogLayout.findViewById<Button>(R.id.MBOke)
            okButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0

                val isDuplicate = adapter.currentList.any { it.name == name }

                if (name == "" || price == 0.0 || ::selectedImageView.isInitialized.not()){
                    Toast.makeText(this@ChangeModelActivity, getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
                } else if(isDuplicate) {
                    Toast.makeText(this@ChangeModelActivity,
                        getString(R.string.data_must_not_be_the_same_as_existing), Toast.LENGTH_SHORT).show()
                } else {
                    binding.LottieAV.playAnimation()
                    binding.PBLoad.visibility = View.VISIBLE
                    viewModel.addModel(AddModel(name = name, uri = selectedImageView.tag as Uri, price = price))
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

    private fun openGalleryForModel(imageView: ImageView){
        selectedImageView = imageView
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageView.setImageURI(uri)
            selectedImageView.tag = uri
        } else {
            Toast.makeText(this, getString(R.string.no_media_selected), Toast.LENGTH_SHORT).show()
        }
    }

}