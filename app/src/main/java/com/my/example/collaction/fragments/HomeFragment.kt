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
import com.my.example.collaction.adapters.FeedPostAdapter
import com.my.example.collaction.interfaces.BaseOnClickListener
import com.my.example.collaction.interfaces.HomeListener
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    lateinit var baseOnClickListener: BaseOnClickListener
    lateinit var homeListener: HomeListener

    private lateinit var feedPostRecyclerView: RecyclerView
    private lateinit var feedPostAdapter: FeedPostAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseOnClickListener = context as BaseOnClickListener
        homeListener = context as HomeListener
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
        super.onViewCreated(view, savedInstanceState)

        feedPostRecyclerView = view.findViewById(R.id.posts_recyclerView)
        feedPostRecyclerView.setHasFixedSize(true)
        feedPostRecyclerView.layoutManager = LinearLayoutManager(context)
        homeListener.getAllFeedPosts() {
            feedPostAdapter = FeedPostAdapter(context!!,it)
            feedPostRecyclerView.adapter = feedPostAdapter
        }
    }

    override fun onStop() {
        homeListener.detachFeedPosts()
        super.onStop()
    }
}
