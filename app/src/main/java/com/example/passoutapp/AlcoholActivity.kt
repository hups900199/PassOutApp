package com.example.passoutapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passoutapp.databinding.ActivityAlcoholBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AlcoholActivity : AppCompatActivity() {

    // Sets view binding.
    private lateinit var binding: ActivityAlcoholBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var newArrayList: ArrayList<Alcohols>
    lateinit var imageId : Array<Int>

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

        // Initialize recycler view.
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        newArrayList = arrayListOf<Alcohols>()
        imageId = arrayOf(
            R.drawable.beer,
            R.drawable.ic_baseline_wine_bar_24,
            R.drawable.ic_baseline_question_mark_24
        )

        // Handles alcohol Button event.
        binding.btnAddAlcohol.setOnClickListener {
            startActivity(Intent(this, AddAlcoholActivity::class.java))
        }

        // Handles refresh Button event.
        binding.btnRefresh.setOnClickListener {
            finish()
            startActivity(getIntent())
        }

        retrieveUserData(firebaseAuth.uid.toString())
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

                    var typeNumber = 2
                    if (document.data["alcoholType"].toString() == "Beer") typeNumber = 0
                    if (document.data["alcoholType"].toString() == "Wine") typeNumber = 1

                    val alcohols = Alcohols(
                        document.id,
                        imageId[typeNumber],
                        document.data["alcoholName"].toString(),
                        document.data["alcoholType"].toString(),
                        document.data["alcoholPercentage"] as Double,
                        document.data["liter"] as Double,
                        document.data["createDate"].toString(),
                        document.data["updateDate"].toString()
                    )

                    newArrayList.add(alcohols)
                }

                val adapter = AlcoholAdapter(newArrayList)

                val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        db.collection("alcohols").document(newArrayList.get(position).id)
                            .delete()
                            .addOnSuccessListener { Log.d(STORE_TAG, "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w(STORE_TAG, "Error deleting document", e) }

                        newArrayList.removeAt(position)
                        binding.recyclerView.adapter?.notifyItemRemoved(position)
                    }
                }

                val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)

                itemTouchHelper.attachToRecyclerView(binding.recyclerView)

                binding.recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.d(STORE_TAG, "Error getting documents: ", exception)
            }
    }
}