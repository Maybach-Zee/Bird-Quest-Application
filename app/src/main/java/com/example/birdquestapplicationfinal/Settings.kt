package com.example.birdquestapplicationfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class Settings : AppCompatActivity() {

    private lateinit var logout: TextView
    private lateinit var back: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager = findViewById(R.id.viewPager)
        logout = findViewById(R.id.tvLogout)
        back = findViewById(R.id.tvBackSettings)

        val pagerAdapter = PagerAdapterSettings(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        // Logging user out
        logout.setOnClickListener() {
            logout()
        }

        // Sending user back to hotspots
        back.setOnClickListener() {
            val intent = Intent(this, HotspotActivity::class.java)
            startActivity(intent)
        }

        val desiredFragmentIndex = intent.getIntExtra("desiredFragmentIndex", 0)
        viewPager.setCurrentItem(desiredFragmentIndex, false)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HotspotActivity::class.java)
        startActivity(intent)
    }

    private fun logout()
    {
        ToolBox.users.clear()

        // Signing user out of firebase using FireBaseAuth
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}