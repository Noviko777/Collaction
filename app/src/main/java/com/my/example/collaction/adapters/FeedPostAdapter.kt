package com.my.example.collaction.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my.example.collaction.R
import com.my.example.collaction.models.FeedPost

class FeedPostAdapter : RecyclerView.Adapter<FeedPostAdapter.FeedPostViewHolder> {
    private lateinit var mContext: Context
    private lateinit var mFeedPosts: List<FeedPost>

    constructor(mContext: Context, mFeedPosts: List<FeedPost>) : super() {
        this.mContext = mContext
        this.mFeedPosts = mFeedPosts
    }

    fun setFeedPosts(posts: List<FeedPost>) {
        mFeedPosts = mFeedPosts
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedPostViewHolder
            = FeedPostViewHolder(LayoutInflater.from(mContext).inflate(R.layout.feed_item, parent,false))

    override fun getItemCount(): Int = mFeedPosts.count()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FeedPostViewHolder, position: Int) {
        val post = mFeedPosts.get(position)

        Glide.with(mContext).load(post.image).into(holder.image)
        Glide.with(mContext).load(post.photo).into(holder.photo)

        holder.username.text = post.username
        holder.caption.text = post.caption
        holder.liked.text = "Liked ${post.likesCount} more"
    }


    class FeedPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageView
        val image: ImageView
        val username: TextView
        val caption: TextView
        val liked: TextView


        init {
            photo = itemView.findViewById(R.id.profileImageView)
            image = itemView.findViewById(R.id.post_imageView)
            username = itemView.findViewById(R.id.username_text)
            caption = itemView.findViewById(R.id.caption_text)
            liked = itemView.findViewById(R.id.likes_text)
        }
    }


}