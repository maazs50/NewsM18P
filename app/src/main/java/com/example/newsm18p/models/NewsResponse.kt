package com.example.newsm18p.models

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)