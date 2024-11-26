package com.snapone.weatherproject.ui.helpers

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.snapone.weatherproject.R

fun loadIcon(url: String, imageView: ImageView) {
    Glide.with(imageView).load(url).error(R.drawable.ic_launcher_foreground)
        .diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(imageView)
}

fun getImageUrl(idIcon: String): String {
    return "https://openweathermap.org/img/wn/$idIcon@4x.png"
}