package com.example.cepstun.ui.activity.barbershop

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cepstun.R
import com.example.cepstun.data.local.AddModel
import com.example.cepstun.databinding.ActivityInputModelBinding
import com.example.cepstun.ui.adapter.barbershop.AddModelBarberAdapter
import com.example.cepstun.viewModel.InputModelViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class InputModelActivity: AppCompatActivity() {

    private lateinit var binding: ActivityInputModelBinding

    private val viewModel: InputModelViewModel by viewModels {
        ViewModelFactory.getInstance(this.application)
    }
    private lateinit var selectedImageView: ImageView

    private lateinit var adapter: AddModelBarberAdapter

    private lateinit var idBarber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idBarber = intent.getStringExtra(EXTRA_BARBERSHOP_ID).toString()

        settingStatusBar()

        binding.apply {

            adapter = AddModelBarberAdapter { model ->
                val updatedList = adapter.currentList.filter { it != model }
                adapter.submitList(updatedList)
            }
            binding.RVModelBarber.adapter = adapter


            FABAdd.setOnClickListener {
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.dialog_add_model, null)

                val builder = AlertDialog.Builder(this@InputModelActivity)
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
                        Toast.makeText(this@InputModelActivity, getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
                    } else if(isDuplicate) {
                        Toast.makeText(this@InputModelActivity,
                            getString(R.string.data_must_not_be_the_same_as_existing), Toast.LENGTH_SHORT).show()
                    } else {
                        val imageUri = selectedImageView.tag as? Uri
                        val model = AddModel(uri = imageUri!!, name = name, price = price)
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
                    Toast.makeText(this@InputModelActivity,
                        getString(R.string.please_add_at_least_one_model), Toast.LENGTH_SHORT).show()
                } else {
                    val alertDialogBuilder = AlertDialog.Builder(this@InputModelActivity)
                    alertDialogBuilder.setTitle(getString(R.string.add_model_to_your_barbershop))
                    alertDialogBuilder
                        .setMessage(getString(R.string.types_of_models_will_be_added_to_your_barbershop_make_sure_it_s_appropriate_you_cannot_return_to_this_page_after_continuing))
                        .setIcon(R.drawable.logo_icon_cir)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes)) { _, _->
                            viewModel.saveModel(adapter.currentList, idBarber)
                        }
                        .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                            dialog.cancel()
                        }
                    val alertDialog = alertDialogBuilder.create()

                    alertDialog.show()

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

    @Suppress("DEPRECATION")
    private fun settingStatusBar() {
        window.statusBarColor = getColor(R.color.brown)
        window.decorView.systemUiVisibility = 0
    }

    companion object {
        const val EXTRA_BARBERSHOP_ID = "extra_barbershop_id"
    }

}