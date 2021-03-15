package com.my.example.collaction.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.my.example.collaction.R
import com.my.example.collaction.interfaces.BaseOnClickListener
import com.my.example.collaction.models.User
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import kotlinx.android.synthetic.main.fragment_register_name.view.*


class EditProfileFragment : Fragment() {

    private lateinit var baseOnClickListener: BaseOnClickListener
    private lateinit var listener: Listener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseOnClickListener = context as BaseOnClickListener
        listener = context as Listener
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

        view.findViewById<ImageView>(R.id.save_image_view).setOnClickListener {
            val user = User(name = view.name_edit_text.text.toString(),
                    username = view.username_edit_text.text.toString(),
                    website = view.website_edit_text.text.toString(),
                    bio = view.bio_edit_text.text.toString(),
                    email = view.email_edit_text.text.toString(),
                    phone = view.phone_edit_text.text.toString())

            listener.updateProfile(user)
        }

    }

    interface Listener {
        fun updateProfile(user: User)
    }

}