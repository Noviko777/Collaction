package com.my.example.collaction.utilis

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class FirebaseHelper(private val activity: Activity) {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val storage: StorageReference = FirebaseStorage.getInstance().reference
    val database: DatabaseReference = FirebaseDatabase.getInstance("https://collaction-18109-default-rtdb.europe-west1.firebasedatabase.app/").reference

     fun updateUser(updates: Map<String, Any>, onSuccess: () -> Unit) {
        database.child("users").child(auth.currentUser!!.uid).updateChildren(updates).addOnCompleteListener {
            if(it.isSuccessful) {
                onSuccess()
            }
            else {
                Toast.makeText(activity, it.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun onConfirmPassword(email: String, newEmail: String, password: String, onSuccess: () -> Unit) {
        val credential = EmailAuthProvider.getCredential(email, password)
        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            if(it.isSuccessful) {
                auth.currentUser!!.updateEmail(newEmail).addOnCompleteListener {
                    if(it.isSuccessful) {
                        onSuccess()
                    }
                    else {
                        Toast.makeText(activity, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                Toast.makeText(activity, it.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun uploadUserPhoto(photo: Uri, onSuccess: (Uri?) -> Unit) {
        storage.child("users/${auth.currentUser!!.uid}/photo").putFile(photo).addOnCompleteListener {
            if(it.isSuccessful) {
                it.result!!.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                    if(it.isSuccessful) {
                        onSuccess(it.result)

                    }
                }

            }
            else {
                Toast.makeText(activity, it.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateUserPhoto(photoUrl: String, onSuccess: (String) -> Unit) {
        database.child("users/${auth.currentUser!!.uid}/photo").setValue(photoUrl).addOnCompleteListener {
            if(it.isSuccessful) {
                onSuccess(photoUrl)
            }
            else {
                Toast.makeText(activity, it.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}