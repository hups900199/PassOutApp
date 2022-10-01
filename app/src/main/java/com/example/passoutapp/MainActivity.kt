package com.example.passoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.passoutapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.txvEmail.text = FirebaseAuth.getInstance().currentUser?.email

        binding.btnSignOut.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
    }
}