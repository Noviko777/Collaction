package com.my.example.collaction.fragments.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.my.example.collaction.R
import com.my.example.collaction.interfaces.LoginOnClickListener
import kotlinx.android.synthetic.main.fragment_login.view.*


class LoginFragment : Fragment() {

    private lateinit var mLoginOnClickListener: LoginOnClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mLoginOnClickListener = context as LoginOnClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.sign_in_btn.setOnClickListener {
            mLoginOnClickListener.signIn(view.email_text.text.toString(), view.password_text.text.toString())
        }
        view.to_sign_up.setOnClickListener {
            mLoginOnClickListener.onSignUp()
        }
    }
}