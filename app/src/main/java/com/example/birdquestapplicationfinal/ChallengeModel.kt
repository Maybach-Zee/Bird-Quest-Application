package com.example.birdquestapplicationfinal

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class ChallengeModel {
    companion object {
        var newTripsCompleted = 0
        var tripsCompleted = 0

        var tripsCompletedBool = false
        var userObservationsBool = false

        // Gets all user challenges and their progress
        fun getChallenges() {
            try {
                val db = FirebaseFirestore.getInstance()

                val currentUserUID =
                    ToolBox.users[0].UserID

                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val currentDate = dateFormat.format(Date())

                val usersCollection = db.collection("users")

                val userProgressCollection =
                    usersCollection.document(currentUserUID).collection("progress")

                val query = userProgressCollection.whereEqualTo("date", currentDate)

                // Execute the query
                query.get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot) {
                            val challengeData = document.data
                            val newTripsCompleted = challengeData["newTripsCompleted"] as Number
                            tripsCompleted = newTripsCompleted.toInt()
                            val pointsTrips = challengeData["pointsTrips"] as Boolean
                            tripsCompletedBool = pointsTrips
                            val obsBool = challengeData["obsBool"] as Boolean
                            userObservationsBool = obsBool
                        }
                    }
                    .addOnFailureListener { e ->
                        println("$e")
                    }
            } catch (e: java.lang.Exception) {
                println("$e")
            }
        }

        // Saves challenges and updates user progress
        fun saveChallenge() {
            if (newTripsCompleted > tripsCompleted) {
                tripsCompleted = newTripsCompleted
            }


            val userId = ToolBox.users[0].UserID

            // Initialize Firebase Firestore
            val db = FirebaseFirestore.getInstance()

            // Get the current date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = dateFormat.format(Date())

            // Reference to the "users" collection
            val usersCollection = db.collection("users")

            // Reference to the specific user's "progress" collection for the current date
            val userProgressCollection =
                usersCollection.document(userId).collection("progress")
                    .document(currentDate)

            // Create a data object for the user's progress for the current day
            val progressData = hashMapOf(
                "date" to currentDate,
                "newTripsCompleted" to tripsCompleted,
                "pointsTrips" to tripsCompletedBool,
                "obsBool" to userObservationsBool,
            )

            // Add the progress data to the user's "progress" collection for the current day
            userProgressCollection.set(progressData)
                .addOnSuccessListener { documentReference ->
                    // Progress data has been successfully added
                }
                .addOnFailureListener { e ->
                    var a = e.message
                }
        }

        fun updatePoints(points: Int) {
            try {
                val db = FirebaseFirestore.getInstance()
                val usersCollection = db.collection("users")

                val fbUser = Firebase.auth.currentUser

                if (fbUser != null) {
                    usersCollection.document(fbUser.uid)
                        .update(
                            "challengePoints", points
                        )
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener { e ->
                            var a = e;
                        }
                }
            } catch (ex: Exception) {
                Log.e("log", "Error updating user settings in Firestore: ${ex.message}")
                ex.printStackTrace()
            }
        }
    }
}