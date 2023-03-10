package com.estg.ipp.fitnessapp.train.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Database.Entities.Exercise
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Database.enumerations.ExerciseType
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.FragmentExerciseBinding
import com.estg.ipp.fitnessapp.train.view_models.ExerciseViewModel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
@RequiresApi(Build.VERSION_CODES.O)
class MyExerciseAdapter(
    private val values: ExerciseViewModel
) : RecyclerView.Adapter<MyExerciseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentExerciseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values.exerciseLiveData.value!![position]

        // Configure number pickers
        holder.secondPicker.minValue = 0
        holder.secondPicker.maxValue = 59
        holder.minutePicker.minValue = 0
        holder.minutePicker.maxValue = 59
        holder.hourPicker.minValue = 0
        holder.hourPicker.maxValue = 23

        // Configure meter pickers
        holder.kilometerPicker.minValue = 0
        holder.kilometerPicker.maxValue = 100
        holder.meterPicker.minValue = 0
        holder.meterPicker.maxValue = 999

        // Get exercises from database
        val context = holder.itemLine.context
        val listOfExercises = getAllExercises(context)
        var listOfTypesAllowed = listOf<ExerciseType>()

        // Apply a numeration to each exercise
        holder.numberText.text =
            context.resources.getString(R.string.exercise) + (position + 1).toString()

        // Check if its a preloaded exercise
        if (item.exerciseName != "") {
            holder.nameText.setText(item.exerciseName, false)
            holder.typeText.setText(item.type.name, false)
            holder.typeLayout.visibility = View.VISIBLE

            // Create an adapter with the list of exercise names
            listOfTypesAllowed =
                listOfExercises.first { it.exerciseName == item.exerciseName }.typesAllowed

            val typeAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                holder.nameText.context,
                android.R.layout.simple_list_item_1, listOfTypesAllowed.map { it.name }
            )
            // Set the new adapter
            holder.typeText.setAdapter(typeAdapter)

            // Show necessary input and hide others
            when (item.type) {
                ExerciseType.Time -> {
                    val hours: Int = item.quantity / 3600
                    val minutes: Int = item.quantity % 3600 / 60
                    val seconds: Int = item.quantity % 3600 % 60

                    holder.secondPicker.value = seconds
                    holder.minutePicker.value = minutes
                    holder.hourPicker.value = hours

                    holder.numberPickers.visibility = View.VISIBLE
                    holder.quantityLayout.visibility = View.GONE
                    holder.metersPicker.visibility = View.GONE
                }
                ExerciseType.Distance -> {
                    val kilometers: Int = item.quantity / 1000
                    val meters = item.quantity % 1000

                    holder.kilometerPicker.value = kilometers
                    holder.meterPicker.value = meters

                    holder.metersPicker.visibility = View.VISIBLE
                    holder.numberPickers.visibility = View.GONE
                    holder.quantityLayout.visibility = View.GONE
                }
                else -> {
                    holder.quantity.setText(item.quantity.toString())

                    holder.quantityLayout.visibility = View.VISIBLE
                    holder.numberPickers.visibility = View.GONE
                    holder.metersPicker.visibility = View.GONE
                }
            }
        }

        // Create an adapter with the list of exercise names
        val nameAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            holder.nameText.context,
            android.R.layout.simple_list_item_1, listOfExercises.map { it.exerciseName }
        )
        // Set the new adapter
        holder.nameText.setAdapter(nameAdapter)

        holder.nameText.setOnItemClickListener { _, _, pos, _ ->
            // Update list of exercises if the name of the current exercise changed
            val data = values.exerciseLiveData.value as MutableList
            data[position].exerciseName = listOfExercises[pos].exerciseName
            data[position].type = ExerciseType.None
            data[position].imagePath = listOfExercises[pos].imagePath
            data[position].quantity = -1

            // Load new exercise type list
            listOfTypesAllowed = listOfExercises[pos].typesAllowed
            // Create an adapter with the list of exercise types
            val typeAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                holder.typeText.context,
                android.R.layout.simple_list_item_1, listOfTypesAllowed.map { it.name }
            )
            holder.typeText.setText("")
            holder.typeText.setAdapter(typeAdapter)

            // Show edit input for exerciser type
            holder.typeLayout.visibility = View.VISIBLE
            // Hide all other additional layouts
            holder.quantityLayout.visibility = View.GONE
            holder.numberPickers.visibility = View.GONE
            holder.metersPicker.visibility = View.GONE
        }

        holder.typeText.setOnItemClickListener { _, _, pos, _ ->
            // Update list of exercises if the type of the current exercise changed
            val data = values.exerciseLiveData.value as MutableList
            data[position].type = listOfTypesAllowed[pos]
            data[position].quantity = -1

            // Show necessary input and hide others
            when (listOfTypesAllowed[pos]) {
                ExerciseType.Time -> {
                    holder.secondPicker.value = 0
                    holder.minutePicker.value = 0
                    holder.hourPicker.value = 0

                    holder.numberPickers.visibility = View.VISIBLE
                    holder.quantityLayout.visibility = View.GONE
                    holder.metersPicker.visibility = View.GONE
                }
                ExerciseType.Distance -> {
                    holder.meterPicker.value = 0
                    holder.kilometerPicker.value = 0

                    holder.metersPicker.visibility = View.VISIBLE
                    holder.numberPickers.visibility = View.GONE
                    holder.quantityLayout.visibility = View.GONE
                }
                else -> {
                    holder.quantity.text.clear()
                    holder.quantityLayout.visibility = View.VISIBLE
                    holder.numberPickers.visibility = View.GONE
                    holder.metersPicker.visibility = View.GONE
                }
            }
        }

        // Update list of exercises if the quantity of the current exercise changed
        holder.quantity.doOnTextChanged { text, _, _, _ ->
            var tempQuantity: Int = -1
            if (text != null && text != "" && text != "0" && text.isNotEmpty()) {
                tempQuantity = text.toString().toInt()
            }
            if (tempQuantity != -1) {
                val data = values.exerciseLiveData.value as MutableList
                data[position].quantity = tempQuantity
            }
        }

        // On Time pickers value change
        holder.secondPicker.setOnValueChangedListener { _, _, newVal ->
            val data = values.exerciseLiveData.value as MutableList
            if (newVal == 0 && holder.minutePicker.value == 0 && holder.hourPicker.value == 0) {
                data[position].quantity = -1
            } else {
                data[position].quantity =
                    newVal + (holder.minutePicker.value * 60) + (holder.hourPicker.value * 60 * 60)
            }
        }
        holder.minutePicker.setOnValueChangedListener { _, _, newVal ->
            val data = values.exerciseLiveData.value as MutableList
            if (newVal == 0 && holder.secondPicker.value == 0 && holder.hourPicker.value == 0) {
                data[position].quantity = -1
            } else {
                data[position].quantity =
                    holder.secondPicker.value + (newVal * 60) + (holder.hourPicker.value * 60 * 60)
            }
        }
        holder.hourPicker.setOnValueChangedListener { _, _, newVal ->
            val data = values.exerciseLiveData.value as MutableList
            if (newVal == 0 && holder.minutePicker.value == 0 && holder.secondPicker.value == 0) {
                data[position].quantity = -1
            } else {
                data[position].quantity =
                    holder.secondPicker.value + (holder.minutePicker.value * 60) + (newVal * 60 * 60)
            }
        }

        // On Meter pickers value change
        holder.meterPicker.setOnValueChangedListener { _, _, newVal ->
            val data = values.exerciseLiveData.value as MutableList
            if (newVal == 0 && holder.kilometerPicker.value == 0) {
                data[position].quantity = -1
            } else {
                data[position].quantity = newVal + (holder.kilometerPicker.value * 1000)
            }
        }
        holder.kilometerPicker.setOnValueChangedListener { _, _, newVal ->
            val data = values.exerciseLiveData.value as MutableList
            if (newVal == 0 && holder.meterPicker.value == 0) {
                data[position].quantity = -1
            } else {
                data[position].quantity = holder.meterPicker.value + (newVal * 1000)
            }
        }

        // Disable the first remove button
        if (position == 0) {
            holder.removeButton.isEnabled = false
            holder.removeButton.setBackgroundColor(Color.GRAY)
        }

        // Listener to remove the exercise
        holder.removeButton.setOnClickListener {
            val data = values.exerciseLiveData.value!!
            data.remove(item)
            notifyItemRemoved(position)
        }
    }


    private fun getAllExercises(context: Context): List<Exercise> {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)
        var exercises = mutableListOf<Exercise>()

        executors.execute {
            exercises = dao.getAllExercises() as MutableList<Exercise>
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            return exercises
        } catch (e: InterruptedException) {
            throw e
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = values.exerciseLiveData.value!!.size

    inner class ViewHolder(binding: FragmentExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val numberText: TextView = binding.exerciseNumber
        val nameText: AutoCompleteTextView = binding.exercise
        val typeText: AutoCompleteTextView = binding.type
        val quantity: EditText = binding.quantity
        val removeButton: Button = binding.removeExeButton
        val itemLine = binding.itemLine
        val hourPicker = binding.hourPicker
        val minutePicker = binding.minutePicker
        val secondPicker = binding.secondPicker
        val numberPickers = binding.numberPickers
        val typeLayout = binding.typeLayout
        val quantityLayout = binding.quantityLayout
        val kilometerPicker = binding.kilometerPicker
        val meterPicker = binding.meterPicker
        val metersPicker = binding.metersPickers

        override fun toString(): String {
            return super.toString() + " '" + nameText.text + "'"
        }
    }

}