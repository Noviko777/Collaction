package com.my.example.collaction.fragments.login

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
import com.my.example.collaction.interfaces.EmailOnClickListener
import com.my.example.collaction.interfaces.NameOnClickListener
import kotlinx.android.synthetic.main.fragment_register_emailpass.view.*
import kotlinx.android.synthetic.main.fragment_register_name.*
import kotlinx.android.synthetic.main.fragment_register_name.view.*


class EmailFragment : Fragment() {

    private lateinit var mEmailOnClickListener: EmailOnClickListener
    private lateinit var mBaseOnClickListener: BaseLoginOnClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mEmailOnClickListener = context as EmailOnClickListener
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

        return inflater.inflate(R.layout.fragment_register_emailpass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.sign_up_btn.setOnClickListener {
            val email = view.email_text.text.toString()
            val password = view.password_text.text.toString()
            mEmailOnClickListener.signUp(email, password)

        }
        view.findViewById<LinearLayout>(R.id.back_btn).setOnClickListener {
            mBaseOnClickListener.goToSignIn()
        }
    }

}