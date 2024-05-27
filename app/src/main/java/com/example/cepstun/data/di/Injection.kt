package com.example.cepstun.data.di

import android.content.Context
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object Injection {

    fun provideRepositoryAuth(): RepositoryAuth = RepositoryAuth.getInstance(FirebaseAuth.getInstance())

    fun provideRepositoryDatabase(): RepositoryDatabase = RepositoryDatabase.getInstance(FirebaseDatabase.getInstance())
}