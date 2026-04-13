package com.example.mandatoryassignment_birthday.data.repository

import android.util.Log
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ImageRepository(private val storage: FirebaseStorage) {

    suspend fun uploadImage(imageUri: Uri): String? {
        return try {
            val fileName = "birthday/${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(fileName)

            ref.putFile(imageUri).await()

            val downloadUrl = ref.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("ImageRepository", "Image upload failed", e)
            null
        }
    }
}