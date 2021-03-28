package com.my.example.collaction.models

import com.google.firebase.database.Exclude

data class User (val name: String = "", val username: String = "", val website: String = "", val bio: String = "",
                    val email: String = "", val phone: String = "", val photo: String? = null, @Exclude val uid: String = "",
                    val follows: Map<String, Boolean> = emptyMap(), val followers: Map<String, Boolean> = emptyMap()) {
}