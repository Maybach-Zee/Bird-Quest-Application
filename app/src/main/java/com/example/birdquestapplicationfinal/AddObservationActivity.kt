package com.example.birdquestapplicationfinal

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.birdquestapplicationfinal.Model.BirdModel
import com.example.birdquestapplicationfinal.Model.UserObservation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddObservationActivity : AppCompatActivity() {

    private lateinit var geocoder: Geocoder
    private lateinit var userLocation: Location
    private lateinit var etSelectSpecies: EditText
    private lateinit var etWhen: EditText
    private lateinit var etNote: EditText
    private lateinit var btnSave: Button
    private lateinit var etHowMany: EditText
    private lateinit var cancelTextView: TextView
    private lateinit var pbWaitToSignIn: ProgressBar
    private val handler = Handler(Looper.getMainLooper())

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_observation)

        // Send user back
        cancelTextView = findViewById(R.id.btnCancel)
        cancelTextView.setOnClickListener {
            val intent = Intent(this, HotspotActivity::class.java)
            startActivity(intent)
        }

        requestLocation()
        geocoder = Geocoder(this, Locale.getDefault())

        etSelectSpecies = findViewById(R.id.etSelectSpecies)
        etSelectSpecies.setOnClickListener {
            showSpeciesDialog(ToolBox.birdsInTheRegion)
        }

        etWhen = findViewById(R.id.etWhen)
        etWhen.setOnClickListener {
            showCalendarDialog()
        }

        btnSave = findViewById(R.id.btnSaveObservation)
        btnSave.setOnClickListener {
            addNewObs()
        }

        etHowMany = findViewById(R.id.etHowMany)
        etNote = findViewById(R.id.etNote)
        pbWaitToSignIn = findViewById(R.id.pbWaitForData)

        // Start the handler
        if(ToolBox.populated)
        {
            btnSave.isEnabled = true
            etNote.isEnabled = true
            etHowMany.isEnabled = true
            etWhen.isEnabled = true
            etSelectSpecies.isEnabled = true
            println("data exists")
        }
        else
        {
            btnSave.isEnabled = false
            etNote.isEnabled = false
            etHowMany.isEnabled = false
            etWhen.isEnabled = false
            etSelectSpecies.isEnabled = false
            handler.post(checkPopulatedRunnable)
            println("Handler started")
        }

    }

    // Check for the populated value when the activity is created.
    private val checkPopulatedRunnable = object : Runnable {
        override fun run() {
            if (ToolBox.populated) {
                btnSave.isEnabled = true
                etNote.isEnabled = true
                etHowMany.isEnabled = true
                etWhen.isEnabled = true
                etSelectSpecies.isEnabled = true
                pbWaitToSignIn.visibility = View.GONE
                Log.d("checkPopulatedRunnable", "Progress bar set to GONE")

            } else {
                btnSave.isEnabled = false
                etNote.isEnabled = false
                etHowMany.isEnabled = false
                etWhen.isEnabled = false
                etSelectSpecies.isEnabled = false
                pbWaitToSignIn.visibility = View.VISIBLE
                handler.postDelayed(this, 100)
            }
        }
    }

    private fun addNewObs() {
        Log.d("addNewObs", "Function called")

        try {
            if (validateForm()) {
                Log.d("addNewObs", "Form validated successfully")

                val dateFormat = SimpleDateFormat("dd-MM-yyyy")
                val dateInput = etWhen.text.toString().trim()
                val birdName = etSelectSpecies.text.toString().trim()
                val howMany = etHowMany.text.toString().trim().toInt()
                val note = etNote.text.toString().trim()
                var location = userLocation

                // Setting date
                val date = dateFormat.parse(dateInput)


                if (ToolBox.newObsOnHotspot)
                {
                    location = (Location(LocationManager.GPS_PROVIDER).apply {
                        latitude = ToolBox.newObslat
                        longitude = ToolBox.newObslng
                    })
                }

                if (date != null) {
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(date)
                    val observation = UserObservation(
                        "",
                        ToolBox.users[0].UserID,
                        formattedDate,
                        birdName,
                        howMany,
                        location,
                        note,
                        "",
                        ToolBox.newObsOnHotspot
                    )

                    // Defaulting to false
                    ToolBox.newObsOnHotspot = false
                    val db = FirebaseFirestore.getInstance()
                    val observationsCollection = db.collection("observations")

                    observationsCollection
                        .add(observation)
                        .addOnSuccessListener { _ ->
                            // Observation added to FireStore successfully
                            Toast.makeText(this, "Bird observation saved!", Toast.LENGTH_LONG).show()

                            ToolBox.usersObservations.add(observation)

                            val intent = Intent(this, HotspotActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            // Handling the error if adding the observation to FireStore fails
                            Toast.makeText(this, "Failed to save bird observation: ${e.message}", Toast.LENGTH_SHORT).show()
                            print(e.message)
                        }
                }

                Log.d("addNewObs", "Observation created successfully")
            }
        } catch (ex: Exception) {
            Log.e("addNewObs", "Error: ${ex.message}", ex)
        }
    }

    // Validate the form inputs
    private fun validateForm(): Boolean {
        var valid = true
        val birdName = etSelectSpecies.text.toString().trim()
        val date = etWhen.text.toString().trim()
        val amount = etHowMany.text.toString().trim()

        if (TextUtils.isEmpty(birdName)) {
            etSelectSpecies.error = "Name is required"
            valid = false
        }

        if (TextUtils.isEmpty(date)) {
            etWhen.error = "Date is required"
            valid = false
        }

        if (TextUtils.isEmpty(amount)) {
            etHowMany.error = "Amount is required"
            valid = false
        }

        Log.d("validateForm", "Validation result: $valid")
        return valid
    }

    //  Request users' current location
    private fun requestLocation() {
        Log.d("Location", "requestLocation called")
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                userLocation = location
            }
        }
    }

    // Shows searchable list of all regional bird species
    private fun showSpeciesDialog(speciesList: List<BirdModel>) {
        val dialogView = layoutInflater.inflate(R.layout.species_dialog, null)
        val etSearch = dialogView.findViewById<EditText>(R.id.etSearch)
        val listView = dialogView.findViewById<ListView>(android.R.id.list)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView).setTitle("Select a species")
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        val comName: MutableList<String> = mutableListOf()

        for (bird in speciesList) {
            comName.add(bird.commonName)
        }

        // Create an adapter for the list view using the provided species list
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, comName)
        listView.adapter = adapter

        etSearch.requestFocus()

        // Handle search functionality
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("AfterTextChanged", "Text: $s")
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle item click
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedSpecies = adapter.getItem(position)
            etSelectSpecies.setText(selectedSpecies)
            dialog.dismiss()
        }

        dialog.show()
    }

   //  Calender Dialog
    private fun showCalendarDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                etWhen.setText(formattedDate)
            }, year, month, day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() + 1000
        datePickerDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkPopulatedRunnable)
    }
}