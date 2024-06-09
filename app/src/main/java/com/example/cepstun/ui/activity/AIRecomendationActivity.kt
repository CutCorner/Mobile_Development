package com.example.cepstun.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.data.local.ModelDataRecommendationList
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


        binding.apply {
            TVShape.text = "Processing..."

            viewModel.result.observe(this@AIRecomendationActivity) { result ->
//                val resultText = result.first?.get(0)
//                val resultText = result.first?.toString()
                val resultText = result.first?.get(0)?.split(":")?.get(0)

                binding.TVShape.text = resultText

                when(selectedModelType){
                    "Straight" -> {
                        when(resultText){
                            "Heart" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelHeartStraight)
                            "Oblong" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelOblongStraight)
                            "Oval" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelOvalStraight)
                            "Round" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelRoundStraight)
                            "Square" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelSquareStraight)
                        }
                    }
                    "Curly" -> {
                        when(resultText){
                            "Heart" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelHeartCurly)
                            "Oblong" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelOblongCurly)
                            "Oval" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelOvalCurly)
                            "Round" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelRoundCurly)
                            "Square" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelSquareCurly)
                        }
                    }
                    "Wavy" -> {
                        when(resultText){
                            "Heart" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelHeartWavy)
                            "Oblong" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelOblongWavy)
                            "Oval" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelOvalWavy)
                            "Round" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelRoundWavy)
                            "Square" -> adapter = ModelRecommendationAdapter(ModelDataRecommendationList.modelSquareWavy)
                        }
                    }
                }
                showRecyclerList()
            }

            viewModel.error.observe(this@AIRecomendationActivity) {
                binding.TVShape.text = it
            }

            BBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showRecyclerList() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }
}