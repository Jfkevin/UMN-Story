package com.example.if570_lab_uts_farreliuskevin_00000081783

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var nameEditText: EditText
    private lateinit var nimEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        db = FirebaseFirestore.getInstance()
        nameEditText = findViewById(R.id.nameEditText)
        nimEditText = findViewById(R.id.nimEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            submitData()
        }
    }

    private fun submitData() {
        val name = nameEditText.text.toString()
        val nim = nimEditText.text.toString()

        if (name.isNotEmpty() && nim.isNotEmpty()) {
            val user = hashMapOf(
                "nama" to name,
                "nim" to nim
            )

            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    saveUserIdToPreferences(documentReference.id)

                    Log.d("SignupActivity", "User added with ID: ${documentReference.id}")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w("SignupActivity", "Error adding user", e)
                }
        } else {
            Toast.makeText(this, "Mohon masukkan Nama dan NIM", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserIdToPreferences(userId: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.apply()
    }
}
