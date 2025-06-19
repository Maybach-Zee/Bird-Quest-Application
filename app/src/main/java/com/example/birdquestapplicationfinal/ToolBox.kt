package com.example.birdquestapplicationfinal

import android.app.Application
import android.location.Location
import android.util.Log
import com.example.birdquestapplicationfinal.Model.BirdModel
import com.example.birdquestapplicationfinal.Model.SightingModel
import com.example.birdquestapplicationfinal.Model.UserObservation
import com.example.birdquestapplicationfinal.Model.UsersModel
import com.google.firebase.firestore.FirebaseFirestore

class ToolBox : Application() {

    companion object {
        var userRegion = ""

        // Will only store one user at a time
        var users = arrayListOf<UsersModel>()

        // Stores all entries of users observations
        var usersObservations = arrayListOf<UserObservation>()

        // Used to store the sightings for a specific observation, changed for every hotpost pressed
        var hotspotsSightings: List<SightingModel> = mutableListOf()

        // Used to store the birds found in a region, session based
        var birdsInTheRegion: List<BirdModel> = mutableListOf()

        // Var for observers
        var populated = false

        // Var for hotspot location when adding new obs to it
        var newObsOnHotspot = false;

        var newObslat = 0.0
        var newObslng = 0.0
        var destlat = 0.0
        var destlng = 0.0
        var currentLat = 0.0
        var currentLng = 0.0

        //  Function fetches all user observations
        fun fetchUserObservations() {
            val db = FirebaseFirestore.getInstance()
            val userObservationsCollection = db.collection("observations")

            usersObservations.clear()

            userObservationsCollection.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val data = document.data

                    val locationData = data["location"] as? Map<String, Any>
                    val latitude = locationData?.get("latitude") as? Double ?: 0.0
                    val longitude = locationData?.get("longitude") as? Double ?: 0.0

                    val location = Location("fused")
                    location.latitude = latitude
                    location.longitude = longitude

                    val observation = UserObservation(
                        ObservationID = data["observationID"] as? String ?: "",
                        UserID = data["userID"] as? String ?: "",
                        Date = data["date"] as String ?: "",
                        BirdName = data["birdName"] as? String ?: "",
                        Amount = (data["amount"] as? Number ?: 0).toInt(),
                        Location = location,
                        Note = data["note"] as? String ?: "",
                        PlaceName = data["placeName"] as? String ?: "",
                        IsAtHotspot = data["isAtHotspot"] as? Boolean ?: false,
                    )

                    usersObservations.add(observation)
                }

            // Notify your UI or perform any other actions after fetching all observations
            }.addOnFailureListener { exception ->
                Log.e("AllObservations", "Error fetching all observations: $exception")
            }
        }
    }
}