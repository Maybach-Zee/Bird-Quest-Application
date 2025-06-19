package com.example.birdquestapplicationfinal

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private val itemList: ArrayList<PicturesB>,
    private val onItemClick: (PicturesB) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemHolder>() {

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.textViewName)
        val itemYear: TextView = itemView.findViewById(R.id.textViewYear)
        val itemPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ItemHolder(itemView)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val currentItem = itemList[position]
        holder.itemName.text = currentItem.name ?: "No Bird Name"
        holder.itemYear.text = currentItem.year ?: "No Year"
        holder.itemPrice.text = currentItem.price ?: "No Description"

        val bytes = android.util.Base64.decode(currentItem.imageURL, android.util.Base64.DEFAULT)
        val bitMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        holder.itemImage.setImageBitmap(bitMap)

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }
}


