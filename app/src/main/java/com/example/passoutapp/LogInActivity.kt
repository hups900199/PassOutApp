package com.example.passoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.passoutapp.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    // Sets view binding
    private lateinit var binding: ActivityLogInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    // Method: Redirects to main page.
    private fun redirectToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Determines a user has signed in.
        if (firebaseAuth.currentUser != null) redirectToMain();

        // Handles register text view event.
        binding.register.setOnClickListener {
            // Redirects to sign up page.
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Handles login button event.
        binding.btnLogin.setOnClickListener {
            val user = binding.edtEmail.text.toString()
            val pass = binding.edtSignInPassword.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener{
                    if (it.isSuccessful) redirectToMain()
                    else Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            } else Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
    }
}