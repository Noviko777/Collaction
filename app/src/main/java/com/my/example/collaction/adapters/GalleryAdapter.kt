package com.my.example.collaction.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my.example.collaction.R
import com.rishabhharit.roundedimageview.RoundedImageView

class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private lateinit var context: Context
    private lateinit var images: List<String>
    private lateinit var photoListener: PhotoListener

    constructor(context: Context, images: List<String>, photoListener: PhotoListener) : super() {
        this.context = context
        this.images = images
        this.photoListener = photoListener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false))


    override fun getItemCount(): Int = images.size

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images.get(position)

        Glide.with(context).load(image).into(holder.image!!)
        holder.itemView.setOnClickListener {
            photoListener.onPhotoClick(image)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: RoundedImageView?

        init {
            image = itemView?.findViewById(R.id.image)
        }
    }

    interface PhotoListener {
        fun onPhotoClick(image: String)
    }
}