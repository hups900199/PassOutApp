package com.example.passoutapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class AlcoholAdapter(private val alcoholsList: ArrayList<Alcohols>) : RecyclerView.Adapter<AlcoholAdapter.AlcoholViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcoholViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_alcohol, parent, false)
        return AlcoholViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlcoholViewHolder, position: Int) {
        val currentItem =alcoholsList[position]
        holder.titleImage.setImageResource(currentItem.titleIamge)
        holder.tvHeading.text = currentItem.heading
    }

    override fun getItemCount(): Int {
        return alcoholsList.size
    }

    class AlcoholViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleImage : ShapeableImageView = itemView.findViewById(R.id.title_image)
        val tvHeading : TextView = itemView.findViewById(R.id.tvHeading)
    }
}