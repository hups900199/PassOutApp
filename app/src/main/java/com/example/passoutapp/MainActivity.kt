package com.example.passoutapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.passoutapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    // Menu toggle
    lateinit var toggle: ActionBarDrawerToggle

    // Sets view binding.
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Constants
    private companion object {
        private const val STORE_TAG = "FIRESTORE_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menu
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> Toast.makeText(applicationContext, "Click Home", Toast.LENGTH_SHORT).show()
                R.id.nav_history -> Toast.makeText(applicationContext, "Click History", Toast.LENGTH_SHORT).show()
                R.id.nav_setting -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                }
                R.id.nav_sign_out -> {
                    firebaseAuth.signOut()
                    checkUser()
                }
            }

            true
        }

        // Init firebase auth.
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        checkUser()

        // Handles alcohol Button event.
        binding.btnAlcohol.setOnClickListener {
            startActivity(Intent(this, AlcoholActivity::class.java))
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
            val uid = firebaseUser.uid

            // Sets email address.
            binding.txvEmail.text = email

            retrieveUserData(uid)
        }
    }

    // Method: Retrieve user information from database.
    private fun retrieveUserData(uid: String) {
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnCompleteListener { task ->
            val result: StringBuffer = StringBuffer()

            if (task.isSuccessful) {
                // Document found in the offline cache
                val document = task.result
                Log.d(STORE_TAG, "Existed account: ${document?.data}")

                val firebaseUser = firebaseAuth.currentUser
                var userName: TextView = findViewById(R.id.user_name)
                var userEmail: TextView = findViewById(R.id.user_email)

                userName.text = document.data?.getValue("username").toString()
                if (firebaseUser != null) {
                    userEmail.text = firebaseUser.email
                }


                result.append("Username: ").append(document.data?.getValue("username")).append("\n")
                    .append("Weight(kg): ").append(document.data?.getValue("weight")).append("\n")
                    .append("Height(cm): ").append(document.data?.getValue("height")).append("\n")
                    .append("Gender: ").append(document.data?.getValue("gender"))

                binding.txvUserInfo.text = result
            } else {
                Log.d(STORE_TAG, "New account: ", task.exception)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}