package com.sample.ecommerce.utils

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import timber.log.Timber

object Uri2PathUtil {

    //Complex version processing (suiting for multiple APIs)
    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        val sdkVersion = Build.VERSION.SDK_INT
        if (sdkVersion < 11) return getRealPathFromUri_BelowApi11(context, uri)
        if (sdkVersion < 19) return getRealPathFromUri_Api11To18(context, uri)
        return if (sdkVersion < 29) getRealPathFromUri_Api19To28(context,
            uri) else getRealPathFromUri_AboveApi29(context, uri)
    }

    private fun getRealPathFromUri_AboveApi29(context: Context, uri: Uri): String {
        var path = ""
        try {
            var cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor!!.moveToFirst()
            var documentId = cursor.getString(0)
            documentId = documentId.substring(documentId.lastIndexOf(":") + 1)
            Timber.e("Document id : $documentId")
            cursor.close()
            cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media._ID + " = ? ", arrayOf(documentId),
                null
            )
            cursor!!.moveToFirst()
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return path
    }

    /**
     * Adapt to api19 and above, get the absolute path of the image according to uri
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getRealPathFromUri_Api19To28(context: Context, uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong())
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                val contentUri: Uri
                contentUri = if ("image" == type) {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Files.getContentUri("external")
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Adapt api11-api18, get the absolute path of the image according to uri
     */
    private fun getRealPathFromUri_Api11To18(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        //This has two packages that don't know which one. . . . However, this complex version is generally not used.
        val loader = CursorLoader(context, uri, projection, null, null, null)
        val cursor = loader.loadInBackground()
        if (cursor != null) {
            cursor.moveToFirst()
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]))
            cursor.close()
        }
        return filePath
    }

    /**
     * Adapt to api11 (excluding api11), get the absolute path of the image according to uri
     */
    private fun getRealPathFromUri_BelowApi11(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]))
            cursor.close()
        }
        return filePath
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.MediaColumns.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs,
                null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}