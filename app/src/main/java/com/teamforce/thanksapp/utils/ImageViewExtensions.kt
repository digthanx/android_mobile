package com.teamforce.thanksapp.utils

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.imageview.ShapeableImageView
import com.stfalcon.imageviewer.StfalconImageViewer
import com.teamforce.thanksapp.R

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