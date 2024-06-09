package com.example.cepstun.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityChooseUserBinding
import com.example.cepstun.viewModel.ChooseUserViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class ChooseUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseUserBinding

    private val viewModel: ChooseUserViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var idToken: String? = null
        if (intent.hasExtra(ID_TOKEN)) {
            idToken = intent.getStringExtra(ID_TOKEN)
        }

        binding.apply {
            BChoose.backgroundTintList = ContextCompat.getColorStateList(this@ChooseUserActivity, R.color.gray2)

            LLCustomer.setOnClickListener {
                MCVCustomer.setCardBackgroundColor(getColor(R.color.brown))
                MCVCustomer.strokeColor = getColor(R.color.yellow)
                MCVBarber.setCardBackgroundColor(getColor(R.color.white))
                viewModel.selectedUser()
            }

            LLBarber.setOnClickListener {
                MCVBarber.setCardBackgroundColor(getColor(R.color.brown))
                MCVBarber.strokeColor = getColor(R.color.yellow)
                MCVCustomer.setCardBackgroundColor(getColor(R.color.white))
                viewModel.selectedUser()
            }

            viewModel.isUserSelected.observe(this@ChooseUserActivity) { isUserTypeSelected ->
                binding.BChoose.isEnabled = isUserTypeSelected
                BChoose.backgroundTintList =
                    ContextCompat.getColorStateList(this@ChooseUserActivity, if (isUserTypeSelected) R.color.brown else R.color.gray2)
            }

            BChoose.setOnClickListener {
                val userType =
                    if (binding.MCVCustomer.cardBackgroundColor.defaultColor == getColor(R.color.brown)) "Customer" else "Barber"

                if (idToken == null){
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@ChooseUserActivity,
                            Pair(binding.IVLogo2, "logo2"),
//                            Pair(binding.BChoose, "button")
                        )

                    Intent(this@ChooseUserActivity, SignUpActivity::class.java).also { intent ->
                        intent.putExtra(SignUpActivity.USER_LEVEL, userType)
                        startActivity(intent, optionsCompat.toBundle())
//                        startActivity(intent)

                        ActivityCompat.finishAfterTransition(this@ChooseUserActivity)
                    }
                } else{
                    viewModel.uploadToDatabase(userType)
                }
            }
        }

        viewModel.showToast.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

    }
    companion object{
        const val ID_TOKEN = "id_token"
    }
}