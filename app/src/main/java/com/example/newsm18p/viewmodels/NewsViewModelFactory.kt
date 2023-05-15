package com.example.newsm18p.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsm18p.NewsApp

class NewsViewModelFactory(
    val app: NewsApp,
    val newsRepo: NewsRepository
    ): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app,newsRepo) as T
    }
}