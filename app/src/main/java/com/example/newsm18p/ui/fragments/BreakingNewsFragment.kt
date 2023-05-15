package com.example.newsm18p.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsm18p.R
import com.example.newsm18p.adapter.NewsAdapter
import com.example.newsm18p.db.ArticleDatabase
import com.example.newsm18p.models.Article
import com.example.newsm18p.ui.NewsActivity
import com.example.newsm18p.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newsm18p.util.Resource
import com.example.newsm18p.viewmodels.NewsRepository
import com.example.newsm18p.viewmodels.NewsViewModel
import com.example.newsm18p.viewmodels.NewsViewModelFactory
import com.facebook.shimmer.ShimmerFrameLayout

class BreakingNewsFragment:Fragment(R.layout.fragment_breaking_news), NewsAdapter.OnClickListener {
    val viewModel: NewsViewModel by activityViewModels(){
        val repo = NewsRepository(ArticleDatabase(this.requireContext()))
        NewsViewModelFactory(repo)
    }
    lateinit var newsAdapter: NewsAdapter
    lateinit var rvList: RecyclerView
    lateinit var progressBar: ProgressBar
    val TAG = this.tag
    lateinit var shimmerContainer: ShimmerFrameLayout
//Pagenation
    var isLoading = false
    var isLastPage =false
    var isScrolling = false
    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = rvList.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem =firstVisibleItemPosition+visibleItemCount >= totalItemCount
            val isNotAtBegining =firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount>=QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBegining &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate){
                viewModel.getBreakingNews("in")
                isScrolling = false
            }
        }
    }
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
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage){
                            rvList.setPadding(0,0,0,0)
                        }
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
        newsAdapter = NewsAdapter(this)
        rvList = view.findViewById(R.id.rvBreakingNews)
        rvList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    private fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
        isLoading = true
    }
    private fun hideProgressBar(){
        progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    override fun onClick(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article",article)
        }
        findNavController().navigate(
            R.id.action_breakingNewsFragment_to_articleFragment,
            bundle
        )
    }
}