package com.estg.ipp.fitnessapp.train.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Database.Entities.Session
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Database.Queries.SessionWithTrain
import com.estg.ipp.fitnessapp.databinding.FragmentSessionBinding
import com.estg.ipp.fitnessapp.train.MainTrainingFragment
import com.estg.ipp.fitnessapp.train.SessionListFragment
import com.estg.ipp.fitnessapp.train.shared.SharedMethods
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class MySessionAdapter(
    private val mainFrag: MainTrainingFragment,
    private val values: MutableList<SessionWithTrain>,
    private val listFragment: SessionListFragment
) : RecyclerView.Adapter<MySessionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentSessionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.dayView.text =
            item.session.day.toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString()
        holder.nameView.text = item.train.name

        // If time for session passed remove removeButton
        if (item.session.day <= LocalDateTime.now()) {
            holder.removeButton.visibility = View.GONE
            if (item.session.status) {
                holder.doneImage.visibility = View.VISIBLE
            } else {
                holder.notDoneImage.visibility = View.VISIBLE
            }
        }

        holder.removeButton.setOnClickListener {
            removeFromDB(it.context, item.session)
            values.remove(item)
            SharedMethods.restartForegroundService(it.context)
            if (values.isEmpty()) {
                listFragment.dismiss()
            }
            mainFrag.updateEvents()
            notifyItemRemoved(holder.bindingAdapterPosition)
        }
    }

    private fun removeFromDB(context: Context, session: Session) {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)

        executors.execute {
            dao.deleteSession(session)
        }

        executors.shutdown()
        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val dayView = binding.itemDate
        val nameView = binding.itemName
        val removeButton = binding.removeButton
        val doneImage = binding.doneImage
        val notDoneImage = binding.notDoneImage

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }
}