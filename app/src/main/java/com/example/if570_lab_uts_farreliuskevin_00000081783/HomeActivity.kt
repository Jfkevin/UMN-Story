package com.example.if570_lab_uts_farreliuskevin_00000081783

import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var storyAdapter: StoryAdapter
    private val stories = mutableListOf<Story>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()

        storyAdapter = StoryAdapter(stories)
        val recyclerView: RecyclerView = findViewById(R.id.storyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = storyAdapter
        fetchStories()

        // Navbar Button
        findViewById<ImageButton>(R.id.buttonPosting).setOnClickListener {
            startActivity(Intent(this, PostingActivity::class.java))
        }
        findViewById<ImageButton>(R.id.buttonHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<ImageButton>(R.id.buttonProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun fetchStories() {
        db.collection("stories")
            .get()
            .addOnSuccessListener { documents ->
                stories.clear()
                for (document in documents) {
                    val story = document.toObject(Story::class.java).copy(id = document.id)
                    Log.d("FirestoreData", "Story: $story")
                    stories.add(story)
                }
                stories.sortWith(compareByDescending<Story> { it.pinned }.thenByDescending { it.timestamp })
                storyAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.e("FirestoreError", "Error getting documents: ", it)
            }
    }

}