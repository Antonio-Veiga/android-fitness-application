package com.estg.ipp.fitnessapp.Locations.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Locations.GeopifyAPI.DataClasses.Features
import com.estg.ipp.fitnessapp.Locations.LocationsFragmentDirections
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.LocationItemBinding
import org.apache.commons.text.WordUtils

class Adapter(private var mContext: Context) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    var data = listOf<Features>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        var categories = ""

        if (item.properties!!.name == null) {
            holder.titleText.text = mContext.getString(R.string.Undefined)
        } else {
            holder.titleText.text = WordUtils.capitalizeFully(item.properties!!.name)
        }
        val addresline2: String =
            mContext.getString(R.string.Adress) + item.properties!!.addressLine2
        holder.addressText.text = addresline2
        val distance: String =
            mContext.getString(R.string.Distance) + "${item.properties!!.distance} m"
        holder.distanceText.text = distance

        for (value in item.properties!!.categories) {
            var filteredStr = value.replace("_", " ")
            filteredStr = filteredStr.replace(".", "/")
            categories += filteredStr + " e "
        }
        categories = categories.dropLast(3)

        val typeText: String =mContext.getString(R.string.building_type) + categories
        holder.typeText.text = typeText


        holder.typeText.isSelected = true
        holder.titleText.isSelected = true
        holder.distanceText.isSelected = true
        holder.addressText.isSelected = true

        //Associate a Image
        if (categories.contains("building")) {
            holder.image.setImageResource(R.drawable.gym)
        } else if (categories.contains("track")) {
            holder.image.setImageResource(R.drawable.track)
        } else if (categories.contains("fitness")) {
            holder.image.setImageResource(R.drawable.fitness)
        } else if (categories.contains("sport club")) {
            holder.image.setImageResource(R.drawable.stadium)
        } else if (categories.contains("national park")) {
            holder.image.setImageResource(R.drawable.park)
        }


        holder.cardView.setOnClickListener {
            val navController = Navigation.findNavController(it)
            navController.navigate(
                LocationsFragmentDirections.actionShowLocationsToSeeLocationDetails(
                    item
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LocationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(binding: LocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cardView: CardView = binding.clickMe
        val titleText: TextView = binding.Title
        val addressText: TextView = binding.Address
        val distanceText: TextView = binding.Distance
        val typeText: TextView = binding.Type
        var image: ImageView = binding.image
    }
}
