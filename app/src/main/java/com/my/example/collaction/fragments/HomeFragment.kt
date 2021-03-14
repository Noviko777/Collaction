package com.my.example.collaction.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.my.example.collaction.R
import com.my.example.collaction.interfaces.BaseOnClickListener
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    lateinit var baseOnClickListener: BaseOnClickListener

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

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.home_text.setOnClickListener {
            baseOnClickListener.signOut()
        }
        super.onViewCreated(view, savedInstanceState)
    }

}
