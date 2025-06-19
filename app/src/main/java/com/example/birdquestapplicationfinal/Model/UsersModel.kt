package com.example.birdquestapplicationfinal.Model

data class UsersModel (var UserID: String = "",
                    var Name: String = "",
                    var Surname: String = "",
                    var isUnitKM: Boolean = true,
                    var MaxDistance: Double = 5.0,
                    var ChallengePoints: Int = 0,
                    var mapStyleIsDark: Boolean = false)