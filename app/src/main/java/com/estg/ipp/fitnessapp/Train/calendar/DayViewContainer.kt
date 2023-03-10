package com.estg.ipp.fitnessapp.train.calendar

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.estg.ipp.fitnessapp.R
import com.kizitonwose.calendarview.ui.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.calendarDayText)
    val dayLayout: LinearLayout = view.findViewById(R.id.day_layout)
    val eventIcon: ImageView = view.findViewById(R.id.event_icon)
}