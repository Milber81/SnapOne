package com.snapone.weatherproject.ui.main

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.snapone.weatherproject.R
import com.snapone.weatherproject.databinding.CityItemBinding

class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = CityItemBinding.bind(view)

    fun render(
        viewItem: CityViewItem,
        onClickListener: (CityViewItem) -> Unit,
        onRemoveItemListener: (CityViewItem) -> Unit
    ) {
        binding.cityAndCountry.text = viewItem.name
        binding.temperature.text = viewItem.temperature

        val url = getImageUrl(viewItem.icon)
        loadIcon(url, binding.imageView)

        itemView.setOnClickListener { onClickListener(viewItem) }
        binding.imgDelete.setOnClickListener { onRemoveItemListener(viewItem) }
    }

    private fun loadIcon(url: String, imageView: ImageView) {
        Glide.with(itemView).load(url).error(R.drawable.ic_launcher_foreground)
            .diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(imageView)
    }

    private fun getImageUrl(idIcon: String): String {
        return "https://openweathermap.org/img/wn/$idIcon@4x.png"
    }

}
