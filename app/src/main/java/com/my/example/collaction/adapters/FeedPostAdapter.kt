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
import com.my.example.collaction.interfaces.BaseOnClickListener
import com.my.example.collaction.models.FeedPost


class FeedPostAdapter(private val mListener: Listener, private val mContext: Context, private var mFeedPosts: MutableList<FeedPost>)
    : RecyclerView.Adapter<FeedPostAdapter.FeedPostViewHolder>() {

    private var userUid: String = (mContext as BaseOnClickListener).getCurrentUid()


    interface Listener {
        fun toggleLike(uid: String, feedId: String, onSuccess: (post: FeedPost) -> Unit)
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
        Glide.with(mContext).load(post.photo).error(R.drawable.unnamed).centerCrop().into(holder.profilePhoto)
        holder.username.text = post.username

        val likedSpannable = SpannableString("${post.likes.size} more")
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
        if(!post.caption.isNullOrEmpty()) {
            val captionSpannable = SpannableString(post.caption)
            captionSpannable.setSpan(RelativeSizeSpan(0.9f), 0, captionSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

            var position = 0
            position = post.caption.indexOf("#", position)
            while (position != -1) {
                hashTagSpannable(captionSpannable, position, if (post.caption.indexOf(" ", position + 1) !== -1) post.caption.indexOf(" ", position + 1) else post.caption.length)
                position = post.caption.indexOf("#", position + 1)
            }

            holder.caption.text = SpannableStringBuilder().append(usernameSpannable).append("  ").append(captionSpannable)
        }
        else
            holder.caption.visibility = View.GONE

        val commentsSpannable = SpannableString("(${post.commentsCount})")
        commentsSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, commentsSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        commentsSpannable.setSpan(ForegroundColorSpan(Color.rgb(205, 118, 151)),
                0, commentsSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        holder.comments.text = SpannableStringBuilder().append("View all comments ").append(commentsSpannable)
        holder.likeImageView.setOnClickListener {
            mListener.toggleLike(post.uid, post.id) {
                mFeedPosts[position] = it
                val likedSpannable = SpannableString("$it more")
                likedSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, likedSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                likedSpannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, likedSpannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                notifyItemChanged(position)
            }
        }
        if(post.likes.containsKey(userUid))
            holder.likeImageView.setImageResource(R.drawable.heart_active)
        else
            holder.likeImageView.setImageResource(R.drawable.heart)

    }
    private fun hashTagSpannable(s: Spannable, start: Int, end: Int) {
        s.setSpan(ForegroundColorSpan(Color.rgb(176, 147, 222)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        s.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }


    class FeedPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePhoto: ImageView = itemView.findViewById(R.id.profileImageView)
        val image: ImageView = itemView.findViewById(R.id.post_imageView)
        val username: TextView = itemView.findViewById(R.id.username_text)
        val caption: TextView = itemView.findViewById(R.id.caption_text)
        val liked: TextView = itemView.findViewById(R.id.likes_text)
        val comments: TextView = itemView.findViewById(R.id.commentsCount_text)
        val likeImageView: ImageView = itemView.findViewById(R.id.like_imageView)

    }


}