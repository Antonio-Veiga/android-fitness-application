package com.estg.ipp.fitnessapp.train.calendar

import android.view.View
import android.widget.TextView
import com.estg.ipp.fitnessapp.R
import com.kizitonwose.calendarview.ui.ViewContainer

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.headerTextView)
    val monday = view.findViewById<TextView>(R.id.monday)
    val tuesday = view.findViewById<TextView>(R.id.tuesday)
    val wednesday = view.findViewById<TextView>(R.id.wednesday)
    val thursday = view.findViewById<TextView>(R.id.thursday)
    val friday = view.findViewById<TextView>(R.id.friday)
    val saturday = view.findViewById<TextView>(R.id.saturday)
    val sunday = view.findViewById<TextView>(R.id.sunday)
}