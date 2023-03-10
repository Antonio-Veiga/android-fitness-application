package com.estg.ipp.fitnessapp.Locations

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.estg.ipp.fitnessapp.Locations.GeopifyAPI.DataClasses.Data
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.FragmentFindLocationsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener


class FindLocationsFragment : Fragment(), OnMapReadyCallback{
    private lateinit var binding: FragmentFindLocationsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var expList: ExpandableListView
    private lateinit var listAdapter1: ExpandableListAdapter
    private lateinit var groupTitles: ArrayList<String>
    private lateinit var childValues: HashMap<String,ArrayList<String>>
    private lateinit var dataViewModel: DataViewModel
    private lateinit var mView: MapView
    private lateinit var adapter: ArrayAdapter<String>
    private val MAPVIEW_BUNDLE_KEY = "Find Locations Map"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataViewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        /*
        Check for fragment acess
         */

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return inflater.inflate(R.layout.no_permissions_given, container, false)
        }else if(!(isNetworkAvailable(requireContext()))) {
            return inflater.inflate(R.layout.no_internet, container, false)
        }

        binding = FragmentFindLocationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mView = binding.mapView

        val items = listOf("1 KM", "5 KM", "10 KM", "20 KM")

        adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)

        binding.options.setAdapter(adapter)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        expList = binding.listView

        fillData()

        listAdapter1 = LocationExpandableListAdapter(requireContext(),groupTitles,childValues)

        expList.setAdapter(listAdapter1)

        expList.setOnGroupClickListener { parent, _, groupPosition, _ ->
            setListViewHeight(parent, groupPosition)
            false
        }

        binding.search.setOnClickListener{
            if(CheckedStates.checked_values.isNotEmpty()){
                if(binding.options.text.isNotBlank()){
                    val navController = Navigation.findNavController(it)
                    navController.navigate(FindLocationsFragmentDirections.actionNavLocationsToShowLocations(binding.options.text.toString(),CheckedStates))

                }else{
                    Toast.makeText(requireActivity(),"Escolha um raio de procura",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireActivity(),"Escolha pelo menos um tipo de localização ",Toast.LENGTH_SHORT).show()
                binding.listView.expandGroup(0)
            }
        }

        initGoogleMap(savedInstanceState)
        return root
    }

    private fun fillData() {
        val parentValue = "Tipos de localizações a encontrar"
        groupTitles = ArrayList()
        groupTitles.add(parentValue)

        val optionsList = ArrayList<String>()
        childValues = HashMap()

        optionsList.add("Building / Sport")
        optionsList.add("Sport / Track")
        optionsList.add("Sport / Fitness")
        optionsList.add("Activity / Sport Club")
        optionsList.add("National Park")

        childValues[parentValue] = optionsList
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mView.onSaveInstanceState(mapViewBundle)
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mView.onCreate(mapViewBundle)
        mView.getMapAsync(this)
    }

    @Override
    override fun onResume() {
        super.onResume()
        if(binding != null) {
            dataViewModel.data.value = Data()
            CheckedStates.initial_position = ""
            CheckedStates.checked_values = ArrayList()
            binding.options.setText("")
            mView.onResume()
        }
    }

    @Override
    override fun onStart() {
        super.onStart()
        if(binding != null) {
            mView.onStart()
        }
    }

    @Override
    override fun onStop() {
        super.onStop()
        if(binding != null) {
            mView.onStop()
        }
    }

    override fun onPause() {
        super.onPause()
        if(binding != null) {
            mView.onPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(binding != null) {
            mView.onDestroy()
        }
    }


    override fun onLowMemory() {
        super.onLowMemory()
        if(binding != null) {
            mView.onLowMemory()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, object: CancellationToken(){
                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        return this
                    }
                    override fun isCancellationRequested(): Boolean {
                        return false
                    }
                }).addOnSuccessListener { location: Location? ->
                    CheckedStates.initial_position = location!!.longitude.toString() + "," + location.latitude.toString()

                    binding.latValue.text = location.latitude.toString()
                    binding.logValue.text = location.longitude.toString()

                    val currLocation = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(currLocation)
                        .title("A tua localização atual"))
                    map.moveCamera(CameraUpdateFactory.newLatLng(currLocation))
                }
        }

        map.isMyLocationEnabled = true
    }

    private fun setListViewHeight(
        listView: ExpandableListView,
        group: Int,
    ) {
        val listAdapter = listView.expandableListAdapter as ExpandableListAdapter
        var totalHeight = 0
        val desiredWidth: Int = View.MeasureSpec.makeMeasureSpec(
            listView.width,
            View.MeasureSpec.EXACTLY
        )
        for (i in 0 until listAdapter.groupCount) {
            val groupItem: View = listAdapter.getGroupView(i, false, null, listView)
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += groupItem.measuredHeight
            if (listView.isGroupExpanded(i) && i != group
                || !listView.isGroupExpanded(i) && i == group
            ) {
                for (j in 0 until listAdapter.getChildrenCount(i)) {
                    val listItem: View = listAdapter.getChildView(
                        i, j, false, null,
                        listView
                    )
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                    totalHeight += listItem.measuredHeight
                }
            }
        }
        val params = listView.layoutParams
        var height = (totalHeight
                + listView.dividerHeight * (listAdapter.groupCount - 1))
        if (height < 10) height = 200
        params.height = height
        listView.layoutParams = params
        listView.requestLayout()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}
