package com.example.passoutapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passoutapp.databinding.ActivityAlcoholBinding
import com.example.passoutapp.databinding.ActivitySettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AlcoholActivity : AppCompatActivity() {

    // Sets view binding.
    private lateinit var binding: ActivityAlcoholBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var newArrayList: ArrayList<Alcohols>
    lateinit var imageId : Array<Int>
    lateinit var heading : Array<String>

    // Constants
    private companion object {
        private const val STORE_TAG = "FIRESTORE_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlcoholBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth.
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Handles alcohol Button event.
        binding.btnAddAlcohol.setOnClickListener {
            startActivity(Intent(this, AddAlcoholActivity::class.java))
        }

        retrieveUserData(firebaseAuth.uid.toString())




        imageId = arrayOf(
            R.drawable.google,
            R.drawable.fb
        )

        heading = arrayOf(
            "First Item abasdfewrqewr here",
            "Seacond adjsifoewjqior"
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf<Alcohols>()
        //getData()





    }

    private fun getData() {
        for (i in imageId.indices ) {
            val alcohols = Alcohols(imageId[i], heading[i])
            newArrayList.add(alcohols)
        }

        binding.recyclerView.adapter = AlcoholAdapter(newArrayList)
    }

    // Method: Redirects to an activity.
    private fun goToActivity(activity: Activity, classes: Class<*>?) {
        val intent = Intent(activity, classes)
        startActivity(intent)
        activity.finish()
    }

    // Method: Retrieve user information from database.
    private fun retrieveUserData(uid: String) {

        // Create a reference to the cities collection
        val alcoholsRef = db.collection("alcohols")

        // Create a query against the collection.
        val query = alcoholsRef.whereEqualTo("uid", uid)

        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(STORE_TAG, "${document.id} => ${document.data}")

                    val alcohols = Alcohols(imageId[1], document.data["liter"].toString())
                    newArrayList.add(alcohols)
                }

                binding.recyclerView.adapter = AlcoholAdapter(newArrayList)
            }
            .addOnFailureListener { exception ->
                Log.w(STORE_TAG, "Error getting documents: ", exception)
            }
    }
}