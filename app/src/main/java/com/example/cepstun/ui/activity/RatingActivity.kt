package com.example.cepstun.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberData
import com.example.cepstun.data.local.BarberDataList.barberDataValue
import com.example.cepstun.data.local.BarberDataList.rating
import com.example.cepstun.data.local.Rating
import com.example.cepstun.databinding.ActivityRatingBinding
import com.example.cepstun.ui.adapter.ReviewAdapter

class RatingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRatingBinding

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var barberId: String

    private lateinit var barberRating: Rating
    private lateinit var barberData: BarberData



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barberId = intent.extras?.getString(ID_BARBER)!!

        binding.apply {
            recyclerView = RVReview

            barberRating = rating.find { it.id == barberId }!!
            val averageRating = barberRating.ratingScore.average()
            val roundedRating = Math.round(averageRating * 10) / 10.0
            TVRate.text = getString(R.string.rate, roundedRating.toString())
            RBRate.rating = roundedRating.toFloat()


            barberData = barberDataValue.find { it.id == barberId }!!
            TVBarberName.text = barberData.name

            val totalReview = (barberRating.review.size)-1
            TVTotalRate.text = getString(R.string.rating, totalReview.toString())

            IBBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        }

        reviewAdapter = ReviewAdapter(barberId, rating)
        setRecyclerView()

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