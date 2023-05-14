package com.example.newsm18p.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsm18p.R
import com.example.newsm18p.adapter.NewsAdapter
import com.example.newsm18p.db.ArticleDatabase
import com.example.newsm18p.models.Article
import com.example.newsm18p.ui.NewsActivity
import com.example.newsm18p.viewmodels.NewsRepository
import com.example.newsm18p.viewmodels.NewsViewModel
import com.example.newsm18p.viewmodels.NewsViewModelFactory
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment:Fragment(R.layout.fragment_saved_news), NewsAdapter.OnClickListener {
    val viewModel: NewsViewModel by activityViewModels() {
        val repo = NewsRepository(ArticleDatabase(this.requireContext()))
        NewsViewModelFactory(repo)
    }
    lateinit var newsAdapter: NewsAdapter
    lateinit var rvList: RecyclerView
    val TAG = this.tag


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        val ItemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view,"Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }

        }
        ItemTouchHelper(ItemTouchHelperCallback).apply {
            attachToRecyclerView(rvList)
        }
        viewModel.getSavedNews().observe(viewLifecycleOwner,
            Observer {articles->
                newsAdapter.differ.submitList(articles)
            }
        )
    }
    private fun setupRecyclerView(view: View){
        newsAdapter = NewsAdapter(this)
        rvList = view.findViewById(R.id.rvSavedNews)
        rvList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onClick(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article",article)
        }
        findNavController().navigate(
            R.id.action_savedNewsFragment_to_articleFragment,
            bundle
        )
    }
}