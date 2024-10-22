package com.example.if570_lab_uts_farreliuskevin_00000081783

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var userName: TextView
    private lateinit var userNim: TextView
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = FirebaseFirestore.getInstance()
        userName = findViewById(R.id.userName)
        userNim = findViewById(R.id.userNim)
        val editProfileButton: Button = findViewById(R.id.editProfileButton)

        userId = getUserIdFromPreferences()
        loadUserData()

        editProfileButton.setOnClickListener {
            showEditProfileDialog()
        }

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Button Navbar
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

    private fun getUserIdFromPreferences(): String {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", "") ?: ""
    }

    private fun loadUserData() {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userName.text = document.getString("nama") ?: "Nama tidak ditemukan"
                    userNim.text = document.getString("nim") ?: "NIM tidak ditemukan"
                } else {
                    Log.d("ProfileActivity", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ProfileActivity", "Get failed with ", exception)
            }
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editNim = dialogView.findViewById<EditText>(R.id.editNim)

        editName.setText(userName.text)
        editNim.setText(userNim.text)

        AlertDialog.Builder(this)
            .setTitle("Edit Profil")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = editName.text.toString()
                val newNim = editNim.text.toString()
                updateProfile(newName, newNim)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateProfile(newName: String, newNim: String) {
        val userData = hashMapOf(
            "nama" to newName,
            "nim" to newNim
        )

        db.collection("users")
            .document(userId)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                loadUserData()
            }
            .addOnFailureListener { e ->
                Log.e("ProfileActivity", "Error Update Profil: ${e.message}")
            }
    }
}
