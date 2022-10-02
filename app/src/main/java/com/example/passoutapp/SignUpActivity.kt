package com.example.passoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.passoutapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    // Sets view binding.
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    // Method: Redirects to login page.
    private fun redirectToLogIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Handles sign in text view event.
        binding.txvSignIn.setOnClickListener { redirectToLogIn() }

        // Handles sign up button event.
        binding.btnSignUp.setOnClickListener {
            val user = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()
            val confirmpass = binding.edtConfirmPassword.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty() && confirmpass.isNotEmpty()) {
                if (pass == confirmpass) {
                    firebaseAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener{
                        if (it.isSuccessful) redirectToLogIn()
                        else Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                } else Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
    }
}