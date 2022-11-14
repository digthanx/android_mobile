package com.teamforce.thanksapp.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.stfalcon.imageviewer.StfalconImageViewer
import com.teamforce.thanksapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL


fun ImageView.viewSinglePhoto(image: String, context: Context) {
    val images = mutableListOf<String>()
    val fullSizeImage = image.replace("_thumb", "").replace(Consts.BASE_URL, "")
    images.add(fullSizeImage)
    StfalconImageViewer.Builder<String>(context, images) { imageView, image ->
        Glide.with(this)
            .load("${Consts.BASE_URL}${image}".toUri())
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_anon_avatar)
            .into(imageView)
    }.withHiddenStatusBar(false).show()
}
//  TODO Пытался добавить внутрь просмотра сразу фичу скачивания, но там ничего не вышло у меня, разрабы библы пишут тчо нужно кастом вью делать
//fun ImageView.viewSinglePhoto2(image: String, context: Context) {
//    val images = mutableListOf<String>()
//    val fullSizeImage = image.replace("_thumb", "").replace(Consts.BASE_URL, "")
//    images.add(fullSizeImage)
//    StfalconImageViewer.Builder<String>(context, images) { imageView, image ->
//        Glide.with(this)
//            .load("${Consts.BASE_URL}${image}".toUri())
//            .fitCenter()
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .error(R.drawable.ic_anon_avatar)
//            .into(imageView)
//    }.withHiddenStatusBar(false)
//        .withImageChangeListener(showDialogAboutDownloadImage())
//        .show()
//}

// С ростом данного файла скоро придется расписать его в виде класса, хотя какой с этого толк...
fun showDialogAboutDownloadImage(photo: String, clickedView: View, context: Context, lifecycleScope: LifecycleCoroutineScope){
    MaterialAlertDialogBuilder(context)
        .setMessage(context.resources.getString(R.string.wouldYouLikeToSaveImage))

        .setNegativeButton(context.resources.getString(R.string.no)) { dialog, _ ->
            dialog.cancel()
        }
        .setPositiveButton(context.resources.getString(R.string.yes)) { dialog, which ->
            dialog.cancel()
            lifecycleScope.launch(Dispatchers.Main){
                if (clickedView is ImageView) clickedView.saveToStorage(photo, context)
            }
        }
        .show()
}

private suspend fun ImageView.saveToStorage(imageUri: String, context: Context) {
    val fullSizeImage = imageUri.replace("_thumb", "")
    val imageBitmap = withContext(Dispatchers.IO) {
        getUriFromBitmap(fullSizeImage)
    }
    val imageName = "thanksApp_${System.currentTimeMillis()}.jpg"
    var fos: OutputStream? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver?.also { resolver ->
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imageURI: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageURI?.let {
                resolver.openOutputStream(it)
            }
        }
    } else {
        val imageDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imageDirectory, imageName)
        // TODO Может привести к голоданию потока, что сделать?
        fos = FileOutputStream(image)
    }
    fos?.use {
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
        it.flush()
        it.close()
        Toast.makeText(context, "Successful load image", Toast.LENGTH_LONG).show()
    }
}



fun getUriFromBitmap(imageUri: String): Bitmap? {
    var image: Bitmap? = null
    try {
        val stringUrl = "${Consts.BASE_URL}${imageUri}"
        val url = URL(stringUrl)
        image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
    } catch (e: IOException) {
        System.out.println(e)
    }
    return image
}




