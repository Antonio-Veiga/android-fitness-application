package com.estg.ipp.fitnessapp.train.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Database.Entities.TrainingExercise
import com.estg.ipp.fitnessapp.databinding.FragmentTrainingDetailBinding
import com.estg.ipp.fitnessapp.train.shared.SharedMethods

class MyTrainingDetailAdapter(
    private val values: List<TrainingExercise>
) : RecyclerView.Adapter<MyTrainingDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentTrainingDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.exerciseName
        holder.quantityView.text = SharedMethods.getQuantityToString(item.type, item.quantity)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentTrainingDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nameView: TextView = binding.exerciseName
        val quantityView = binding.exerciseQuantity

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }
}