package com.example.mymaptask


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mymaptask.databinding.ActivityMapsBinding
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.os.IResultReceiver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latitude = -34.0
    private var longtude = 151.0
    private var isOn: Boolean = false
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        val thread: Thread = object : Thread() {
//            override fun run() {
//                try {
//                    while (true) {
//                        sleep(5000)
//                        getCurrentLocation()
//                    }
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//        thread.start()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getCurrentLocation()
        CoroutineScope(IO).launch {
            startCoroutine()
        }
    }

    private suspend fun startCoroutine() {
        while (true) {
            delay(5000)
            println("coroutine is Running")
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                println("no error0")
                var locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?


                if (true) {
                    fusedLocationProviderClient.getCurrentLocation(100, null)
                        .addOnCompleteListener { task ->
                            val location: Location? = task.result
                            if (location != null) {
                                latitude = location.latitude
                                longtude = location.longitude
                                val currentPosition = LatLng(latitude, longtude)



                                if (!isOn) {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(currentPosition).title("Marker in currentPosition")
                                    )
                                    startService(Intent(this, service::class.java))


                                    Toast.makeText(
                                        applicationContext,
                                        "latitude: $latitude \n longtude: $longtude",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            currentPosition,
                                            18.5f
                                        )
                                    )
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "latitude: $latitude \n longtude: $longtude",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    marker.position = currentPosition
                                }
                                if (!isOn) isOn = true
                            }
                        }
                } else {
                    runOnUiThread {
                        var locationListener = object : LocationListener {

                            override fun onLocationChanged(location: Location) {
                                latitude = location.latitude
                                longtude = location.longitude
                                val newPosition = LatLng(latitude, longtude)
                                marker.position = newPosition
                                Toast.makeText(
                                    applicationContext,
                                    "latitude: $latitude \n longtude: $longtude",
                                    Toast.LENGTH_SHORT
                                ).show()
                                println("update")
                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {
                            }
                        }
                        locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            0L,
                            0f,
                            locationListener
                        )
                    }

                }
            } else {
            }

        } else {
            requestPermission()
        }
    }

    //
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ==
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled((LocationManager.GPS_PROVIDER)) || locationManager.isProviderEnabled(
            (LocationManager.NETWORK_PROVIDER)
        )
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ), 100
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()

            }
        }
    }
}