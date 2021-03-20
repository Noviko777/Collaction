package com.my.example.collaction

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageOptions
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_crop_image.*
import java.io.ByteArrayOutputStream


class CropImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_image)


        (cropImageView as CropImageView).setImageUriAsync(Uri.parse(intent.extras!!.getString("imageUri")))
        cropImageView.setAspectRatio(4, 4)

        findViewById<View>(R.id.save_image_view).setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra("result", getImageUri(this, cropImageView.croppedImage).toString())
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
        findViewById<View>(R.id.cancelImageView).setOnClickListener {
            val returnIntent = Intent()

            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}