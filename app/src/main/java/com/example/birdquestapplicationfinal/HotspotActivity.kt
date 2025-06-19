package com.example.birdquestapplicationfinal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.birdquestapplicationfinal.Model.HotspotModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.concurrent.thread

class HotspotActivity : AppCompatActivity(), OnMapReadyCallback, LocationDataCallback {

    // Map and location
    private lateinit var locationName: String
    private var isPermissionGranted = false
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: MapView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var lat = 0.0
    private var lon = 0.0
    private var destlat = 0.0
    private var destlon = 0.0

    // Nav buttons
    private lateinit var fabMenu: FloatingActionButton
    private lateinit var menuGame: FloatingActionButton
    private lateinit var menuSettings: FloatingActionButton
    private lateinit var menuAddObservation: FloatingActionButton
    private lateinit var menuMyObs: FloatingActionButton
    private lateinit var menuChallenges: FloatingActionButton
    private var mapStyleChosen = 0

    // Menu movement
    private lateinit var fabClose: Animation
    private lateinit var fabOpen: Animation
    private lateinit var fabClock: Animation
    private lateinit var fabAnticlock: Animation
    private var isOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotspot)


        // MAP code
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // NAV menu popup code
        fabClose = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
        fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fabClock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_clock)
        fabAnticlock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_anticlock)

        fabMenu = findViewById(R.id.fabMenu)
        menuGame = findViewById(R.id.menu_game)
        menuSettings = findViewById(R.id.menu_settings)
        menuAddObservation = findViewById(R.id.menu_addObservation)
        menuMyObs = findViewById(R.id.menu_viewObservation)
        menuChallenges = findViewById(R.id.menu_challenges)

        menuMyObs.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            intent.putExtra("desiredFragmentIndex", 1)
            startActivity(intent)
        }

        menuGame.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
            close()
        }
        menuAddObservation.setOnClickListener {
            val intent = Intent(this, AddObservationActivity::class.java)
            startActivity(intent)
            close()
        }
        menuSettings.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            intent.putExtra("desiredFragmentIndex", 0)
            startActivity(intent)
        }
        menuChallenges.setOnClickListener {
            close()
            loadChallengesFragment()
        }
        fabMenu.setOnClickListener {
            if (isOpen()) {
                close()
            } else {
                open()
            }
        }

        initializeMap()
    }

    // This method is called when getLocationData has completed.
    override fun onLocationDataReceived() {
        Log.d("Hotspot", "Location data received")
    }

    // Check for location perms, if granted get location and move map, if not ask
    private fun initializeMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    lat = it.latitude
                    lon = it.longitude
                    ToolBox.currentLat = lat
                    ToolBox.currentLng = lon
                    getNearByHotspots()
                    addUserObs()
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 15f))
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }


    // Loading user map style
    private fun loadMapStyle() {

        mapStyleChosen = if (ToolBox.users[0].mapStyleIsDark) {
            R.raw.dark
        } else {
            R.raw.light
        }

        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, mapStyleChosen
                )
            )

            if (!success) {
                println("Style parsing failed.")
            }
        } catch (e: IOException) {
            println("Could not load style. Error: ${e.message}")
        }
    }

    // When google maps is ready this code will execute
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Null check for mMap before performing any map operations
        mMap?.let { map ->
            try {
                loadMapStyle()
            } catch (e: IOException) {
                Log.e("MapStyle", "Map style loading failed", e)
            }

            lifecycleScope.launch {
                doWork()
            }

            // Get the user's current location with null checks
            getCurrentLocation { lat, lon ->
                this.lat = lat
                this.lon = lon
                ToolBox.currentLat = lat
                ToolBox.currentLng = lon

                val userLocation = LatLng(lat, lon)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
            }

            map.setOnMarkerClickListener { marker ->
                locationName = marker.title.toString()
                ToolBox.destlat = marker.position.latitude
                ToolBox.destlng = marker.position.longitude
                ToolBox.newObslat = marker.position.latitude
                ToolBox.newObslng = marker.position.longitude
                getLocationData(marker.position.latitude, marker.position.longitude, marker)
                true
            }
        }
    }



    // Method to retrieve data while bottom sheet is active, so that there is no delay
    private suspend fun doWork() = coroutineScope {
        launch {
            getNearByHotspots()
        }
        launch {
            addUserObs()
        }
    }

    // Show any user obs on the map in a different color
    private fun addUserObs() {
        mMap?.let { map ->
            if (ToolBox.usersObservations.isNotEmpty()) {
                val filteredObservations = ToolBox.usersObservations.filter { !it.IsAtHotspot }

                for (location in filteredObservations) {
                    map.addMarker(
                        MarkerOptions().position(
                            LatLng(location.Location.latitude, location.Location.longitude)
                        ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .title("User Sighting: " + location.BirdName)
                    )
                }
            }
        }
    }

    // Get all nearby hotspot to the user, based on their chosen distance
    private fun getNearByHotspots() {
        val apiWorker = APIWorker()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val hotspots = apiWorker.getHotspots(lat, lon)
                UpdateMarkers(hotspots)
                ToolBox.birdsInTheRegion = apiWorker.getBirds()
                ToolBox.populated = true
            } catch (e: IOException) {
                Log.e("HotspotActivity", "Error fetching hotspots", e)
            }
        }
    }

    // Puts markers on map
    private fun UpdateMarkers(locations: List<HotspotModel>) {
        mMap?.let { map ->
            try {
                runOnUiThread {
                    for (location in locations) {
                        map.addMarker(
                            MarkerOptions().position(LatLng(location.Lat, location.Lon))
                                .title(location.Name)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Method to handel the fusedLocationClient logic and if no location is found use a hard coded location
    private fun getCurrentLocation(callback: (Double, Double) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true
            mMap?.let { map ->
                map.isMyLocationEnabled = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val lat = location.latitude
                        val lon = location.longitude
                        callback(lat, lon)
                    } ?: callback(-33.9249, 18.4241)
                }
            }
        }
    }

    // Gets location data to be displayed in bottom sheet
    private fun getLocationData(lat: Double, lng: Double, marker: Marker) {
        val apiWorker = APIWorker()
        val scope = CoroutineScope(Dispatchers.Default)
        val bottomSheet = BottomSheetHotspot.newInstance(marker.title.toString(), lat, lng)
        bottomSheet.show(supportFragmentManager, BottomSheetHotspot.TAG)

        // Using threading to query external resources in the background
        thread {
            scope.launch {
                ToolBox.hotspotsSightings = apiWorker.getHotspotBirdData(lat, lng)

                destlat = lat
                destlon = lng

                // Updating the content of the bottom sheet with the received data
                runOnUiThread {
                    bottomSheet.updateHotspotSightings(ToolBox.hotspotsSightings)

                    // Bottom sheet click listener for navigation
                    bottomSheet.setButtonClickListener {
                        val intent = Intent(this@HotspotActivity, NavigationActivity::class.java)
                        intent.putExtra("LATITUDE", ToolBox.currentLat)
                        intent.putExtra("LONGITUDE", ToolBox.currentLng)
                        intent.putExtra("DEST_LAT", destlat)
                        intent.putExtra("DEST_LNG", destlon)

                        startActivity(intent)
                    }
                }
            }
        }
    }

    // Requests location permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true
                initializeMap()
            }
        }
    }

    // Open animation for the menu
    private fun open() {
        fabMenu.startAnimation(fabClock)
        fabMenu.isEnabled = true

        menuGame.startAnimation(fabOpen)
        menuGame.isEnabled = true

        menuSettings.startAnimation(fabOpen)
        menuSettings.isEnabled = true

        menuAddObservation.startAnimation(fabOpen)
        menuAddObservation.isEnabled = true

        menuMyObs.startAnimation(fabOpen)
        menuMyObs.isEnabled = true

        menuChallenges.startAnimation(fabOpen)
        menuChallenges.isEnabled = true

        isOpen = true

    }

    // Checks to see if user has open the menu
    private fun isOpen(): Boolean {
        if (isOpen) {
            menuGame.startAnimation(fabClose)
            menuSettings.startAnimation(fabClose)
            menuAddObservation.startAnimation(fabClose)
            menuMyObs.startAnimation(fabClose)
            menuChallenges.startAnimation(fabClose)
            fabMenu.startAnimation(fabAnticlock)
            return true

        } else {
            fabMenu.startAnimation(fabClock)
            menuGame.startAnimation(fabOpen)
            menuSettings.startAnimation(fabOpen)
            menuAddObservation.startAnimation(fabOpen)
            menuMyObs.startAnimation(fabOpen)
            menuChallenges.startAnimation(fabOpen)
            return false
        }
    }

    // Close animations for menu
    private fun close() {
        menuGame.startAnimation(fabClose)
        menuGame.isEnabled = false

        menuSettings.startAnimation(fabClose)
        menuSettings.isEnabled = false

        menuAddObservation.startAnimation(fabClose)
        menuAddObservation.isEnabled = false

        menuMyObs.startAnimation(fabClose)
        menuMyObs.isEnabled = false

        menuChallenges.startAnimation(fabClose)
        menuChallenges.isEnabled = false

        fabMenu.startAnimation(fabAnticlock)
        isOpen = false
    }

    // Showing/loading challenges fragment
    private fun loadChallengesFragment() {
        val challengesFragment = ChallengesActivity()
        fabMenu.isEnabled = false
        supportFragmentManager.beginTransaction().replace(android.R.id.content, challengesFragment)
            .addToBackStack(null).commit()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        fabMenu.isEnabled = true
    }
}

interface LocationDataCallback {
    fun onLocationDataReceived()
}