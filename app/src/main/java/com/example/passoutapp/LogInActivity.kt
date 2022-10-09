package com.example.passoutapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.passoutapp.databinding.ActivityLogInBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LogInActivity : AppCompatActivity() {

    // Sets view binding.
    private lateinit var binding: ActivityLogInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    // Constants
    private companion object {
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
        private const val FB_TAG = "FACEBOOK_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configures the Google SignIn.
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail() // Only need email from google account.
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Init firebase auth.
        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        checkUser()

        // Handles login Button event.
        binding.btnLogin.setOnClickListener {
            val user = binding.edtEmail.text.toString()
            val pass = binding.edtSignInPassword.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener{
                    if (it.isSuccessful) goToActivity(this, MainActivity::class.java)
                    else Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            } else Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }

        // Google SignIn ImageView.
        // Handles Google ImageView event.
        binding.imvGoogle.setOnClickListener {
            // Begin Google SignIn
            Log.d(TAG, "onCreate: begin Google SignIn")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        // Facebook SignIn ImageView.
        binding.btnFacebook.setReadPermissions("email")
        binding.btnFacebook.setOnClickListener {
            facebookSignIn()
        }

        // Handles register TextView event.
        binding.txvRegister.setOnClickListener {
            goToActivity(this, SignUpActivity::class.java)
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

        if (firebaseUser != null) {
            goToActivity(this, MainActivity::class.java)
        }
    }

    private fun facebookSignIn() {
        binding.btnFacebook.registerCallback(callbackManager, object:FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken (result!!.accessToken)
            }

            override fun onCancel() {
                goToActivity(this@LogInActivity, LogInActivity::class.java)
            }

            override fun onError(error: FacebookException) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun handleCredential(credential: AuthCredential, tag: String, method: String) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                // Login Success
                Log.d(tag, "${method}: LoggedIn")

                // Gets loggedIn user.
                val firebaseUser = firebaseAuth.currentUser

                // Gets user info.
                val uid = firebaseUser!!.uid
                val email = firebaseUser.email

                Log.d(tag, "${method}: Uid: $uid")
                Log.d(tag, "${method}: Email: $email")

                // Determines user is new or existing.
                if (authResult.additionalUserInfo!!.isNewUser) {
                    // User is new - Account created
                    Log.d(tag, "${method}: Account created... \n$email")
                    Toast.makeText(this@LogInActivity, "Account created... \n$email", Toast.LENGTH_SHORT).show()
                } else {
                    // Existing user - LoggedIn
                    Log.d(tag, "${method}: Existing user... \n$email")
                    Toast.makeText(this@LogInActivity, "LoggedIn... \n$email", Toast.LENGTH_SHORT).show()
                }

                // Starts main activity.
                goToActivity(this, MainActivity::class.java)
            }
            .addOnFailureListener { e ->
                // Login Failed
                Log.d(tag, "${method}: Login Failed due to ${e.message}")
                Toast.makeText(this@LogInActivity, "Login Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
        Log.d(FB_TAG, "handleFacebookAccessToken: begin firebase auth with facebook account")

        // Gets credential
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        handleCredential(credential, FB_TAG, "handleFacebookAccessToken")
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account")

        // Gets credential
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        handleCredential(credential, TAG, "firebaseAuthWithGoogleAccount")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google SignIn success, now auth with firebase.
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            }
            catch (e: Exception) {
                // Failed Google SignIn.
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        } else callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}