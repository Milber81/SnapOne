package com.snapone.weatherproject.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snapone.weatherproject.R

class CitiesAdapter(
    private var cityInfoList: MutableList<CityViewItem>,
    private val onClickListener: (CityViewItem) -> Unit,
    private val onItemRemoveClickListener: (CityViewItem) -> Unit
) : RecyclerView.Adapter<CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CityViewHolder(layoutInflater.inflate(R.layout.city_item, parent, false))
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val itemCityInfo = cityInfoList[position]

        holder.render(itemCityInfo, onClickListener, onItemRemoveClickListener)
    }

    override fun getItemCount(): Int = cityInfoList.size

    fun updateCity(viewItem: CityViewItem) {
        val index = cityInfoList.indexOf(viewItem)
        if (index != -1) {
            cityInfoList[index] = viewItem
            notifyItemChanged(index) // Notify only the specific item that was changed
        } else {
            cityInfoList.add(viewItem)
            notifyDataSetChanged()
        }
    }

    fun swapData(items: List<CityViewItem>){
        cityInfoList = items.toMutableList()
        notifyDataSetChanged()
    }
}
