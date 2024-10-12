package com.example.lab_04

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_04.databinding.ActivityOnboardBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardBinding // Declare binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the binding
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set the content view to the binding root

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent) // Start the LandingActivity
        }
    }
}
