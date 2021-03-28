package com.my.example.collaction.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my.example.collaction.R
import com.my.example.collaction.models.User
import com.rishabhharit.roundedimageview.RoundedImageView

class UsersAdapter(private val mContext: Context, private var mUsersList: List<User>, private var mFollows: Map<String, Boolean>,
                   private val listener: Listener)
    : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    private var mPositions = mapOf<String, Int>()

    fun update(usersList: List<User>, follows: Map<String, Boolean>) {
        mUsersList = usersList
        mFollows = follows
        mPositions = usersList.withIndex().map { (idx, user) -> user.uid!! to idx }.toMap()
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder
    = UserViewHolder(LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false))

    override fun getItemCount(): Int = mUsersList.count()

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        with(holder) {
            val user = mUsersList[position]
            val follows = mFollows[user.uid] ?: false
            val btn = itemView.findViewById<AppCompatButton>(R.id.follow_btn)
            if(follows) {
                btn.setBackgroundResource(R.drawable.btn_message_background)
                btn.setOnClickListener { listener.unFollow(user.uid!!) }
                btn.setTextColor(Color.BLACK)
                btn.text = "Followed"
            }
            else {
                btn.setBackgroundResource(R.drawable.btn_background)
                btn.setOnClickListener { listener.follow(user.uid!!)  }
                btn.setTextColor(Color.WHITE)
                btn.text = "Follow"
            }
            itemView.findViewById<TextView>(R.id.name_text).text = user.name
            itemView.findViewById<TextView>(R.id.username_text).text = user.username
            Glide.with(mContext).load(user.photo).placeholder(R.drawable.unnamed).into(itemView.findViewById<RoundedImageView>(R.id.profileImageView))
        }
    }

    interface Listener {
        fun follow(uid: String)
        fun unFollow(uid: String)
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}