package com.example.passoutapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passoutapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    // Menu toggle
    lateinit var toggle: ActionBarDrawerToggle

    // Sets view binding.
    private lateinit var binding: ActivityMainBinding
    private lateinit var newArrayList: ArrayList<Alcohols>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var imageId : Array<Int>

    // Constants
    private companion object {
        private const val STORE_TAG = "FIRESTORE_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Menu
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> Toast.makeText(applicationContext, "Click Home", Toast.LENGTH_SHORT).show()
                R.id.nav_history -> Toast.makeText(applicationContext, "Click History", Toast.LENGTH_SHORT).show()
                R.id.nav_setting -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                }
                R.id.nav_sign_out -> {
                    firebaseAuth.signOut()
                    checkUser()
                }
            }

            true
        }

        // Init firebase auth.
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        checkUser()

        // Initialize recycler view.
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        newArrayList = arrayListOf<Alcohols>()
        imageId = arrayOf(
            R.drawable.beer,
            R.drawable.ic_baseline_wine_bar_24,
            R.drawable.ic_baseline_question_mark_24
        )

        // Handles start Button event.
        binding.btnStart.setOnClickListener {
            binding.btnStart.setVisibility(View.INVISIBLE); //HIDE the button
            binding.drinkingLayout.setVisibility(View.VISIBLE); //SHOW the layout
        }

        // Handles alcohol Button event.
        binding.btnAddAlcohol.setOnClickListener {
            startActivity(Intent(this, AddAlcoholActivity::class.java))
        }

        // Handles refresh Button event.
        binding.btnRefresh.setOnClickListener {
            finish()
            startActivity(getIntent())
        }

        // Handles pass out Button event.
        binding.btnPassOut.setOnClickListener{
            binding.btnStart.setVisibility(View.VISIBLE); //SHOW the button
            binding.drinkingLayout.setVisibility(View.INVISIBLE); //HIDE the layout
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

        if (firebaseUser == null) {
            // User not logged in.
            goToActivity(this, LogInActivity::class.java)
        } else {
            // User logged in.
            // Gets user info.
            val email = firebaseUser.email
            val uid = firebaseUser.uid

            // Sets email address.
            binding.txvEmail.text = email

            retrieveUserData(uid)
            retrieveUserDrinkList(uid)
        }
    }

    // Method: Retrieve user information from database.
    private fun retrieveUserData(uid: String) {
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnCompleteListener { task ->
            val result: StringBuffer = StringBuffer()

            if (task.isSuccessful) {
                // Document found in the offline cache
                val document = task.result
                Log.d(STORE_TAG, "Existed account: ${document?.data}")

                val firebaseUser = firebaseAuth.currentUser
                var userName: TextView = findViewById(R.id.user_name)
                var userEmail: TextView = findViewById(R.id.user_email)

                userName.text = document.data?.getValue("username").toString()
                if (firebaseUser != null) {
                    userEmail.text = firebaseUser.email
                }

                result.append("Username: ").append(document.data?.getValue("username")).append("\n")
                    .append("Weight(kg): ").append(document.data?.getValue("weight")).append("\n")
                    .append("Height(cm): ").append(document.data?.getValue("height")).append("\n")
                    .append("Gender: ").append(document.data?.getValue("gender"))

                binding.txvUserInfo.text = result
            } else {
                Log.d(STORE_TAG, "New account: ", task.exception)
            }
        }
    }

    // Method: Retrieve user information from database.
    private fun retrieveUserDrinkList(uid: String) {

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}