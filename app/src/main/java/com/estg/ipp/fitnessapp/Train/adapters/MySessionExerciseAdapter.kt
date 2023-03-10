package com.estg.ipp.fitnessapp.train.adapters

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.estg.ipp.fitnessapp.Database.Entities.Session
import com.estg.ipp.fitnessapp.Database.Entities.TrainingExercise
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Database.enumerations.ExerciseType
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.FragmentSessionExerciseBinding
import com.estg.ipp.fitnessapp.train.shared.SharedMethods
import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
@RequiresApi(Build.VERSION_CODES.O)
class MySessionExerciseAdapter(
    private val values: List<TrainingExercise>,
    private val session: Session,
    private val activity: Activity,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<MySessionExerciseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentSessionExerciseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        // Show exercise name
        holder.nameView.text = item.exerciseName

        holder.quantityView.text = SharedMethods.getQuantityToString(item.type, item.quantity)

        var timer = Timer()

        holder.startButton.setOnClickListener {
            if (item.type == ExerciseType.Time) {
                var quantity = item.quantity
                timer = Timer()
                timer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        activity.runOnUiThread {
                            quantity--
                            holder.quantityView.text =
                                SharedMethods.getQuantityToString(item.type, quantity)
                            if (quantity == 0)
                                timer.cancel()
                        }
                    }
                }, 1000, 1000)
            }

            if (position == values.size - 1) {
                holder.submitButton.visibility = View.VISIBLE
            }
            holder.restartLayout.visibility = View.VISIBLE
            holder.startLayout.visibility = View.GONE
        }

        holder.restartButton.setOnClickListener {
            timer.cancel()
            if (item.type == ExerciseType.Time) {
                timer = Timer()
                var quantity = item.quantity

                timer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        activity.runOnUiThread {
                            quantity--
                            holder.quantityView.text =
                                SharedMethods.getQuantityToString(item.type, quantity)
                            if (quantity == 0)
                                timer.cancel()
                        }
                    }
                }, 1000, 1000)
            }
        }

        holder.submitButton.setOnClickListener {
            if (session.sessionId == 0L) {
                createSession(it.context)
            } else {
                updateSession(it.context)
            }
            // Send to home fragment when ends
            Navigation.findNavController(it).navigate(R.id.action_nav_session_exercise_to_nav_home)
        }

        holder.previousItem.setOnClickListener {
            viewPager2.setCurrentItem(position - 1, true)
            if (item.type == ExerciseType.Time) {
                timer.cancel()
            }
        }

        holder.nextItem.setOnClickListener {
            viewPager2.setCurrentItem(position + 1, true)
            if (item.type == ExerciseType.Time) {
                timer.cancel()
            }
        }

        if (!(position == 0 && position == values.size - 1)) {
            when (position) {
                0 -> {
                    holder.nextItem.visibility = View.VISIBLE
                }
                values.size - 1 -> {
                    holder.previousItem.visibility = View.VISIBLE
                }
                else -> {
                    holder.nextItem.visibility = View.VISIBLE
                    holder.previousItem.visibility = View.VISIBLE
                }
            }
        }

        val resId = getResId(item.imagePath, R.drawable::class.java)
        Glide.with(activity.applicationContext).load(resId).into(holder.imageView)
    }

    private fun getResId(resName: String?, c: Class<*>): Int {
        return try {
            val idField: Field = c.getDeclaredField(resName)
            idField.getInt(idField)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    /**
     * Method responsible for inserting the training session
     * into the database
     */
    private fun createSession(context: Context) {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)
        session.status = true

        executors.execute {
            dao.insertSession(session)
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }
    }

    /**
     * Method responsible for changing the training session status
     * and updating the session in the database
     */
    private fun updateSession(context: Context) {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)
        session.status = true

        executors.execute {
            dao.updateSession(session)
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }
    }

    override fun getItemCount(): Int = values.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(binding: FragmentSessionExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nameView = binding.name
        val imageView = binding.image
        val quantityView = binding.quantity
        val startLayout = binding.startLayout
        val restartLayout = binding.restartLayout
        val startButton = binding.starButton
        val submitButton = binding.submitButton
        val restartButton = binding.restartButton
        val previousItem = binding.leftArrow
        val nextItem = binding.rightArrow

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }
}