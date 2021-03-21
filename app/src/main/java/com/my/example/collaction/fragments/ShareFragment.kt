package com.my.example.collaction.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.my.example.collaction.R
import com.my.example.collaction.adapters.GalleryAdapter
import com.my.example.collaction.interfaces.HomeListener
import com.my.example.collaction.utilis.CropHideBehavior
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_share.*
import java.io.File


class ShareFragment : Fragment() {

    private lateinit var mHomeListener: HomeListener

    private lateinit var shareRecyclerView: RecyclerView
    private lateinit var shareGalleryAdapter: GalleryAdapter

    private lateinit var images: List<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mHomeListener = context as HomeListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        images = mHomeListener.getGalleryImages { mHomeListener.getGalleryImages {} }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {

        return inflater.inflate(R.layout.fragment_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shareRecyclerView = view.findViewById(R.id.recyclerview_gallery_images)

        shareRecyclerView.setHasFixedSize(true)
        shareRecyclerView.layoutManager = GridLayoutManager(context, 3)

        shareGalleryAdapter = GalleryAdapter(context!!, images, object  : GalleryAdapter.PhotoListener{
            override fun onPhotoClick(image: String) {
//                val params: CoordinatorLayout.LayoutParams = cropLayout.layoutParams as CoordinatorLayout.LayoutParams
//                (params.behavior as CropHideBehavior).show(cropLayout)
               view.findViewById<CropImageView>(R.id.cropImageView).setImageUriAsync(Uri.fromFile(File(image)))
             //   view.findViewById<CropImageView>(R.id.cropImageView).scaleType = CropImageView.ScaleType.CENTER_CROP
            }

        })
        shareRecyclerView.adapter = shareGalleryAdapter


    }

    interface Listener {
       // fun getGalleryImages(getImages: (x: List<String>) -> Unit)
    }
}