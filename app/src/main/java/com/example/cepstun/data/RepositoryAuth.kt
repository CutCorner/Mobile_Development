package com.example.cepstun.data


import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class RepositoryAuth private constructor(
    val auth: FirebaseAuth
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun loginEmailPassword(email: String, password: String, messageSuccess: String, messageFail: String, messageFail2: String): Pair<Boolean, String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val verification = auth.currentUser?.isEmailVerified
            if (verification == true) {
                Pair(true, messageSuccess)
            } else {
                auth.signOut()
                Pair(false, messageFail)
            }
        } catch (e: Exception) {
            auth.signOut()
            Pair(false, String.format(messageFail2, e.message))
        }
    }

    suspend fun loginWithGoogle(account: GoogleSignInAccount): Pair<Boolean, String> {
        val idToken = account.idToken ?: return Pair(false, "")
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val task = auth.signInWithCredential(firebaseCredential).await()
        return Pair(task.user != null, idToken)
    }

    suspend fun loginWithFacebook(token: AccessToken, failedToLinkAccounts: String): Pair<Boolean, String> {
        val idToken = token.token
        try {
            val credential = FacebookAuthProvider.getCredential(idToken)
            val task = auth.signInWithCredential(credential).await()
            return Pair(task.user != null, idToken)
        } catch (e: FirebaseAuthUserCollisionException) {
            try {
                val newCredential = FacebookAuthProvider.getCredential(idToken)
                val newTask = auth.signInWithCredential(newCredential).await()
                return Pair(newTask.user != null, idToken)
            } catch (createException: Exception) {
                return Pair(false, failedToLinkAccounts + createException.message)
            }
        } catch (e: Exception) {
            return Pair(false, e.message ?: "Unknown error occurred")
        }
    }

    suspend fun resendEmail(email: String, password: String, send: String, verified: String, fail: String): String {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val verification = auth.currentUser?.isEmailVerified
            if (verification == true) {
                auth.signOut()
                send
            } else {
                auth.signOut()
                verified
            }
        }catch (e: Exception){
            auth.signOut()
            fail + e.message.toString()
        }
    }

    suspend fun forgetPassword(email: String, send: String): String {
        return try {
            auth.sendPasswordResetEmail(email).await()
            send
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    fun logout() {
        auth.signOut()
    }


    suspend fun registerEmailPassword(email: String, password: String): Pair<Boolean, String> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            auth.currentUser?.reload()
            auth.currentUser?.sendEmailVerification()?.await()
            Pair(true, "")
        } catch (e: Exception) {
            Pair(false, e.message ?: "An error occurred")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RepositoryAuth? = null

        fun clearInstance(){
            INSTANCE = null
        }

        fun getInstance(auth: FirebaseAuth): RepositoryAuth {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryAuth(auth)
                INSTANCE = instance
                instance
            }
        }
    }
}