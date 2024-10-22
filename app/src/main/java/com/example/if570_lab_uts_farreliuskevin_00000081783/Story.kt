package com.example.if570_lab_uts_farreliuskevin_00000081783

data class Story(
    val id: String = "",
    val imageURL: String = "",
    val text: String = "",
    val likes: String = "0",
    var pinned: Boolean = false,
    val timestamp: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)