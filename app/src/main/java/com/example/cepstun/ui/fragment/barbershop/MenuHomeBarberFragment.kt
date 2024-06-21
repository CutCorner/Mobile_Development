package com.example.cepstun.ui.fragment.barbershop

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.data.local.ListOrder
import com.example.cepstun.data.remote.response.barber.StoreData
import com.example.cepstun.databinding.FragmentMenuHomeBarberBinding
import com.example.cepstun.ui.adapter.barbershop.CustomerOrderAdapter
import com.example.cepstun.viewModel.MenuHomeBarberViewModel
import com.example.cepstun.viewModel.ViewModelFactory


class MenuHomeBarberFragment : Fragment() {
    private var _binding: FragmentMenuHomeBarberBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: CustomerOrderAdapter

    private lateinit var barberData: List<StoreData>
    private lateinit var barberId: String

    private val viewModel: MenuHomeBarberViewModel by viewModels{
        ViewModelFactory.getInstance(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuHomeBarberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barberId = viewModel.getIdBarber().toString()

        viewModel.getBarberData()

        viewModel.getAllQueue(barberId)

        binding.apply {

            adapter = CustomerOrderAdapter()
            recyclerView = RVListCustomer
            recyclerView.adapter = adapter

            viewModel.barberData.observe(viewLifecycleOwner){
                binding.LottieAV.playAnimation()
                binding.PBLoad.visibility = View.VISIBLE
                barberData = it.store
                TVBarberName.text = barberData.first().name
                TVLocation.text = barberData.first().location
                binding.PBLoad.visibility = View.GONE
                binding.LottieAV.cancelAnimation()
            }

//            TVBarberName.text = "ALADEEN BARBERSHOP"
//            TVLocation.text = "Danakarya No.35, Ujung, Kec. Semampir, Surabaya, Jawa Timur 60155"

            switcher.setOnCheckedChangeListener { checked ->
                if (checked){
                    viewModel.setOpen(true)
                } else{
                    viewModel.setOpen(false)
                }
            }

            viewModel.order.observe(viewLifecycleOwner) { orders ->
                if (orders != null) {
                    TVEmpty.visibility = View.GONE
                    RVListCustomer.visibility = View.VISIBLE
                    adapter.submitList(null)
                    adapter.submitList(orders)

                    adapter.setOnItemClickListener(object : CustomerOrderAdapter.OnItemClickListener {

                        override fun onAcceptClick(order: ListOrder) {
                            viewModel.acceptOrder(order)
                        }

                        override fun onDeclineClick(order: ListOrder) {
                            val inflater = layoutInflater
                            val dialogLayout = inflater.inflate(R.layout.dialog_decline_order, null)

                            val builder = AlertDialog.Builder(requireContext())
                            builder.setView(dialogLayout)

                            val dialog = builder.create()

                            var choiceActive = 0

                            val choice1 = dialogLayout.findViewById<CardView>(R.id.CVCustomerIsNotOnSpot)
                            val text1 = dialogLayout.findViewById<TextView>(R.id.TVCustomerIsNotOnSpot)

                            val choice2 = dialogLayout.findViewById<CardView>(R.id.CVBarbershopClose)
                            val text2 = dialogLayout.findViewById<TextView>(R.id.TVBarbershopClose)

                            val otherReason = dialogLayout.findViewById<EditText>(R.id.ETOtherReason)

                            val defaultCardBackgroundColor = choice1.cardBackgroundColor.defaultColor
                            val defaultTextColor = text1.currentTextColor

                            choice1.setOnClickListener {

                                if (choiceActive != 1) {
                                    choice1.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.brown))
                                    choice2.setCardBackgroundColor(defaultCardBackgroundColor)
                                    text1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                    text2.setTextColor(defaultTextColor)
                                    choiceActive = 1
                                } else {
                                    choice1.setCardBackgroundColor(defaultCardBackgroundColor)
                                    text1.setTextColor(defaultTextColor)
                                    choiceActive = 0
                                }
                            }

                            choice2.setOnClickListener {
                                if (choiceActive != 2) {
                                    choice2.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.brown))
                                    choice1.setCardBackgroundColor(defaultCardBackgroundColor)
                                    text2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                    text1.setTextColor(defaultTextColor)
                                    choiceActive = 2
                                } else {
                                    choice2.setCardBackgroundColor(defaultCardBackgroundColor)
                                    text2.setTextColor(defaultTextColor)
                                    choiceActive = 0
                                }
                            }

                            val okButton = dialogLayout.findViewById<Button>(R.id.MBOke)
                            okButton.setOnClickListener {
                                if (choiceActive == 1){
                                    viewModel.declineOrder(order, "Pelanggan tidak berada di tempat")
                                } else if (choiceActive == 2){
                                    viewModel.declineOrder(order, "Barbershop akan tutup")
                                } else {
                                    if (otherReason.text.toString() != ""){
                                        viewModel.declineOrder(order, otherReason.text.toString())
                                    } else {
                                        Toast.makeText(requireContext(),
                                            getString(R.string.please_fill_the_reason), Toast.LENGTH_SHORT).show()
                                    }
                                }
                                dialog.dismiss()
                            }

                            dialog.show()
                        }
                    })
                } else {
                    RVListCustomer.visibility = View.GONE
                    TVEmpty.text =
                        getString(R.string.no_one_has_booked_make_sure_you_have_opened_a_barbershop)
                    TVEmpty.visibility = View.VISIBLE
                }
            }

        }

        viewModel.message.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(viewLifecycleOwner){
            if (it){
                binding.LottieAV.playAnimation()
                binding.PBLoad.visibility = View.VISIBLE
            } else {
                binding.LottieAV.cancelAnimation()
                binding.PBLoad.visibility = View.GONE
            }
        }

    }

    override fun onResume() {
        viewModel.cekBarbershopOpen().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isOpen = task.result
                activity?.runOnUiThread {
                    binding.switcher.setChecked(isOpen)
                }
            }
        }
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}