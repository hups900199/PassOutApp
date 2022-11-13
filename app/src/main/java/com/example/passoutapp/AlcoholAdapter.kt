package com.example.passoutapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import java.util.*

class AlcoholAdapter(private val alcoholsList: ArrayList<Alcohols>) : RecyclerView.Adapter<AlcoholAdapter.AlcoholViewHolder>() {

    fun deleteItem(i : Int) {
        alcoholsList.removeAt(i)
        notifyDataSetChanged()
    }

    fun addItem(i : Int, alcohols: Alcohols) {
        alcoholsList.add(i, alcohols)
        notifyDataSetChanged()
    }

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(getAdapterPosition(), getItemViewType())
        }
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcoholViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_alcohol, parent, false)
        return AlcoholViewHolder(itemView).listen { pos, type ->

            //TODO do other stuff here
            Log.d("GOOOOOOOO", "GOOOOOOO: ${pos} --- ${type} ---- ${alcoholsList.get(pos).id}")
        }
    }

    override fun onBindViewHolder(holder: AlcoholViewHolder, position: Int) {
        val currentItem = alcoholsList[position]
        holder.imv_alcohol.setImageResource(currentItem.image)
        holder.txv_alcohol_name.text = String.format(Locale.CANADA, "Name: %s", currentItem.name)
        holder.txv_alcohol_type.text = String.format(Locale.CANADA, "Type: %s", currentItem.type)
        holder.txv_alcohol_percentage.text = String.format(Locale.CANADA, "Percentage: %.02f %%", currentItem.percentage)
        holder.txv_liter.text = String.format(Locale.CANADA, "Liter: %.02f", currentItem.liter)
        holder.txv_date.text = String.format(Locale.CANADA, "Date: %s", currentItem.date)
    }

    override fun getItemCount(): Int {
        return alcoholsList.size
    }

    class AlcoholViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imv_alcohol : ShapeableImageView = itemView.findViewById(R.id.imv_alcohol)
        val txv_alcohol_name : TextView = itemView.findViewById(R.id.txv_alcohol_name)
        val txv_alcohol_type : TextView = itemView.findViewById(R.id.txv_alcohol_type)
        val txv_alcohol_percentage : TextView = itemView.findViewById(R.id.txv_alcohol_percentage)
        val txv_liter : TextView = itemView.findViewById(R.id.txv_liter)
        val txv_date : TextView = itemView.findViewById(R.id.txv_date)
    }
}