package com.estg.ipp.fitnessapp.Home


import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Home.dataclass.SessaoENomeDoTreino
import com.estg.ipp.fitnessapp.databinding.FragmentExerciciosHojeBinding


class ExerciciosHojeAdapter(
    private val values: MutableList<SessaoENomeDoTreino>
) : RecyclerView.Adapter<ExerciciosHojeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentExerciciosHojeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.treinoNome
        val hora :String =  item.treinoTime.hour.toString() + ":" + item.treinoTime.minute.toString()
        holder.contentView.text = hora

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentExerciciosHojeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}