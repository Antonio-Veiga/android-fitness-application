package com.estg.ipp.fitnessapp.train.adapters

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Database.Entities.Session
import com.estg.ipp.fitnessapp.Database.Entities.Train
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Database.Queries.TrainWithExercises
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.FragmentTrainBinding
import com.estg.ipp.fitnessapp.train.SessionExerciseFragment
import com.estg.ipp.fitnessapp.train.shared.SharedMethods
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
@RequiresApi(Build.VERSION_CODES.O)
class MyTrainingAdapter(
    private val values: MutableList<TrainWithExercises>
) : RecyclerView.Adapter<MyTrainingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentTrainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        var isExpanded = false

        holder.idView.text = (position + 1).toString()
        holder.contentView.text = item.train.name
        if (item.train.favorite) {
            holder.favoriteIcon.setImageResource(R.drawable.ic_baseline_star_24)
        } else {
            holder.favoriteIcon.setImageResource(R.drawable.ic_baseline_star_border_24)
        }

        holder.favoriteIcon.setOnClickListener {
            item.train.favorite = !item.train.favorite
            updateOnDatabase(it.context, item.train)
            if (item.train.favorite) {
                holder.favoriteIcon.setImageResource(R.drawable.ic_baseline_star_24)
            } else {
                holder.favoriteIcon.setImageResource(R.drawable.ic_baseline_star_border_24)
            }
        }

        holder.editButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(
                "training",
                TrainWithExercises(
                    train = item.train.copy(),
                    trainingExercises = item.trainingExercises.toMutableList()
                )
            )
            Navigation.findNavController(it)
                .navigate(R.id.action_nav_train_list_to_nav_add_train2, bundle)
        }

        holder.itemLine.setOnClickListener {
            isExpanded = !isExpanded
            if (isExpanded)
                holder.expandableList.visibility = View.VISIBLE
            else holder.expandableList.visibility = View.GONE
        }

        holder.removeButton.setOnClickListener {
            removeTrainFromDB(it.context, item.train)
            values.remove(item)
            SharedMethods.restartForegroundService(it.context)
            notifyItemRangeChanged(position, itemCount - position + 1)
        }

        holder.startButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(SessionExerciseFragment.ARG_TITLE, item.train.name)
            bundle.putSerializable(
                SessionExerciseFragment.ARG_SESSION,
                Session(sessionTrainId = item.train.trainId, day = LocalDateTime.now())
            )
            Navigation.findNavController(it)
                .navigate(R.id.action_nav_train_list_to_nav_session_exercise, bundle)
        }

        holder.expandableList.adapter = MyTrainingDetailAdapter(item.trainingExercises)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentTrainBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView = binding.itemNumber
        val contentView = binding.content
        val itemLine = binding.itemLine
        val favoriteIcon = binding.favoriteIconButton
        val removeButton = binding.removeButton
        val expandableList = binding.expandableList
        val editButton = binding.editButton
        val startButton = binding.startButton

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    private fun updateOnDatabase(context: Context, item: Train) {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)

        executors.execute {
            dao.updateTraining(item)
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }
    }

    private fun removeTrainFromDB(context: Context, train: Train) {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)

        executors.execute {
            dao.deleteTraining(train)
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }
    }
}