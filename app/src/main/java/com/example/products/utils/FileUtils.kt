package com.example.products.utils
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import java.io.File
object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File {
        val filePath = getRealPathFromURI(context, uri)
        return File(filePath)
    }
    private fun getRealPathFromURI(context: Context, contentUri: Uri?): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(
                contentUri!!, proj, null,
                null, null
            )
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }
}