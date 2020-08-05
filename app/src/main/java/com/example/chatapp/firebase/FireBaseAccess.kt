package com.example.chatapp.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

object FireBaseAccess {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val auth = Firebase.auth
    val storage = FirebaseStorage.getInstance()

    val usersDataBaseReference = database.reference.child("users")
    val storageDataBaseReference = storage.reference.child("chat_images")
    val messagesDataBaseReference = database.reference.child("messages")
}