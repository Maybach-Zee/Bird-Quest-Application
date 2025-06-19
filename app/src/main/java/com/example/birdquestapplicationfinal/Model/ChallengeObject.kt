package com.example.birdquestapplicationfinal.Model

data class ChallengeObject (val description: String,
                       var progress: Int,
                       val required: Int,
                       val pointsToGet: Int,
                       val pointsAwarded: Int)
