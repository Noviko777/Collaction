package com.my.example.collaction.interfaces

interface HomeListener {
    fun getGalleryImages(tryRequest: () -> Unit) : List<String>
    fun getPosts() : List<String>
}