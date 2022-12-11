package com.example.passoutapp

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import java.util.*


class RoundAdapter(private val roundsList: ArrayList<Rounds>) : RecyclerView.Adapter<RoundAdapter.RoundViewHolder>() {

    fun deleteItem(i : Int) {
        roundsList.removeAt(i)
        notifyDataSetChanged()
    }

    fun addItem(i : Int, rounds: Rounds) {
        roundsList.add(i, rounds)
        notifyDataSetChanged()
    }

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(getAdapterPosition(), getItemViewType())
        }
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_round, parent, false)
        return RoundViewHolder(itemView).listen { pos, type ->

            //TODO do other stuff here
            Log.d("GOOOOOOOO", "GOOOOOOO: ${pos} --- ${type} ---- ${roundsList.get(pos).id}")

            val selectedId = roundsList.get(pos).id
            val selectedBac = roundsList.get(pos).bac
            val selectedStatus = roundsList.get(pos).status
            val selectedCreateDate = roundsList.get(pos).createDate
            val selectedFinishDate = roundsList.get(pos).finishDate

            val intent = Intent(parent.context, RoundDetailActivity::class.java)
            intent.putExtra("selectedPosition", pos);
            intent.putExtra("selectedId", selectedId);
            intent.putExtra("selectedBac", selectedBac.toString());
            intent.putExtra("selectedStatus", selectedStatus);
            intent.putExtra("selectedCreateDate", selectedCreateDate);
            intent.putExtra("selectedFinishDate", selectedFinishDate);
            parent.context.startActivity(intent)
        }
    }

    override fun onBindViewHolder(holder: RoundViewHolder, position: Int) {
        val currentItem = roundsList[position]
        holder.txv_bac.text = String.format(Locale.CANADA, "BAC: %.02f", currentItem.bac)
        holder.txv_status.text = String.format(Locale.CANADA, "Status: %s", currentItem.status)
        holder.txv_create_date.text = String.format(Locale.CANADA, "Create: %s", currentItem.createDate)
        holder.txv_finish_date.text = String.format(Locale.CANADA, "Finish: %s", currentItem.finishDate)
    }

    override fun getItemCount(): Int {
        return roundsList.size
    }

    class RoundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imv_round : ShapeableImageView = itemView.findViewById(R.id.imv_round)
        val txv_bac : TextView = itemView.findViewById(R.id.txv_bac)
        val txv_status : TextView = itemView.findViewById(R.id.txv_status)
        val txv_create_date : TextView = itemView.findViewById(R.id.txv_create_date)
        val txv_finish_date : TextView = itemView.findViewById(R.id.txv_finish_date)
    }
}