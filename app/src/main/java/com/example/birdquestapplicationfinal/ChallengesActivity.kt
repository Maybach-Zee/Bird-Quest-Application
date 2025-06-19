package com.example.birdquestapplicationfinal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.birdquestapplicationfinal.Model.ChallengeObject
import java.text.SimpleDateFormat
import java.util.Date

class ChallengesActivity : Fragment() {
    private var challengeList: List<ChallengeObject> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_challenges_activity, container, false)

        // Populate challenges (Assuming you have this list populated somehow)
        challengeList = checkProgress()

        val linearLayout =
            view.findViewById<LinearLayout>(R.id.fragment_container)

        val tvPoints = view.findViewById<TextView>(R.id.tvPoints)

        // Loop through the challenges and dynamically add them to the container
        for ((i, challenge) in challengeList.withIndex()) {

            // Instantiating challenge layout
            val challengeItemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.challenge_item_layout, null) // Change to the correct layout

            // Binding points to get view
            val pointSToGet = challengeItemView.findViewById<TextView>(R.id.tvPointsToGet)
            val tvChallengeDescription = challengeItemView.findViewById<TextView>(R.id.tvChallengeDescription)
            val progressBar = challengeItemView.findViewById<ProgressBar>(R.id.progressBar)
            val tvProgress = challengeItemView.findViewById<TextView>(R.id.tvProgress)
            val backTextView: TextView = view.findViewById(R.id.tvBack)
            backTextView.setOnClickListener {
                activity?.onBackPressed()
            }

            // Setting values
            pointSToGet.text = "+${challenge.pointsToGet} points"
            tvChallengeDescription.text = challenge.description
            progressBar.max = challenge.required
            progressBar.progress = challenge.progress

            if (challenge.progress > challenge.required) {
                tvProgress.text = "${challenge.required}/${challenge.required}"
            } else {
                tvProgress.text = "${challenge.progress}/${challenge.required}"
            }

            // Set top margin
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = 64
            challengeItemView.layoutParams = params

            // Adding the challenge item to the container
            linearLayout.addView(challengeItemView)
        }

        // Setting total points earned
        tvPoints.text = ToolBox.users[0].ChallengePoints.toString()
        return view
    }

    // Create the challenges and check the users progress
    private fun checkProgress(): List<ChallengeObject> {
        val challenges = mutableListOf<ChallengeObject>()
        var pointsToGet = 0

        // Spot 3 birds
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = Date()

        val filteredObservations = ToolBox.usersObservations.filter {
            it.UserID == ToolBox.users[0].UserID && (it.Date) == sdf.format(currentDate)
        }

        val uniqueBirdNames = filteredObservations.distinctBy { it.BirdName }
        val uniqueBirdCount = uniqueBirdNames.size

        // Travel to two hotspots
        if (!ChallengeModel.userObservationsBool && uniqueBirdCount >= 3) {
            pointsToGet = 15
            ChallengeModel.userObservationsBool = true
        }

        challenges.add(ChallengeObject("Spot three bird species", uniqueBirdCount, 3, 15, pointsToGet))
        pointsToGet = 0

        // Travel to two hotspots
        if (!ChallengeModel.tripsCompletedBool && ChallengeModel.tripsCompleted >= 2) {
            pointsToGet = 20
            ChallengeModel.tripsCompletedBool = true
        }

        challenges.add(
            ChallengeObject(
                "Travel to two hotspots",
                ChallengeModel.tripsCompleted,
                2,
                20,
                pointsToGet
            )
        )
        pointsToGet = 0


        var pointsAwarded = ToolBox.users[0].ChallengePoints
        for (challenge in challenges) {
            pointsAwarded += challenge.pointsAwarded
        }

        if (pointsAwarded != ToolBox.users[0].ChallengePoints) {
            ChallengeModel.updatePoints(pointsAwarded)
        }

        ToolBox.users[0].ChallengePoints = pointsAwarded
        ChallengeModel.saveChallenge()

        return challenges
    }
}