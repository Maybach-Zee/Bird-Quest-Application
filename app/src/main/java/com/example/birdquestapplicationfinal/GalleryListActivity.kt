package com.example.birdquestapplicationfinal

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GalleryListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var itemArrayList: ArrayList<PicturesB>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_list)

        itemRecyclerView = findViewById(R.id.itemList)
        itemRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerView.setHasFixedSize(true)

        itemArrayList = arrayListOf()
        getItemData()

        val buttonAdd: Button = findViewById(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getItemData() {
        database = FirebaseDatabase.getInstance().getReference("Picture List")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemArrayList.clear()
                for (itemSnapshot in snapshot.children) {
                    val picturesB = itemSnapshot.getValue(PicturesB::class.java)
                    picturesB?.let {
                        it.id = itemSnapshot.key
                        itemArrayList.add(it)
                    }
                }
                val adapter = ItemAdapter(itemArrayList) { picturesB ->
                    showDeleteDialog(picturesB)
                }
                itemRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("GalleryListActivity", "Failed to read data", error.toException())
            }
        })
    }

    private fun showDeleteDialog(picturesB: PicturesB) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this item?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                deleteItem(picturesB)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }



    private fun deleteItem(picturesB: PicturesB) {
        picturesB.id?.let {
            database.child(it).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
        }
    }
}