package com.example.birdquestapplicationfinal

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.birdquestapplicationfinal.Model.UsersModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignInActivity : Fragment() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var btnSignIn: Button
    private lateinit var pbWaitToSignIn: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(requireContext())
    }

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_sign_in_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseApp.initializeApp(requireContext())

        // Find the button by ID
        pbWaitToSignIn = view.findViewById(R.id.pbWaitToSignIn)
        btnSignIn = view.findViewById(R.id.btnSignIn)
        emailInput = view.findViewById(R.id.txtUserSignInEmail)
        passwordInput = view.findViewById(R.id.txtUserSignInPassword)

        // Set an OnClickListener to the button
        btnSignIn.setOnClickListener {

            if (validateForm()) {
                val email = emailInput.text.toString().trim()
                val pword = passwordInput.text.toString().trim()

                pbWaitToSignIn.visibility = View.VISIBLE

                authenticateUser(email, pword)
            }
        }
    }

    private fun authenticateUser(email: String, password: String) {
        FirebaseApp.initializeApp(requireContext())
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    val errToast = Toast.makeText(
                        requireContext(), "Signed In!", Toast.LENGTH_LONG
                    )

                    errToast.setGravity(Gravity.BOTTOM, 0, 25)
                    errToast.show()

                    if (uid != null) {
                        // Retrieve user data from FireStore
                        val usersCollection = db.collection("users")

                        usersCollection.document(uid)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    // Document exists, so you can access its data
                                    val userData = documentSnapshot.toObject(UsersModel::class.java)

                                    if (userData != null) {

                                        // Authentication successful
                                        ToolBox.users.clear()
                                        ToolBox.users.add(userData)

                                        ChallengeModel.getChallenges()

                                        ToolBox.fetchUserObservations()

                                        val intent = Intent(activity, HotspotActivity::class.java)
                                        startActivity(intent)

                                    } else {
                                        // Handle the case where the data couldn't be converted to UsersModel
                                    }
                                } else {
                                    // Handle the case where the document doesn't exist (user data not found)
                                }
                            }
                            .addOnFailureListener { e ->

                                Toast.makeText(
                                    requireContext(),
                                    "Failed to retrieve user data from Firestore: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {

                    }
                } else {
                    val errToast = Toast.makeText(
                        requireContext(), "Incorrect email or password", Toast.LENGTH_LONG
                    )

                    pbWaitToSignIn.visibility = View.GONE
                    errToast.setGravity(Gravity.BOTTOM, 0, 25)
                    errToast.show()
                }
            }
        pbWaitToSignIn.visibility = View.GONE
    }
    private fun validateForm(): Boolean {
        var valid = true
        try {
            // A custom error message to prevent it from overlapping with the view password eye icon
            val customError = ErrorDrawable(requireContext())

            val email: String = emailInput.text.toString().trim()
            val password: String = passwordInput.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Email is required", customError)
                valid = false
            }
            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Password is required", customError)
                valid = false
            }

            return valid
        } catch (ex: Exception) {
            println(ex.toString())
            ex.printStackTrace()
            return false
        }
    }
}