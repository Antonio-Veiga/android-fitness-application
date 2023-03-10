package com.estg.ipp.fitnessapp.Statistic

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Statistic.dataclass.ExercicioQuantidadeETipo
import com.estg.ipp.fitnessapp.databinding.FragmentPbItemBinding
import com.estg.ipp.fitnessapp.train.shared.SharedMethods

class PBRecyclerViewAdapter(
    private val values: MutableList<ExercicioQuantidadeETipo>
) : RecyclerView.Adapter<PBRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentPbItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.exerciseName.text = values[position].name
        holder.maxReps.text = SharedMethods.getQuantityToString(values[position].tipo,values[position].quantidade)
        holder.exerciseType.text = values[position].tipo.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentPbItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val exerciseName: TextView = binding.exerciseName
        val maxReps: TextView = binding.maxReps
        val exerciseType : TextView = binding.exerciseType

        override fun toString(): String {
            return super.toString() + " '" + maxReps.text + "'"
        }
    }
}