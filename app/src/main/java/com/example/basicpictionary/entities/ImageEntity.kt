package com.example.basicpictionary.entities

data class ImageEntity(
    val id: Int,
    val imageUrl: String,
    val difficulty: Int,
    val answer: String
)