package com.my.example.collaction.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.my.example.collaction.R
import com.my.example.collaction.interfaces.BaseOnClickListener
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*


class EditProfileFragment : Fragment() {

    private lateinit var baseOnClickListener: BaseOnClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseOnClickListener = context as BaseOnClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {

        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = baseOnClickListener.getUser()

        view.name_edit_text.setText(user.name)
        view.username_edit_text.setText(user.username)
        view.website_edit_text.setText(user.website)
        view.bio_edit_text.setText(user.bio)
        view.email_edit_text.setText(user.email)
        view.phone_edit_text.setText(user.phone)


    }

}