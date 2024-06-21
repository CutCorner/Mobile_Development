package com.example.cepstun.ui.activity.customer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityRatingBinding
import com.example.cepstun.ui.adapter.customer.ReviewAdapter
import com.example.cepstun.viewModel.RatingViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class RatingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRatingBinding

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var recyclerView: RecyclerView

    private var barberId: String? = null

    private val viewModel: RatingViewModel by viewModels{
        ViewModelFactory.getInstance(this.application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barberId = intent.extras?.getString(ID_BARBER)

        binding.apply {
            recyclerView = RVReview

            viewModel.getRating(barberId!!)

            viewModel.listRating.observe(this@RatingActivity) { rating ->
                if (rating != null) {
//                    barberRating = rating.find { it.id == barberId }!!
                    val averageRating = rating.map { it.ratingScore }.average()
                    val roundedRating = Math.round(averageRating * 10) / 10.0
                    TVRate.text = getString(R.string.rate, roundedRating.toString())
                    RBRate.rating = roundedRating.toFloat()

                    val totalReview = (rating.size)-1
                    TVTotalRate.text = getString(R.string.rating, totalReview.toString())

                    reviewAdapter.submitList(rating)
                    setRecyclerView()
                }
            }



//            barberData = barberDataValue.find { it.id == barberId }!!
//            TVBarberName.text = barberData.name


            IBBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        }



        settingStatusBar()

    }

    private fun setRecyclerView() {
        binding.RVReview.layoutManager = LinearLayoutManager(this)
        binding.RVReview.adapter = reviewAdapter
    }

    @Suppress("DEPRECATION")
    private fun settingStatusBar() {
        window.statusBarColor = getColor(R.color.brown)
        window.decorView.systemUiVisibility = 0
    }

    companion object {
        const val ID_BARBER = "id_barber_rate"
    }
}