package com.example.cepstun.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BarbershopViewModel (
    private val auth: RepositoryAuth,
    private val database: RepositoryDatabase
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: MutableLiveData<String> = _message

    private val _allQueue = MutableLiveData<Long>()
    val allQueue: LiveData<Long> = _allQueue


    fun cekBarberQueue(barberId: String){
        val user = auth.currentUser
        val uid = user?.uid
        val dbRef = database.getDatabase()
        val userRef = dbRef.child("users").child(uid!!)
        userRef.get().addOnSuccessListener {


            val barberRef = dbRef.child("queue").child(barberId)

            barberRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        _allQueue.value = dataSnapshot.childrenCount
                    } else {
                        _allQueue.value = 0
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _message.value = error.message
                }
            })
        }
    }
}