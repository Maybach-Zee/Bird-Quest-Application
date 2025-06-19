package com.example.birdquestapplicationfinal.Model

import android.location.Location

data class UserObservation (val ObservationID: String,
                        val UserID: String,
                        val Date: String,
                        val BirdName: String,
                        val Amount: Int,
                        val Location: Location,
                        val Note: String,
                        val PlaceName: String,
                        var IsAtHotspot: Boolean)