package com.example.newsm18p.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsm18p.R
import com.example.newsm18p.adapter.NewsAdapter
import com.example.newsm18p.db.ArticleDatabase
import com.example.newsm18p.ui.NewsActivity
import com.example.newsm18p.util.Resource
import com.example.newsm18p.viewmodels.NewsRepository
import com.example.newsm18p.viewmodels.NewsViewModel
import com.example.newsm18p.viewmodels.NewsViewModelFactory
import com.facebook.shimmer.ShimmerFrameLayout

class BreakingNewsFragment:Fragment(R.layout.fragment_breaking_news) {
    val viewModel: NewsViewModel by activityViewModels(){
        val repo = NewsRepository(ArticleDatabase(this.requireContext()))
        NewsViewModelFactory(repo)
    }
    lateinit var newsAdapter: NewsAdapter
    lateinit var rvList: RecyclerView
    lateinit var progressBar: ProgressBar
    val TAG = this.tag
    lateinit var shimmerContainer: ShimmerFrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shimmerContainer = view.findViewById(R.id.shimmer_view_container)
        setupRecyclerView(view)
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success ->{
                    hideProgressBar()
                    shimmerContainer.stopShimmer()
                    shimmerContainer.visibility = View.GONE
                    rvList.visibility = View.VISIBLE
                    it.data?.let {
                        newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }

                is Resource.Loading ->{
                    showProgressBar()
                    shimmerContainer.visibility = View.VISIBLE
                    shimmerContainer.startShimmer()
                }
                is Resource.Error ->{
                    hideProgressBar()
                    shimmerContainer.stopShimmer()
                    shimmerContainer.visibility = View.GONE
                    rvList.visibility = View.VISIBLE
                    it.message?.let {
                        Log.e(TAG,"An error occured")
                    }
                }

            }
        })
    }
    private fun setupRecyclerView(view: View){
        progressBar = view.findViewById(R.id.paginationProgressBar)
        newsAdapter = NewsAdapter()
        rvList = view.findViewById(R.id.rvBreakingNews)
        rvList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar(){
        progressBar.visibility = View.INVISIBLE
    }
}