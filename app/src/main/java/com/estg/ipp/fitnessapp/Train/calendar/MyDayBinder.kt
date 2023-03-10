package com.estg.ipp.fitnessapp.train.calendar

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.estg.ipp.fitnessapp.Database.Queries.SessionWithTrain
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.train.MainTrainingFragment
import com.estg.ipp.fitnessapp.train.SessionListFragment
import com.estg.ipp.fitnessapp.train.shared.SharedMethods
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class MyDayBinder(
    private var frag: MainTrainingFragment,
    private var context: Context,
    private var sessionList: List<SessionWithTrain>
) :
    DayBinder<DayViewContainer> {
    override fun bind(container: DayViewContainer, day: CalendarDay) {
        container.textView.text = day.date.dayOfMonth.toString()

        // Resize text according to screen size
        val sizeName = SharedMethods.getSizeName(context)
        if (sizeName != "normal" && sizeName != "small") {
            container.textView.textSize = 35F
        }

        if (day.owner == DayOwner.THIS_MONTH) {
            if (day.date >= LocalDate.now()) {
                container.textView.setTextColor(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.gold,
                        null
                    )
                )
            } else {
                container.textView.setTextColor(Color.LTGRAY)
            }
        } else {
            container.textView.setTextColor(Color.GRAY)
        }

        val daySessionList = sessionList.toMutableList()
        daySessionList.removeAll { it.session.day.toLocalDate() != day.date }
        if (daySessionList.isNotEmpty()){
            container.eventIcon.visibility = View.VISIBLE
        }

        container.dayLayout.setOnClickListener {
            // Check the day owner as we do not want to select in or out dates.
            if (day.owner == DayOwner.THIS_MONTH && day.date >= LocalDate.now()) {
                onDayClick(day.date.atStartOfDay(), daySessionList)
            }
        }
    }

    override fun create(view: View) = DayViewContainer(view)

    /**
     * Calendar's Day Click Listener
     */
    private fun onDayClick(date: LocalDateTime, daySessionList: List<SessionWithTrain>) {
        val lastDay = Calendar.getInstance()
        lastDay.add(Calendar.DAY_OF_YEAR, -1)

        if (localDateTimeToDate(date) > lastDay) {
            val day: LocalDate = date.toLocalDate()

            if (sessionList.isEmpty()) {
                SharedMethods.openAddSessionDialog(frag, day)
            } else {
                if (daySessionList.isEmpty()) {
                    SharedMethods.openAddSessionDialog(frag, day)
                } else {
                    // Create Bundle
                    val bundle = Bundle()
                    bundle.putSerializable(SessionListFragment.DAY_OF_SESSION, day)
                    bundle.putSerializable(
                        SessionListFragment.ARG_SESSION_LIST,
                        daySessionList as Serializable
                    )

                    // Create dialog, send arguments and show dialog
                    val sessionFragment = SessionListFragment(frag)
                    sessionFragment.arguments = bundle
                    sessionFragment.show(frag.requireActivity().supportFragmentManager, null)
                }
            }
        }
    }


    private fun localDateTimeToDate(localDateTime: LocalDateTime): Calendar {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar[localDateTime.year, localDateTime.monthValue - 1, localDateTime.dayOfMonth, localDateTime.hour, localDateTime.minute] =
            localDateTime.second
        return calendar
    }
}