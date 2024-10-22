package com.example.if570_lab_uts_farreliuskevin_00000081783

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class StoryAdapter(private val storyList: List<Story>) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storyImage: ImageView = itemView.findViewById(R.id.storyImage)
        val storyText: TextView = itemView.findViewById(R.id.storyText)
        val buttonPin: ImageButton = itemView.findViewById(R.id.buttonPin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]

        Glide.with(holder.itemView.context)
            .load(story.imageURL)
            .into(holder.storyImage)

        holder.storyText.text = story.text

        if (story.pinned) {
            holder.buttonPin.setImageResource(R.drawable.ic_pin)  // Ikon untuk pinned
        } else {
            holder.buttonPin.setImageResource(R.drawable.ic_unpin)  // Ikon untuk unpinned
        }

        holder.itemView.setOnClickListener {
            Log.d("StoryClick", "Clicked story: ${story.id}, pinned: ${story.pinned}")

            story.pinned = !story.pinned
            updateStoryInFirestore(story)
            notifyItemChanged(position)
        }
    }

    private fun updateStoryInFirestore(story: Story) {
        val db = FirebaseFirestore.getInstance()
        db.collection("stories").document(story.id).set(story)
            .addOnSuccessListener {
                Log.d("FirestoreUpdate", "Story updated: ${story.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreUpdateError", "Error Update Document", e)
            }
    }

    override fun getItemCount(): Int {
        return storyList.size
    }
}
