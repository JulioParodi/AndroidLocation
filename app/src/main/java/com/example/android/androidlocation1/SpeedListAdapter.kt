package com.example.android.androidlocation1

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SpeedListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<SpeedListAdapter.SpeedViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var speeds = emptyList<Speed>() // Cached copy of words

    inner class SpeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMySpeed: TextView = itemView.findViewById(R.id.textViewMySpeed)
        val textViewRealSpeed: TextView = itemView.findViewById(R.id.textViewRealSpeed)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeedViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return SpeedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SpeedViewHolder, position: Int) {
        val current = speeds[position]
        holder.textViewMySpeed.text = current.mySpeed
        holder.textViewRealSpeed.text = current.realSpeed
        holder.textViewDate.text = current.currentDate.replace("-","\n")
    }

    internal fun setSpeeds(speeds: List<Speed>) {
        this.speeds = speeds
        notifyDataSetChanged()
    }

    override fun getItemCount() = speeds.size
}