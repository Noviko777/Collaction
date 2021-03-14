package com.my.example.collaction.fragments.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.my.example.collaction.R
import com.my.example.collaction.interfaces.BaseLoginOnClickListener
import com.my.example.collaction.interfaces.NameOnClickListener
import kotlinx.android.synthetic.main.fragment_register_emailpass.view.*
import kotlinx.android.synthetic.main.fragment_register_name.*
import kotlinx.android.synthetic.main.fragment_register_name.view.*


class NameFragment : Fragment() {

    private lateinit var mNameOnClickListener: NameOnClickListener
    private lateinit var mBaseOnClickListener: BaseLoginOnClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mNameOnClickListener = context as NameOnClickListener
        mBaseOnClickListener = context as BaseLoginOnClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {

        return inflater.inflate(R.layout.fragment_register_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.next_btn.setOnClickListener {
            val name = view.name_text.text.toString()
            val username = view.username_text.text.toString()
            mNameOnClickListener.onNext(name, username)


        }
        view.findViewById<LinearLayout>(R.id.back_btn).setOnClickListener {
            mBaseOnClickListener.goToSignIn()
        }

    }

}