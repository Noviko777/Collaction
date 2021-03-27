package com.my.example.collaction.interfaces

import com.my.example.collaction.models.FeedPost

interface HomeListener {
    fun getGalleryImages(tryRequest: () -> Unit) : List<String>
    fun getUserPosts() : List<String>
    fun getAllFeedPosts(postsCallBack: (List<FeedPost>) -> Unit)
}