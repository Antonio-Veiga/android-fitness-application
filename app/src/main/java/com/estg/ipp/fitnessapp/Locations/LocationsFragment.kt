package com.estg.ipp.fitnessapp.Locations

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Locations.GeopifyAPI.DataClasses.Data
import com.estg.ipp.fitnessapp.Locations.GeopifyAPI.GeopifyAPI
import com.estg.ipp.fitnessapp.Locations.RecyclerView.Adapter
import com.estg.ipp.fitnessapp.databinding.FragmentLocationsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationsFragment : Fragment(){
    private lateinit var binding : FragmentLocationsBinding
    private lateinit var area: String
    private lateinit var dataViewModel: DataViewModel
    private var typeValues = ""
    private var location = ""
    private val args: LocationsFragmentArgs by navArgs()
    private lateinit var animation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataViewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationsBinding.inflate(inflater,container,false)
        val root = binding.root
        val recycleViewer: RecyclerView = binding.rv
        val adapter = Adapter(requireContext())

        location = CheckedStates.initial_position
        prepareData(args.area, args.types.checked_values)

        animation = binding.androidFlexing.drawable as AnimationDrawable

        if(dataViewModel.data.value!!.features.isEmpty()) {
            binding.loadingScreen.visibility = View.VISIBLE
            animation.start()
        }

        val geoAPI = GeopifyAPI.create().getPlaces(typeValues.lowercase(),
            "circle:$location,"+area+"000", "proximity:$location",
            "20")

        geoAPI.enqueue(object : Callback<Data> {
            override fun onResponse(
                call: Call<Data>,
                response: Response<Data>?,
            ) {
                if (response!!.body() != null) {
                    animation.stop()
                    binding.androidFlexing.isVisible = false
                    binding.loadingScreen.visibility = View.INVISIBLE
                    dataViewModel.data.value = response.body()!!
                }
            }
            override fun onFailure(call: Call<Data>?, t: Throwable?) {
                Log.d("Error:",t.toString())
                Toast.makeText(context, "Error while getting the '~fitness~' places", Toast.LENGTH_SHORT).show()
            }
        })

        val dataObserver = Observer<Data> { data ->
            adapter.data = data.features
            recycleViewer.adapter = adapter
        }

        dataViewModel.data.observe(viewLifecycleOwner, dataObserver)

        return root
    }


    private fun prepareData(x: String, y: ArrayList<String> ){
        val it = y.iterator()

        while(it.hasNext()){
            var tmp:String = it.next()

            tmp = tmp.replace(" ","")

            //Activity / Sport Club and National Park --> Activity/SportClub and NationalPark

            if(tmp.contains("SportClub",true)) {

                tmp = tmp.replace("SportClub","sport_club")

                Log.d("1",tmp)

            }else if(tmp.contains("NationalPark",true)){

                tmp = tmp.replace("NationalPark","national_park")

                Log.d("2",tmp)
            }

            typeValues += tmp.replace("/", ".") + ","
        }

        area = x.replace(" ", "")

        typeValues = typeValues.dropLast(1)
        area = area.replace("KM","")
    }
}