package com.my.example.collaction.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my.example.collaction.R
import com.my.example.collaction.adapters.UsersAdapter
import com.my.example.collaction.interfaces.BaseFragmentListener
import com.my.example.collaction.interfaces.BaseOnClickListener
import com.my.example.collaction.models.User


class SearchFragment() : Fragment() {

    private lateinit var mListener: Listener
    private lateinit var mFragmentListener: BaseFragmentListener
    private lateinit var mBaseOnClickListener: BaseOnClickListener
    private lateinit var mAccountsRecyclerView: RecyclerView
    private lateinit var mUsersAdapter: UsersAdapter
    private lateinit var adapterListener: UsersAdapter.Listener


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
        adapterListener = context as UsersAdapter.Listener
        mBaseOnClickListener = context as BaseOnClickListener
        mFragmentListener = context as BaseFragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAccountsRecyclerView = view.findViewById(R.id.accounts_recyclerView)
        mAccountsRecyclerView.setHasFixedSize(true)
        mAccountsRecyclerView.layoutManager = LinearLayoutManager(context)

        mListener.getAllUsers {
            mUsersAdapter = UsersAdapter(context!!, it, mBaseOnClickListener.getUser().follows.toMutableMap(), adapterListener)
            mAccountsRecyclerView.adapter = mUsersAdapter
        }
        view.findViewById<View>(R.id.cancel_imageView).setOnClickListener {
            mFragmentListener.popFragment()
        }
    }

    override fun onStop() {
        mListener.detachAllUsers()
        super.onStop()
    }

    interface Listener {
        fun getAllUsers(onSuccess: (List<User>) -> Unit)
        fun detachAllUsers()
    }



}