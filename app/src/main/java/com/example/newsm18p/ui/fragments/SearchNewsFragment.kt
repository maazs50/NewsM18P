package com.example.newsm18p.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsm18p.NewsApp
import com.example.newsm18p.R
import com.example.newsm18p.adapter.NewsAdapter
import com.example.newsm18p.db.ArticleDatabase
import com.example.newsm18p.models.Article
import com.example.newsm18p.util.Constants
import com.example.newsm18p.util.Constants.Companion.SEARCH_NEWS_DELAY
import com.example.newsm18p.util.Resource
import com.example.newsm18p.viewmodels.NewsRepository
import com.example.newsm18p.viewmodels.NewsViewModel
import com.example.newsm18p.viewmodels.NewsViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment:Fragment(R.layout.fragment_search_news),NewsAdapter.OnClickListener {
    val viewModel: NewsViewModel by activityViewModels(){
        val repo = NewsRepository(ArticleDatabase(this.requireContext()))
        NewsViewModelFactory(activity?.application as NewsApp,repo)
    }
    lateinit var newsAdapter: NewsAdapter
    lateinit var rvList: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var etSearch: EditText
    val TAG = this.tag
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
            val isTotalMoreThanVisible = totalItemCount>= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBegining &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate){
                viewModel.getSearchNews(etSearch.text.toString())
                isScrolling = false
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        makeSearchQuery()
        viewModel.searchNews.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success ->{
                    hideProgressBar()
                    it.data?.let {
                        newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage){
                            rvList.setPadding(0,0,0,0)
                        }
                    }

                }
                is Resource.Loading ->{
                    showProgressBar()
                }
                is Resource.Error ->{
                    hideProgressBar()
                    it.message?.let {
                        Toast.makeText(activity,"An error $it", Toast.LENGTH_LONG).show()
                    }
                }

            }
        })
    }
    private fun setupRecyclerView(view: View){
        newsAdapter = NewsAdapter(this)
        progressBar = view.findViewById(R.id.paginationProgressBar)
        rvList = view.findViewById(R.id.rvSearchNews)
        etSearch = view.findViewById(R.id.etSearch)
        rvList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
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
    private fun makeSearchQuery(){
        var job: Job? = null
        etSearch.addTextChangedListener {editable->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.getSearchNews(editable.toString())
                    }
                }
            }
        }
    }
    override fun onClick(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article",article)
        }
        findNavController().navigate(
            R.id.action_searchNewsFragment_to_articleFragment,
            bundle
        )
    }
}