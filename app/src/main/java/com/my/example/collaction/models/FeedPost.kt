package com.my.example.collaction.models

import com.google.firebase.database.ServerValue
import java.util.*

data class FeedPost(val uid: String = "", val username: String = "", val photo: String? = "", val image: String = "",
                     val caption: String = "", val likesCount: Int = 0, val commentsCount: Int = 0,
                      val comments: List<Comment> = emptyList(), val timeStamp: Any = ServerValue.TIMESTAMP) {
    fun timeStampDate() : Date = Date(timeStamp as Long)
}
data class Comment(val uid: String, val username: String, val text: String) {

}