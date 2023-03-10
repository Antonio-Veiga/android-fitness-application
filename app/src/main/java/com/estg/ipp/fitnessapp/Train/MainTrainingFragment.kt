package com.estg.ipp.fitnessapp.train

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Database.Queries.SessionWithTrain
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.train.adapters.SliderAdapter
import com.estg.ipp.fitnessapp.train.calendar.MyDayBinder
import com.estg.ipp.fitnessapp.train.calendar.MyMonthBinder
import com.estg.ipp.fitnessapp.train.view_models.SessionViewModel
import com.kizitonwose.calendarview.CalendarView
import com.smarteist.autoimageslider.SliderView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@RequiresApi(Build.VERSION_CODES.O)
class MainTrainingFragment : Fragment(), SessionListInterface {

    private val sessionModel: SessionViewModel by activityViewModels()
    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_training, container, false)

        val listButton = view.findViewById<Button>(R.id.train_list_button)

        calendarView = view.findViewById(R.id.simpleCalendarView)
        val sessionsList = getSessionsAndTrainingFromDB(requireContext())

        calendarView.dayBinder = MyDayBinder(this, requireContext(), sessionsList)
        calendarView.monthHeaderBinder = MyMonthBinder(requireContext())

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(0)
        val lastMonth = currentMonth.plusMonths(20)
        val daysOfWeek = arrayOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
        calendarView.setup(firstMonth, lastMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        // Open trainings' list
        listButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_training_to_nav_train_list)
        }

        sessionModel.sessionLiveData.observe(
            viewLifecycleOwner,
            {
                calendarView.dayBinder = MyDayBinder(
                    this,
                    requireContext(),
                    getSessionsAndTrainingFromDB(requireContext())
                )
            })

        // Load images to slider
        loadImages(view)
        return view
    }

    /**
     * Method responsible for getting all training sessions after today
     */
    private fun getSessionsAndTrainingFromDB(
        context: Context
    ): MutableList<SessionWithTrain> {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)
        var listOfSessions = mutableListOf<SessionWithTrain>()
        val dateNow = LocalDate.now().atStartOfDay()

        executors.execute {
            listOfSessions =
                dao.getAllSessionsWithTrainingAfterADay(dateNow)
        }

        executors.shutdown()

        try {
            val dialog = ProgressDialog.show(
                context, "",
                context.getString(R.string.database_warning), true
            )
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            dialog.dismiss()
            return listOfSessions
        } catch (e: InterruptedException) {
            throw e
        }
    }

    /**
     * Method responsible for inputting images and configuring the
     * slider at the top of the fragment.
     */
    private fun loadImages(view: View) {
        val sliderDataArrayList: ArrayList<Bitmap> = ArrayList()

        val sliderView: SliderView = view.findViewById(R.id.slider)

        val icon1 = BitmapFactory.decodeResource(view.context.resources, R.drawable.train_1)
        val icon2 = BitmapFactory.decodeResource(view.context.resources, R.drawable.train_2)
        val icon3 = BitmapFactory.decodeResource(view.context.resources, R.drawable.train_3)
        val icon4 = BitmapFactory.decodeResource(view.context.resources, R.drawable.train_4)

        sliderDataArrayList.add(icon1)
        sliderDataArrayList.add(icon2)
        sliderDataArrayList.add(icon3)
        sliderDataArrayList.add(icon4)

        val adapter = SliderAdapter(sliderDataArrayList)
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        sliderView.setSliderAdapter(adapter)
        sliderView.setSliderAnimationDuration(600)
        sliderView.scrollTimeInSec = 5
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
    }

    override fun updateEvents() {
        calendarView.dayBinder = MyDayBinder(
            this,
            requireContext(),
            getSessionsAndTrainingFromDB(requireContext())
        )
    }
}

interface SessionListInterface {
    fun updateEvents()
}