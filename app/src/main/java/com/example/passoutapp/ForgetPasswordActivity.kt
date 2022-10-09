package com.example.passoutapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.passoutapp.databinding.ActivityForgetPasswordBinding
import com.example.passoutapp.databinding.ActivityLogInBinding
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {

    // Sets view binding.
    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val email: String = binding.edtEmail.text.toString().trim { it <= ' '}

            if (email.isNotEmpty()) {
                // Init firebase auth.
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful) goToActivity(this@ForgetPasswordActivity, LogInActivity::class.java)
                        else Toast.makeText(this@ForgetPasswordActivity, task.exception!!.toString(), Toast.LENGTH_SHORT).show()
                    }
            } else Toast.makeText(this@ForgetPasswordActivity, "Please enter email address.", Toast.LENGTH_SHORT).show()
        }
    }

    // Method: Redirects to an activity.
    private fun goToActivity(activity: Activity, classes: Class<*>?) {
        val intent = Intent(activity, classes)
        startActivity(intent)
        activity.finish()
    }
}