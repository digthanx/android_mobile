package com.teamforce.thanksapp.utils

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    StfalconImageViewer.Builder<String>(context, images)
    { imageView, image ->
        Glide.with(this)
            .load("${Consts.BASE_URL}${image}".toUri())
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_anon_avatar)
            .into(imageView)
    }
        .show()
}

fun ShapeableImageView.viewSinglePhoto(image: String, context: Context) {
    val images = mutableListOf<String>()
    val fullSizeImage = image.replace("_thumb", "")
    images.add(fullSizeImage)
    StfalconImageViewer.Builder<String>(context, images) { imageView, image ->
        Glide.with(this)
            .load("${Consts.BASE_URL}${image}".toUri())
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_anon_avatar)
            .into(imageView)
    }.withHiddenStatusBar(true)
        .show()
}

fun ShapeableImageView.imageView(image: String, context: Context, view: View) {
    val images = mutableListOf<String>()
    val fullSizeImage = image.replace("_thumb", "")
    images.add(fullSizeImage)
    StfalconImageViewer.Builder<String>(context, images) { imageView, image ->
        Glide.with(this)
            .load("${Consts.BASE_URL}${image}".toUri())
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_anon_avatar)
            .into(imageView)
    }.withHiddenStatusBar(true)
        .withOverlayView(view)
        .show()
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
fun showDialogAboutDownloadImage(
    photo: String,
    clickedView: View,
    context: Context,
    lifecycleScope: LifecycleCoroutineScope
) {
    MaterialAlertDialogBuilder(context)
        .setMessage(context.resources.getString(R.string.wouldYouLikeToSaveImage))

        .setNegativeButton(context.resources.getString(R.string.no)) { dialog, _ ->
            dialog.cancel()
        }
        .setPositiveButton(context.resources.getString(R.string.yes)) { dialog, which ->
            dialog.cancel()
            lifecycleScope.launch(Dispatchers.Main) {
                val url = "${Consts.BASE_URL}${photo.replace("_thumb", "")}"
                if (clickedView is ImageView) clickedView.downloadImage(url, context)
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
        Toast.makeText(context, "Фото скачивается", Toast.LENGTH_SHORT).show()
        withContext(Dispatchers.IO) {
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
        }
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


private fun ImageView.downloadImage(imageURL: String, context: Context) {
    if (!verifyPermissions(context)) return

    //val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/" + context.getString(R.string.app_name) + "/"
    val dirPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val imageName = "thanksApp_${System.currentTimeMillis()}.jpg"
    val dir = File(dirPath, imageName)
    val fileName = imageURL.substring(imageURL.lastIndexOf('/') + 1)
    Glide.with(context)
        .load(imageURL)
        .into(object : CustomTarget<Drawable?>() {

            override fun onResourceReady(
                resource: Drawable,
                transition: com.bumptech.glide.request.transition.Transition<in Drawable?>?
            ) {
                val bitmap = (resource as BitmapDrawable).bitmap
                Toast.makeText(context, "Saving Image...", Toast.LENGTH_SHORT).show()
                saveImage(bitmap, dir, fileName, context)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                Toast.makeText(
                    context,
                    "Failed to Download Image! Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
}

private fun saveImage(imageBitmap: Bitmap, storageDir: File, imageFileName: String, context: Context) {
    var successDirCreated = false
    if (!storageDir.exists()) {
        successDirCreated = storageDir.mkdir()
    }
    if (successDirCreated) {
        try {
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
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
                val image = File(imageDirectory, imageFileName)
                fos = FileOutputStream(image)
            }
            fos?.use {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.close()
                Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error while saving image!", Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    } else {
        Toast.makeText(context, "Failed to make folder!", Toast.LENGTH_SHORT).show()
    }
}

private fun verifyPermissions(context: Context): Boolean {

    when {
        ContextCompat.checkSelfPermission(
            context,
            WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED -> {
            return true
        }
        else -> {
            return false
        }
    }
}



class PosterOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    download: () -> Unit
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var onDeleteClick: () -> Unit = {}

    init {
        View.inflate(context, R.layout.layout_image_overlay, this)
        update()
    }

    fun update() {
        val toolbar = findViewById<Toolbar>(R.id.image_viewer_toolbar)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.download -> {
                    Toast.makeText(this.context, "download clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> true
            }
        }
    }
}