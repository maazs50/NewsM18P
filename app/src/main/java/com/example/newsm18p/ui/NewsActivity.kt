package com.example.newsm18p.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsm18p.R
import com.example.newsm18p.db.ArticleDatabase
import com.example.newsm18p.viewmodels.NewsRepository
import com.example.newsm18p.viewmodels.NewsViewModel
import com.example.newsm18p.viewmodels.NewsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class NewsActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var viewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        viewModelSetup()
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.newsNavHostFragment)
        bottomNavigationView.setupWithNavController(navController)
    }

    fun viewModelSetup(){
        val repo = NewsRepository(ArticleDatabase(this))
        val vmFactory = NewsViewModelFactory(repo)
        viewModel = ViewModelProvider(this,vmFactory).get(NewsViewModel::class.java)
    }
}