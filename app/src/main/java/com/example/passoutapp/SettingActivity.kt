package com.example.passoutapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.passoutapp.databinding.ActivitySettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingActivity : AppCompatActivity() {

    // Sets view binding.
    private lateinit var binding: ActivitySettingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
//    private var dbHandler: DBHandler? = null

    // Constants
    private companion object {
        private const val STORE_TAG = "FIRESTORE_TAG"
        private val GENDERS = arrayOf("Male", "Female", "Other")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth.
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
//        dbHandler = DBHandler(this);

        retrieveUserData(firebaseAuth.uid.toString())

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, GENDERS)
        // Set layout to use when the list of choices appear
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        binding.spnGender.adapter = arrayAdapter

        binding.btnUpdate.setOnClickListener {
            Log.d(STORE_TAG, "Retrieve Inputs")

            val uid = firebaseAuth.uid.toString()
            val username = binding.edtUsername.text.toString().trim()
            val weight = binding.edtWeight.text.toString().trim().toDouble()
            val height = binding.edtHeight.text.toString().trim().toDouble()
            val gender = binding.spnGender.selectedItem.toString().trim()

            Log.d(STORE_TAG, "UID: ${uid}")
            Log.d(STORE_TAG, "Username: ${username}")
            Log.d(STORE_TAG, "Weight: ${weight}")
            Log.d(STORE_TAG, "Height: ${height}")
            Log.d(STORE_TAG, "Gender: ${gender}")

            if (!uid.isEmpty() && !username.isEmpty()) {
                saveFireStore(uid, username, weight, height, gender)
            } else {
                Toast.makeText(this, "Username cannot be empty.", Toast.LENGTH_SHORT).show()
            }

//            val myRef = db.getReference()
//            val gender = gender_model("male")
//            myRef.child("SADFEWERS").setValue(gender)
//                .addOnSuccessListener {
//                    Log.d("DATABASE", "Account created... ${uid}\n")
//                }.addOnFailureListener{
//                    Log.d("DATABASE", "Failed created... ${uid}\n")
//                }

//            dbHandler!!.addNewCourse("courseName", "courseDuration", "courseDescription", "courseTracks");
        }

        binding.btnCancel.setOnClickListener {
            goToActivity(this, MainActivity::class.java)
        }

        readFireStoreData()
    }

    // Method: Redirects to an activity.
    private fun goToActivity(activity: Activity, classes: Class<*>?) {
        val intent = Intent(activity, classes)
        startActivity(intent)
        activity.finish()
    }

    fun saveFireStore(uid: String, username: String, weight: Double, height: Double, gender: String) {
        val items = HashMap<String, Any>()
        items.put("uid", uid)
        items.put("username", username)
        items.put("weight", weight)
        items.put("height", height)
        items.put("gender", gender)

//        val user: MutableMap<String, Any> = HashMap()
//        user["uid"] = uid
//        user["height"] = height

        db.collection("users")
            .document(uid)
            .set(items)
            .addOnSuccessListener {
                Toast.makeText(this, "Update successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            }

        readFireStoreData()

//            val myRef = db.getReference()
//            val gender = gender_model("male")
//            myRef.child("SADFEWERS").setValue(gender)
//                .addOnSuccessListener {
//                    Log.d("DATABASE", "Account created... ${uid}\n")
//                }.addOnFailureListener{
//                    Log.d("DATABASE", "Failed created... ${uid}\n")
//                }
    }

    private fun readFireStoreData() {
        db.collection("users")
            .get()
            .addOnCompleteListener {
                val result: StringBuffer = StringBuffer()

                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        result.append(document.data.getValue("uid")).append("\n")
                            .append(document.data.getValue("username")).append("\n")
                            .append(document.data.getValue("weight")).append("\n")
                            .append(document.data.getValue("height")).append("\n")
                            .append(document.data.getValue("gender"))
                    }

                    binding.txvOutput.text = result
                }
            }
    }

    private fun retrieveUserData(uid: String) {
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Document found in the offline cache
                val document = task.result
                Log.d(STORE_TAG, "Existed account: ${document?.data}")

                binding.edtUsername.setText(document.data?.getValue("username").toString())
                binding.edtWeight.setText(document.data?.getValue("weight").toString())
                binding.edtHeight.setText(document.data?.getValue("height").toString())
                binding.spnGender.setSelection(GENDERS.indexOf(document.data?.getValue("gender").toString()))
            } else {
                Log.d(STORE_TAG, "New account: ", task.exception)
            }
        }
    }
}