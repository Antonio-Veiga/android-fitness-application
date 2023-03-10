package com.estg.ipp.fitnessapp.train.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.estg.ipp.fitnessapp.R
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(private var mSliderItems: MutableList<Bitmap>) :
    SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterViewHolder {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.slider_layout, null)
        return SliderAdapterViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: SliderAdapterViewHolder, position: Int) {
        val sliderItem: Bitmap = mSliderItems[position]
        holder.imageViewBackground.setImageBitmap(sliderItem)
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    class SliderAdapterViewHolder(itemView: View) :
        ViewHolder(itemView) {
        var imageViewBackground: ImageView = itemView.findViewById(R.id.myimage)
    }
}