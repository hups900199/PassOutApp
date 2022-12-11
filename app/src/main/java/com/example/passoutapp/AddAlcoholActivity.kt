package com.example.passoutapp

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.passoutapp.databinding.ActivityAddAlcoholBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddAlcoholActivity : AppCompatActivity() {

    // Sets view binding.
    private lateinit var binding: ActivityAddAlcoholBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Constants
    private companion object {
        private const val STORE_TAG = "FIRESTORE_TAG"
        private val ALCOHOL_TYPES = arrayOf("Beer", "Wine", "Other")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAlcoholBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth.
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Create an ArrayAdapter using a simple spinner layout and ALCOHOL_TYPES array
        val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, ALCOHOL_TYPES)
        // Set layout to use when the list of choices appear
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        binding.spnAlcoholType.adapter = arrayAdapter

        binding.btnAddAlcohol.setOnClickListener {
            Log.d(STORE_TAG, "Retrieve Inputs")

            val extras = intent.extras

            val rid = extras?.getString("roundId").toString()
            val alcoholName = binding.edtAlcoholName.text.toString().trim()
            val alcoholType = binding.spnAlcoholType.selectedItem.toString().trim()
            val alcoholPercentage = binding.edtAlcoholPercentage.text.toString().trim().toDouble()
            val liter = binding.edtLiter.text.toString().trim().toDouble()

            Log.d(STORE_TAG, "RID: $rid")
            Log.d(STORE_TAG, "Alcohol Name: $alcoholName")
            Log.d(STORE_TAG, "Alcohol Type: $alcoholType")
            Log.d(STORE_TAG, "Alcohol Percentage: $alcoholPercentage")
            Log.d(STORE_TAG, "Liter: $liter")

            if (rid.isNotEmpty() && !alcoholPercentage.isNaN() && !liter.isNaN()) {
                val date = Date()
                saveAlcohol(rid, alcoholName, alcoholType, alcoholPercentage, liter, date)
                this.finish()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Alcohol name cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            this.finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun saveAlcohol(rid: String,
                            alcoholName: String,
                            alcoholType: String,
                            alcoholPercentage: Double,
                            liter: Double,
                            date: Date)
    {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val items = HashMap<String, Any>()
        items.put("rid", rid)
        items.put("alcoholName", alcoholName)
        items.put("alcoholType", alcoholType)
        items.put("alcoholPercentage", alcoholPercentage)
        items.put("liter", liter)
        items.put("createDate", dateFormat.format(date))
        items.put("updateDate", dateFormat.format(date))

        db.collection("alcohols")
            .add(items)
            .addOnSuccessListener {
                Toast.makeText(this, "Add alcohol successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Add alcohol failed", Toast.LENGTH_SHORT).show()
            }
    }
}