package com.my.example.collaction.utilis

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

class ImagesGallery {
    companion object {
        fun listOfImages(context: Context) : ArrayList<String> {
            var uri: Uri? = null
            var cursor: Cursor? = null
            var columnIndexData: Int
            var columnIndexFolderName: Int
            var listOfAllImages: ArrayList<String> = ArrayList()
            var absolutePathOfImage: String

            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val orderBy = MediaStore.Video.Media.DATE_TAKEN

            cursor = context.contentResolver!!.query(uri, projection, null, null, "$orderBy DESC")
            columnIndexData = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

            // get folder name
            //columnIndexFolderName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor!!.moveToNext()) {
                absolutePathOfImage = cursor.getString(columnIndexData)
                listOfAllImages.add(absolutePathOfImage)
            }
            return listOfAllImages
        }
    }
}