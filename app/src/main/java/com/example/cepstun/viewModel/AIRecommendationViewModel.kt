package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.tensorflow.lite.task.vision.classifier.Classifications

class AIRecommendationViewModel: ViewModel() {

    private val _result = MutableLiveData<Pair<List<String>?, Long>>()
    val result: LiveData<Pair<List<String>?, Long>> = _result

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    @SuppressLint("DefaultLocale")
    fun onResult(results: Classifications?, inferenceTime: Long) {
        val resultText = results?.categories?.map {
            val formattedScore = String.format("%.2f", it.score * 100)
            "${it.label}: $formattedScore%"
        }
        _result.value = Pair(resultText, inferenceTime)
    }

    fun onError(error: String) {
        _error.value = error
    }
}

//class AIRecommendationViewModel : ViewModel() {
//
//    // For result
//    private val _result = MutableLiveData<Pair<List<Pair<String, Float>>?, Long>>()
//    val result: LiveData<Pair<List<Pair<String, Float>>?, Long>> = _result
//
//    // For error
//    private val _error = MutableLiveData<String>()
//    val error: LiveData<String> = _error
//
//    fun onResult(results: List<Pair<String, Float>>?, inferenceTime: Long) {
//        _result.value = Pair(results, inferenceTime)
//    }
//
//    fun onError(error: String) {
//        _error.value = error
//    }
//}