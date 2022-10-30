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

        // Reading
//        binding.spnGender.setSelection()

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, GENDERS)
        // Set layout to use when the list of choices appear
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        binding.spnGender!!.setAdapter(arrayAdapter)

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

            if (!uid.isEmpty() && !username.isEmpty())

            saveFireStore(uid, username, weight, height, gender)

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
//        val user: MutableMap<String, Any> = HashMap()
//        user["uid"] = uid
//        user["height"] = height

        val items = HashMap<String, Any>()
        items.put("uid", uid)
        items.put("username", username)
        items.put("weight", weight)
        items.put("height", height)
        items.put("gender", gender)

        db.collection("Users")
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

    fun readFireStoreData() {
        db.collection("Users")
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

                    binding.txvOutput.setText(result)
                }
            }
    }
}