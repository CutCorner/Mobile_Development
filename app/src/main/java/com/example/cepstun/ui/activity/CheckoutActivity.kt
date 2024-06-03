package com.example.cepstun.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.Model
import com.example.cepstun.databinding.ActivityCheckoutBinding
import com.example.cepstun.helper.getAdminFee
import com.example.cepstun.helper.withCurrencyFormat
import kotlinx.coroutines.launch
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    private lateinit var price: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedModel: Model? = intent.getParcelableExtra(SELECTED_MODEL)
        val selectedBarber = intent.getStringExtra(SELECTED_BARBER)

        binding.apply {
            selectedModel.let {
                price = it?.price?.get(0).toString()
                TVModelName.text = it?.name?.get(0).toString()
                Glide.with(this@CheckoutActivity)
                    .load(it?.image?.get(0).toString())
                    .into(IVHairModel)
            }

            val barberData = BarberDataList.barberDataValue.find { it.id == selectedBarber }
            TVBarberName.text = barberData?.name

            lifecycleScope.launch {
                val priceDouble = price.toDouble()
                TVPrice.text = priceDouble.toInt().toString().withCurrencyFormat()
                TVSubtotal.text = priceDouble.toInt().toString().withCurrencyFormat()
                TVAdminFee.text = price.getAdminFee().withCurrencyFormat()

                val subTotal = priceDouble.toInt()
                val adminFee = price.getAdminFee().toDouble().toInt()
                val total: Int = subTotal + adminFee

                val formattedCurrency = total.toString().withCurrencyFormat()
                TVTotal.text = formattedCurrency
            }
        }
    }

    companion object{
        const val SELECTED_MODEL = "selectedModel"
        const val SELECTED_BARBER = "selectedBarber"
    }
}