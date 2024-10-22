package com.example.if570_lab_uts_farreliuskevin_00000081783

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class PostingActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var imageView: ImageView
    private lateinit var buttonUpload: ImageButton
    private lateinit var buttonSelectImage: ImageButton

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting)

        editText = findViewById(R.id.editText)
        imageView = findViewById(R.id.imageView)
        buttonUpload = findViewById(R.id.buttonUpload)
        buttonSelectImage = findViewById(R.id.buttonSelectImage)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        db = FirebaseFirestore.getInstance()

        buttonSelectImage.setOnClickListener {
            selectImage()
        }

        buttonUpload.setOnClickListener {
            uploadImage()
        }

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

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
            imageView.visibility = ImageView.VISIBLE
        }
    }

    private fun uploadImage() {
        if (imageUri != null) {
            val fileReference = storageReference.child("stories/${System.currentTimeMillis()}.jpg")
            val uploadTask: UploadTask = fileReference.putFile(imageUri!!)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val story = hashMapOf(
                        "imageURL" to imageUrl,
                        "text" to editText.text.toString(),
                        "likes" to "0",
                        "pinned" to false,
                        "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )

                    db.collection("stories")
                        .add(story)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Story Sukses Ditambahkan", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error adding story: ${e.message}")
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal Upload", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Silahkan pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
        }
    }
}
