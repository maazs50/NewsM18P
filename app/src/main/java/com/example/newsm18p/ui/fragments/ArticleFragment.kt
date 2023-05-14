package com.example.newsm18p.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.navArgs
import com.example.newsm18p.R
import com.example.newsm18p.db.ArticleDatabase
import com.example.newsm18p.ui.NewsActivity
import com.example.newsm18p.viewmodels.NewsRepository
import com.example.newsm18p.viewmodels.NewsViewModel
import com.example.newsm18p.viewmodels.NewsViewModelFactory

class ArticleFragment:Fragment(R.layout.fragment_article) {
    val viewModel: NewsViewModel by activityViewModels(){
        val repo = NewsRepository(ArticleDatabase(this.requireContext()))
        NewsViewModelFactory(repo)
    }
    val args: ArticleFragmentArgs by navArgs()
    lateinit var webView: WebView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
    }
}