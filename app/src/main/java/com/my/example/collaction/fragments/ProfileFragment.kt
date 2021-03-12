package com.my.example.collaction.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.my.example.collaction.HomeActivity
import com.my.example.collaction.R
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    lateinit var parentActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as HomeActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        view.editProfileImageView.setOnClickListener {
            parentActivity.openOtherFragment(Fragment(R.layout.fragment_edit_profile))
        }
    }
}