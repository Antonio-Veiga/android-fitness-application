package com.estg.ipp.fitnessapp.runs

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Database.Entities.Run
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.FragmentRunsItemBinding


class RunsAdapter(var mContext: Context): RecyclerView.Adapter<RunsAdapter.ViewHolder>(){
    var data = listOf<Run>()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount () = data.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.mediumVelocity.text = item.avg_velocity
        holder.maxVelocity.text = item.max_velocity
        holder.burntFat.text = item.burned_calories
        holder.disTravelled.text = item.distance
        holder.time.text = item.Time

        var str = item.date.toString()
        str = str.replace("T", " ")
        str = str.split(".")[0]
        val title : String = mContext.getString(R.string.run_title)  + str
        holder.title.text = title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentRunsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ViewHolder(binding: FragmentRunsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val mediumVelocity: TextView = binding.mediumVelocity
        val maxVelocity: TextView = binding.maxVelocity
        val disTravelled: TextView = binding.disTravelled
        val time: TextView = binding.time
        val burntFat: TextView = binding.burntFat
        val title: TextView = binding.title
    }
}