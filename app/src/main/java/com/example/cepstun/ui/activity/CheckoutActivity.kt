package com.example.cepstun.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.Model
import com.example.cepstun.databinding.ActivityCheckoutBinding
import com.example.cepstun.helper.getAdminFee
import com.example.cepstun.helper.withCurrencyFormat
import com.example.cepstun.viewModel.CheckoutViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    private lateinit var price: String
    private var totalPrice: Int = 0

    private val viewModel: CheckoutViewModel by viewModels {
        ViewModelFactory.getInstance(this.application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedModel: Model? = intent.getParcelableExtra(SELECTED_MODEL)
        val selectedBarber = intent.getStringExtra(SELECTED_BARBER)

//        viewModel.observeQueue(selectedBarber.toString())

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
                TVPrice.text = priceDouble.toString().withCurrencyFormat()
                TVSubtotal.text = priceDouble.toString().withCurrencyFormat()
                TVAdminFee.text = price.getAdminFee().withCurrencyFormat()

                val subTotal = priceDouble.toInt()
                val adminFee = price.getAdminFee().toDouble().toInt()
                totalPrice = subTotal + adminFee

                val formattedCurrency = totalPrice.toString().withCurrencyFormat()
                TVTotal.text = formattedCurrency
            }

            BCheckout.setOnClickListener {
                val model = TVModelName.text.toString()
                val price = totalPrice
                viewModel.bookedBarber(model, "Message", price.toDouble(), selectedBarber.toString())
            }

        }

        viewModel.message.observe(this) {
            if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.startActivityEvent.observe(this) { queue ->
            Intent(this, BarberLocationActivity::class.java).also {
                it.putExtra(BarberLocationActivity.ID_BARBER, selectedBarber.toString())
                it.putExtra(BarberLocationActivity.YOUR_QUEUE, queue.toString())
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

    }

    companion object{
        const val SELECTED_MODEL = "selectedModel"
        const val SELECTED_BARBER = "selectedBarber"
    }
}