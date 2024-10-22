package com.example.if570_lab_uts_farreliuskevin_00000081783

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var nameEditText: EditText
    private lateinit var nimEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = FirebaseFirestore.getInstance()
        nameEditText = findViewById(R.id.nameEditText)
        nimEditText = findViewById(R.id.nimEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val name = nameEditText.text.toString()
        val nim = nimEditText.text.toString()

        if (name.isNotEmpty() && nim.isNotEmpty()) {
            db.collection("users")
                .whereEqualTo("nama", name)
                .whereEqualTo("nim", nim)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Log.w("LoginActivity", "Nama dan NIM yang diinput salah")
                        Toast.makeText(this, "Nama dan NIM tidak ditemukan", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("LoginActivity", "Login berhasil!")
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("LoginActivity", "Error Pengambilan Dokumen: ", e)
                    Toast.makeText(this, "Terjadi kesalahan, coba lagi", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Mohon masukkan Nama dan NIM", Toast.LENGTH_SHORT).show()
        }
    }
}
