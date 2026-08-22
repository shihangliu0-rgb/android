package com.example.myapplication.data

data class Wish(
    val id: Long,
    val text: String,
    val done: Boolean,
    val createdAt: Long,
)
