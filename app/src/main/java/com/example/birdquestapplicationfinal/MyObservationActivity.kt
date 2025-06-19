package com.example.birdquestapplicationfinal

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible

class MyObservationActivity : Fragment() {
    private lateinit var llObservationContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_observation_activity, container, false)

        // Initialize observation container
        llObservationContainer = view.findViewById(R.id.myObservationContainer)

        // Populate user observations dynamically
        populateObservationViews()
        return view
    }

    // Function to populate observation views dynamically
    private fun populateObservationViews() {
        llObservationContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        // Filtering observations for the current user
        val filteredObservations = ToolBox.usersObservations.filter {
            it.UserID == ToolBox.users[0].UserID
        }

        for (userObservation in filteredObservations) {
            val observationView = inflater.inflate(R.layout.my_observations_display_layout, null)

            // Displaying Bird Name and Amount
            observationView.findViewById<TextView>(R.id.tvBirdName).text =
                getString(R.string.bird_name, userObservation.BirdName, userObservation.Amount)

            // Displaying Location with Latitude and Longitude
            val latitude = userObservation.Location?.latitude ?: 0.0
            val longitude = userObservation.Location?.longitude ?: 0.0
            val placeName = if (userObservation.PlaceName.isNotEmpty())
                userObservation.PlaceName
            else getString(R.string.unknown_location)
            observationView.findViewById<TextView>(R.id.tvLocation).text =
                getString(R.string.location, placeName, latitude, longitude)

            // Displaying Observation Date
            observationView.findViewById<TextView>(R.id.tvDateSpotted).text =
                userObservation.Date.toString()

            // Displaying Note if present
            if (userObservation.Note.isNotEmpty()) {
                observationView.findViewById<TextView>(R.id.tvViewObsNote).text = userObservation.Note
            } else {
                observationView.findViewById<TextView>(R.id.tvViewObsNote).isVisible = false
            }

            // Set OnClickListener to navigate to NavigationActivity with coordinates
            observationView.setOnClickListener {
                val intent = Intent(requireContext(), NavigationActivity::class.java).apply {
                    putExtra("LATITUDE", ToolBox.currentLat)
                    putExtra("LONGITUDE", ToolBox.currentLng)
                    putExtra("DEST_LAT", latitude)
                    putExtra("DEST_LNG", longitude)
                }
                startActivity(intent)
            }

            // Add observation view to container
            llObservationContainer.addView(observationView)
        }
    }
}
