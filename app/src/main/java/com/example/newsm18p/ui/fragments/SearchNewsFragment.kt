package com.example.newsm18p.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.example.newsm18p.R
import com.example.newsm18p.db.ArticleDatabase
import com.example.newsm18p.ui.NewsActivity
import com.example.newsm18p.viewmodels.NewsRepository
import com.example.newsm18p.viewmodels.NewsViewModel
import com.example.newsm18p.viewmodels.NewsViewModelFactory

class SearchNewsFragment:Fragment(R.layout.fragment_search_news) {
    val viewModel: NewsViewModel by activityViewModels(){
        val repo = NewsRepository(ArticleDatabase(this.requireContext()))
        NewsViewModelFactory(repo)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}