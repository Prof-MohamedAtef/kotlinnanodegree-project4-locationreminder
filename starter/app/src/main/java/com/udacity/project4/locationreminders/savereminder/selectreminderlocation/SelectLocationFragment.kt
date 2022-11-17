package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.BuildConfig.MAPS_API_KEY_PAID
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.*
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private var mMap: GoogleMap?=null

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        if (!anyPermissionsGranted(FOREGROUND_LOCATION_PERMISSIONS)) {
            requestMissingPermissions(FOREGROUND_LOCATION_PERMISSIONS)
        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Log.d("PAID Maps key is:","${MAPS_API_KEY_PAID}")


//        TODO: put a marker to location that the user selected

//        TODO: call this function after the user confirms on the selected location
        onLocationSelected()

        return binding.root
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkGrantedLocation(){
        if (anyPermissionsGranted(FOREGROUND_LOCATION_PERMISSIONS)){
            mMap?.isMyLocationEnabled=true

            val fusedLocationProvider=LocationServices.getFusedLocationProviderClient(requireActivity())

            val location=fusedLocationProvider.lastLocation

            location.addOnCompleteListener {
                if (it.isSuccessful) it.result?.let { location ->
                    mMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                location.latitude,
                                location.longitude
                            ),
                            16f
                        )
                    )
                }
            }
        }
    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.hybrid_map -> {
            mMap?.mapType=GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.normal_map -> {
            mMap?.mapType=GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.terrain_map -> {
            mMap?.mapType=GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        R.id.satellite_map -> {
            mMap?.mapType=GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(map: GoogleMap) {
        mMap=map
        mMap?.setMapStyle(context?.let { MapStyleOptions.loadRawResourceStyle(it, R.raw.gray_map_style) })

        // TODO is Done: zoom to the user location after taking his permission
        checkGrantedLocation()


    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode== REQUEST_PERMISSION_CODE){
            if (permissions.filter { FOREGROUND_LOCATION_PERMISSIONS.contains(it) }
                    .any{ grantResults[permissions.indexOf(it)]==PackageManager.PERMISSION_GRANTED }
            ){
                checkGrantedLocation()
            }else{
                // show permission denied message
            }
        }
    }
}