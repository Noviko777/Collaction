package com.my.example.collaction.fragments

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.my.example.collaction.R
import com.my.example.collaction.adapters.GalleryAdapter
import com.my.example.collaction.interfaces.BaseFragmentListener
import com.my.example.collaction.interfaces.HomeListener
import com.my.example.collaction.utilis.CropHideBehavior
import com.my.example.collaction.utilis.QuickHideBehavior
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_share.*
import java.io.File


class PublishPostFragment : Fragment() {

    private lateinit var mHomeListener: HomeListener
    private lateinit var mBaseListener: BaseFragmentListener
    private lateinit var mListener: Listener


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mHomeListener = context as HomeListener
        mBaseListener = context as BaseFragmentListener
        mListener = context as Listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {

        return inflater.inflate(R.layout.fragment_publish_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.crop_image).setImageBitmap(mListener.getPostBitmap())
        view.findViewById<View>(R.id.cancel_image_view).setOnClickListener {
            mBaseListener.popFragment()
        }
        view.findViewById<View>(R.id.save_image_view).setOnClickListener {
            mListener.shareImage(view.findViewById<EditText>(R.id.caption_text).text.toString())
        }
    }

    interface Listener {
       // fun getGalleryImages(getImages: (x: List<String>) -> Unit)
        fun getPostBitmap(): Bitmap
        fun shareImage(caption: String)
    }
}