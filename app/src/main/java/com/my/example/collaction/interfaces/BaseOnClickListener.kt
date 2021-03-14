package com.my.example.collaction.interfaces

import com.my.example.collaction.models.User

interface BaseOnClickListener {
    fun signOut()
    fun getUser(): User
}