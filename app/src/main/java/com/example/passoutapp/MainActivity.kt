package com.example.passoutapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.passoutapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // Sets view binding.
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth.
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        // Handles sign out Button event.
        binding.btnSignOut.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        // Handles setting Button event.
        binding.btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    // Method: Redirects to an activity.
    private fun goToActivity(activity: Activity, classes: Class<*>?) {
        val intent = Intent(activity, classes)
        startActivity(intent)
        activity.finish()
    }

    // Determines a user has signed in.
    private fun checkUser() {
        // Gets current user.
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null) {
            // User not logged in.
            goToActivity(this, LogInActivity::class.java)
        } else {
            // User logged in.
            // Gets user info.
            val email = firebaseUser.email
            Log.d("DATABASE", "UID:... ${firebaseUser.uid}")

            // Sets email address.
            binding.txvEmail.text = email
        }
    }
}