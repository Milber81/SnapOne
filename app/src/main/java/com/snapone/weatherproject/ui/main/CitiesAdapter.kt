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

    override fun onBindViewHolder(
        holder: CityViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            // No payloads, bind full view
            super.onBindViewHolder(holder, position, payloads)
        } else {
            // Handle partial updates (payloads)
            val itemCityInfo = cityInfoList[position]
            payloads.forEach { payload ->
                if (payload is Boolean && payload == true) {
                    holder.updateTextAndIcon(itemCityInfo)
                }
            }
        }
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
            notifyItemChanged(index, true) // Notify only the specific item that was changed
        } else {
            cityInfoList.add(viewItem)
            notifyDataSetChanged()
        }
    }

    fun removeCity(viewItem: CityViewItem){
        val index = cityInfoList.indexOf(viewItem)
        if (index != -1) {
            cityInfoList.remove(viewItem)
            notifyItemRemoved(index) // Notify only the specific item is removed
        }
    }
}
