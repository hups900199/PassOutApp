package com.example.passoutapp

import android.app.Activity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth.
        firebaseAuth = FirebaseAuth.getInstance()

        // Handles sign up Button event.
        binding.btnSignUp.setOnClickListener {
            val user = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()
            val confirmpass = binding.edtConfirmPassword.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty() && confirmpass.isNotEmpty()) {
                if (pass == confirmpass) {
                    firebaseAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener{
                        if (it.isSuccessful) goToActivity(this, LogInActivity::class.java)
                        else Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                } else Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }

        // Handles sign in TextView event.
        binding.txvSignIn.setOnClickListener {
            goToActivity(this, LogInActivity::class.java)
        }
    }

    // Method: Redirects to an activity.
    private fun goToActivity(activity: Activity, classes: Class<*>?) {
        val intent = Intent(activity, classes)
        startActivity(intent)
        activity.finish()
    }
}