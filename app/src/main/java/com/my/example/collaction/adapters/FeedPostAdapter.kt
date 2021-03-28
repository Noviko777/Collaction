package com.my.example.collaction.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
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
        holder.caption.text = post.caption

        val likedSpannable = SpannableString("${post.likesCount} more")
        likedSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, likedSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        likedSpannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, likedSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
      //  likedSpannable.setSpan(RelativeSizeSpan(1f), 0, likedSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        holder.liked.text = SpannableStringBuilder().append("liked ").append(likedSpannable)

        val usernameSpannable = SpannableString(post.username)
        usernameSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, usernameSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, usernameSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {

            }

            override fun updateDrawState(ds: TextPaint) { }
        }, 0, usernameSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        val captionSpannable = SpannableString(post.caption)
        captionSpannable.setSpan(RelativeSizeSpan(0.9f), 0, captionSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        var position = 0
        position = post.caption.indexOf("#", position)
        while (position != -1) {
            hashTagSpannable(captionSpannable, position, if (post.caption.indexOf(" ", position + 1) !== -1) post.caption.indexOf(" ", position + 1) else post.caption.length)
            position = post.caption.indexOf("#", position + 1)
        }

        holder.caption.text = SpannableStringBuilder().append(usernameSpannable).append("  ").append(captionSpannable)

        val commentsSpannable = SpannableString("(${post.commentsCount})")
        commentsSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, commentsSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        commentsSpannable.setSpan(ForegroundColorSpan(Color.rgb(205, 118, 151)),
                0, commentsSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        holder.comments.text = SpannableStringBuilder().append("View all comments ").append(commentsSpannable)


    }
    private fun hashTagSpannable(s: Spannable, start: Int, end: Int) {
        s.setSpan(ForegroundColorSpan(Color.rgb(176, 147, 222)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        s.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }


    class FeedPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageView
        val image: ImageView
        val username: TextView
        val caption: TextView
        val liked: TextView
        val comments: TextView


        init {
            photo = itemView.findViewById(R.id.profileImageView)
            image = itemView.findViewById(R.id.post_imageView)
            username = itemView.findViewById(R.id.username_text)
            caption = itemView.findViewById(R.id.caption_text)
            liked = itemView.findViewById(R.id.likes_text)
            comments = itemView.findViewById(R.id.commentsCount_text)
        }
    }


}