package com.estg.ipp.fitnessapp.train.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.train.shared.SharedMethods
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder

class MyMonthBinder(private var context: Context) : MonthHeaderFooterBinder<MonthViewContainer> {
    private val monthsArray: Array<String> =
        context.resources.getStringArray(R.array.material_calendar_months_array)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun bind(container: MonthViewContainer, month: CalendarMonth) {
        // Resize text according to screen size
        val sizeName = SharedMethods.getSizeName(context)
        if (sizeName != "normal" && sizeName != "small") {
            val weekendSize = 25F
            container.textView.textSize = 40F
            container.monday.textSize = weekendSize
            container.tuesday.textSize = weekendSize
            container.wednesday.textSize = weekendSize
            container.thursday.textSize = weekendSize
            container.friday.textSize = weekendSize
            container.saturday.textSize = weekendSize
            container.sunday.textSize = weekendSize
        }
        container.textView.text = "${monthsArray[month.yearMonth.monthValue - 1]} ${month.year}"
    }

    override fun create(view: View): MonthViewContainer = MonthViewContainer(view)
}