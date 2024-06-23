package com.example.cepstun.ui.activity.customer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.Model
import com.example.cepstun.databinding.ActivityCheckoutBinding
import com.example.cepstun.utils.getAdminFee
import com.example.cepstun.utils.withCurrencyFormat
import com.example.cepstun.ui.adapter.customer.AddOnAdapterCheckout
import com.example.cepstun.utils.getFullImageUrl
import com.example.cepstun.viewModel.CheckoutViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    private lateinit var price: String
    private var totalPrice: Int = 0
    private var addOnPrice: Int = 0

    private val viewModel: CheckoutViewModel by viewModels {
        ViewModelFactory.getInstance(this.application)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddOnAdapterCheckout

    private var selectedAddon: String? = null
    private val selectedAddOns = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val selectedModel: Model? = intent.getParcelableExtra(SELECTED_MODEL)
        val selectedBarber = intent.getStringExtra(SELECTED_BARBER)

        settingStatusBar()

        binding.apply {
            if (selectedBarber != null){
                viewModel.getAddOn(selectedBarber)

                viewModel.addOns.observe(this@CheckoutActivity) { addOns ->
                    if (addOns.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        TVEmpty.visibility = View.VISIBLE
                    } else {
                        adapter.submitList(addOns)
                        recyclerView.adapter = adapter
                    }
                }
            }

            selectedModel.let {
                price = it?.price.toString()
                TVModelName.text = it?.name
                Glide.with(this@CheckoutActivity)
                    .load(it?.image?.getFullImageUrl())
                    .into(IVHairModel)
            }

            calculatePrice(0)

            recyclerView = RVAddOn
            adapter = AddOnAdapterCheckout { addOn ->
                if (addOn.isSelected) {
                    addOnPrice += addOn.price
                    if (selectedAddOns.isNotEmpty()) {
                        selectedAddOns.append(", ")
                    }
                    selectedAddOns.append(addOn.name)
                } else {
                    addOnPrice -= addOn.price
                    val start = selectedAddOns.indexOf(addOn.name)
                    val end = start + addOn.name.length
                    selectedAddOns.delete(start, end)
                    if (selectedAddOns.isNotEmpty() && selectedAddOns[selectedAddOns.length - 1] == ' ') {
                        selectedAddOns.delete(selectedAddOns.length - 2, selectedAddOns.length)
                    }
                }
                selectedAddon = selectedAddOns.toString()
                calculatePrice(addOnPrice)
            }

            viewModel.nameBarber.observe(this@CheckoutActivity){
                TVBarberName.text = it
            }

            BCheckout.setOnClickListener {
                PBLoad.visibility = View.VISIBLE
                LottieAV.playAnimation()

                val model = TVModelName.text.toString()
                val price = totalPrice
                if (price == 0){
                    Toast.makeText(this@CheckoutActivity,
                        getString(R.string.please_wait_until_the_price_is_calculated), Toast.LENGTH_SHORT).show()
                    LottieAV.cancelAnimation()
                    PBLoad.visibility = View.GONE
                }else{
                    viewModel.bookedBarber(model, selectedAddon ?: "", price.toDouble(), selectedBarber.toString())
                    LottieAV.cancelAnimation()
                    PBLoad.visibility = View.GONE
                }
            }

            IBBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
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

        viewModel.isLoading.observe(this) {
            if (it) {
                binding.PBLoad.visibility = View.VISIBLE
                binding.LottieAV.playAnimation()
            } else {
                binding.PBLoad.visibility = View.GONE
                binding.LottieAV.cancelAnimation()
            }
        }

    }

    private fun calculatePrice(addOn: Int) {
        lifecycleScope.launch {
            binding.apply {
                PBLoad.visibility = View.VISIBLE
                LottieAV.playAnimation()

                val priceDouble = price.toDouble()
                val addOnDouble = addOn.toDouble()
                val subTotalView = priceDouble + addOnDouble
                TVPrice.text = priceDouble.toString().withCurrencyFormat(this@CheckoutActivity)
                TVSubtotal.text = subTotalView.toString().withCurrencyFormat(this@CheckoutActivity)
                TVAdminFee.text = subTotalView.toString().getAdminFee().withCurrencyFormat(this@CheckoutActivity)

                val subTotal = (priceDouble + addOnDouble).toInt()
                val adminFee = subTotal.toString().getAdminFee().toDouble().toInt()
                totalPrice = subTotal + adminFee

                val formattedCurrency = totalPrice.toString().withCurrencyFormat(this@CheckoutActivity)
                TVTotal.text = formattedCurrency

                PBLoad.visibility = View.GONE
                LottieAV.cancelAnimation()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun settingStatusBar() {
        window.statusBarColor = getColor(R.color.brown)
        window.decorView.systemUiVisibility = 0
    }

    companion object{
        const val SELECTED_MODEL = "selectedModel"
        const val SELECTED_BARBER = "selectedBarber"
    }
}