package com.example.birdquestapplicationfinal

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.birdquestapplicationfinal.databinding.ActivityGalleryBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class GalleryActivity : AppCompatActivity() {

    private var selectedImage: String? = ""
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityGalleryBinding
    private lateinit var back: TextView

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button
        back = findViewById(R.id.tvBackSettings)
        back.setOnClickListener {
            val intent = Intent(this, HotspotActivity::class.java)
            startActivity(intent)
        }

        // Set up buttons
        binding.buttonList.setOnClickListener {
            startActivity(Intent(this, GalleryListActivity::class.java))
        }

        binding.buttonCamera.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            captureImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureImage()
        } else {
            Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show()
        }
    }

    fun save_data(view: View) {
        val name = binding.editTextName.text.toString()
        val year = binding.editTextYear.text.toString()
        val price = binding.editTextPrice.text.toString()

        if (name.isEmpty() || year.isEmpty() || price.isEmpty() || selectedImage.isNullOrEmpty()) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        database = FirebaseDatabase.getInstance().getReference("Picture List")
        val picture = PicturesB(name, year, price, selectedImage)
        val id = database.push().key

        id?.let {
            database.child(it).setValue(picture).addOnSuccessListener {
                binding.editTextName.text.clear()
                binding.editTextYear.text.clear()
                binding.editTextPrice.text.clear()
                selectedImage = ""

                Toast.makeText(this, "Hooray! Picture Data Stored", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, GalleryListActivity::class.java))
                
            }.addOnFailureListener {
                Toast.makeText(this, "Picture Data Not Stored!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun insert_image(view: View) {
        val myFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        myFileIntent.type = "image/*"
        galleryResultLauncher.launch(myFileIntent)
    }

    private val galleryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                val inputStream = contentResolver.openInputStream(uri)
                val myBitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                val bytes = stream.toByteArray()
                selectedImage = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
                binding.imageViewUpload.setImageBitmap(myBitmap)
                inputStream?.close()
                Toast.makeText(this, "Image Selected!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun captureImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        CameraResultLauncher.launch(cameraIntent)
    }

    private val CameraResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                val stream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                val bytes = stream.toByteArray()
                selectedImage = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
                binding.imageViewUpload.setImageBitmap(imageBitmap)
                Toast.makeText(this, "Image Captured!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
