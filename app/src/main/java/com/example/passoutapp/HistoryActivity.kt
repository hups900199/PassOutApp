package com.example.passoutapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passoutapp.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryActivity : AppCompatActivity() {

    // Sets view binding.
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var newArrayList: ArrayList<Rounds>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Constants
    private companion object {
        private const val STORE_TAG = "FIRESTORE_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init firebase auth.
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize recycler view.
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        newArrayList = arrayListOf<Rounds>()

        firebaseAuth.currentUser?.let { it1 -> retrieveUserRound(it1.uid) } // Create a new round
    }

    // Method: Retrieve user current round from database.
    private fun retrieveUserRound(uid: String) {
        // Create a reference to the cities collection
        val roundsRef = db.collection("rounds")

        // Create a query against the collection.
        val query = roundsRef.whereEqualTo("uid", uid).whereEqualTo("status", "finish")

        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(STORE_TAG, "${document.id} => ${document.data}")

                    val alcohols = Rounds(
                        document.id,
                        document.data["bac"] as Double,
                        document.data["status"].toString(),
                        document.data["createDate"].toString(),
                        document.data["finishDate"].toString()
                    )

                    newArrayList.add(alcohols)
                }

                val adapter = RoundAdapter(newArrayList)

                val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        db.collection("rounds").document(newArrayList.get(position).id)
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