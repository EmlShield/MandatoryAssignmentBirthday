package com.example.mandatoryassignment_birthday.data.repository

import com.example.mandatoryassignment_birthday.data.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(private val firebaseAuth: FirebaseAuth) {
    // Check if a user is already logged in
    fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let {
            User(userId = it.uid, email = it.email)
        }
    }

    suspend fun login(email: String, pass: String): User? {
        return try {
            // Use .await() to turn the Firebase "Task" into a suspend function
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val firebaseUser = result.user
            firebaseUser?.let {
                User(userId = it.uid, email = it.email)
            }
        } catch (e: Exception) {
            // TODO: Handle login errors
            null
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    suspend fun signUp(email: String, pass: String): User? {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val firebaseUser = result.user
            firebaseUser?.let {
                User(userId = it.uid, email = it.email)
            }
        } catch (e: Exception) {
            // TODO: Handle specific registration errors (email already in use)
            null
        }
    }
}