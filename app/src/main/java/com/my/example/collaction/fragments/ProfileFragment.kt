package com.my.example.collaction.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.my.example.collaction.HomeActivity
import com.my.example.collaction.R
import com.my.example.collaction.interfaces.BaseOnClickListener
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    lateinit var parentActivity: HomeActivity
    lateinit var baseOnClickListener: BaseOnClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as HomeActivity
        baseOnClickListener = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = baseOnClickListener.getUser()

        view.editProfileImageView.setOnClickListener {
            parentActivity.openOtherFragment(EditProfileFragment())
        }
        Glide.with(view).load(user.photo).centerCrop().into(view.findViewById(R.id.profileImageView))
        view.findViewById<TextView>(R.id.username_text).text = user.username
    }
}