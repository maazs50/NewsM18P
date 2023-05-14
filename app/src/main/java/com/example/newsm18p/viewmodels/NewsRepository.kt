package com.example.newsm18p.viewmodels

import com.example.newsm18p.api.RetrofitInstance
import com.example.newsm18p.db.ArticleDatabase

class NewsRepository(val db:ArticleDatabase) {
    val api = RetrofitInstance.api
    suspend fun getBreakingNews(countryCode:String, pageNumber: Int) =
        api.getBreakingNews(countryCode,pageNumber)
    suspend fun searchNews(search: String, pageNumber: Int) =
        api.searchForNews(search,pageNumber)
}