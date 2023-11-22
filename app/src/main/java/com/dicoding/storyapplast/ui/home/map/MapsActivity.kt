package com.dicoding.storyapplast.ui.home.map

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.dicoding.storyapplast.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.dicoding.storyapplast.databinding.ActivityMapsBinding
import com.dicoding.storyapplast.ui.ViewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val TAG = "MapsActivity"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setmMapStyle()
        addMarker()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setmMapStyle() {
        var _isChecked = true
        binding.btnmapstyle.setOnClickListener {
            _isChecked = !_isChecked
            if (_isChecked) {
                try {
                    val success =
                        mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                this,
                                R.raw.map_style
                            )
                        )
                    if (!success) {
                        Log.e(TAG, "Light style parsing failed.")
                    }
                } catch (exception: Resources.NotFoundException) {
                    Log.e(TAG, "Can't find style. Error: ", exception)
                }
            } else {
                try {
                    val success =
                        mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                this,
                                R.raw.map_syledark
                            )
                        )
                    if (!success) {
                        Log.e(TAG, "Dark style parsing failed.")
                    }
                } catch (exception: Resources.NotFoundException) {
                    Log.e(TAG, "Can't find style. Error: ", exception)
                }
            }
        }
    }

    private fun addMarker() {
        mapsViewModel.getToken().observe(this) {
            val getToken = it.token
            mapsViewModel.getStoriesLocation(getToken)
        }
        mapsViewModel.stories.observe(this) {
            it.forEach {
                val latLng = LatLng(it.lat!!, it.lon!!)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(it.name)
                        .snippet(it.description)
                )
            }
        }
    }
}