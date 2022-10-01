package com.example.passoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.passoutapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private fun redirectToLogIn(): Void? {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener { redirectToLogIn() }

        binding.button.setOnClickListener {
            val user = binding.username.text.toString()
            val pass = binding.passET.text.toString()
            val confirmpass = binding.confirmPassEt.text.toString()

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