package com.example.cepstun.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.data.local.ModelDataList
import com.example.cepstun.databinding.ActivityAirecomendationBinding
import com.example.cepstun.helper.ImageClassifierHelper
import com.example.cepstun.ui.adapter.ModelRecommendationAdapter
import com.example.cepstun.viewModel.AIRecomendationViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class AIRecomendationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAirecomendationBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ModelRecommendationAdapter

//    private var currentImageUri: Uri? = null

    private lateinit var selectedModelType: String

    private val viewModel: AIRecomendationViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAirecomendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentImageUri = intent.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
        selectedModelType = intent.getStringExtra(CameraActivity.MODEL_TYPE).toString()

        imageClassifierHelper = ImageClassifierHelper(context = this, viewModel = viewModel)


        currentImageUri?.let {
            binding.IVPhoto.setImageURI(it)
            imageClassifierHelper.classifyStaticImage(it)
        }

        recyclerView = binding.RVModelRecomend


        // sementara ngambil data dari object dulu sambil nunggu model ML dan data CC
        adapter = ModelRecommendationAdapter(ModelDataList.modelDataValue)

        showRecyclerList()




        binding.apply {
            TVShape.text = "Processing..."

            viewModel.result.observe(this@AIRecomendationActivity) { result ->
//                val resultText = result.first?.get(0)
//                val resultText = result.first?.toString()
                val resultText = result.first?.get(0)?.split(":")?.get(0)
//                val inferenceTime = result.second
                binding.TVShape.text = resultText

//                binding.TVInference.text = getString(R.string.inference_time, inferenceTime.toString())

//                val timeNow = updateTimeStamp()
                // Save to database
//                viewModel.saveToDatabase(resultText, inferenceTime, imageUri.toString(), timeNow)
            }

            viewModel.error.observe(this@AIRecomendationActivity) {
                binding.TVShape.text = it
            }

            BBack.setOnClickListener {
                Log.d("Back", "Pressed")
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showRecyclerList() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }
}