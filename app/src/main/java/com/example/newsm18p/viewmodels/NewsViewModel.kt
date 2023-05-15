package com.example.newsm18p.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsm18p.models.Article
import com.example.newsm18p.models.NewsResponse
import com.example.newsm18p.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.ArithmeticException

class NewsViewModel(val newsRepository: NewsRepository) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    init{
        getBreakingNews("in")
    }
    fun getBreakingNews(countryCode: String){
        viewModelScope.launch {
            //Change the state while making networking call to loading
            breakingNews.postValue(Resource.Loading())
            val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
            //Handle response state
            breakingNews.postValue(handleBreakingNewsResponse(response))
        }
    }
    fun getSearchNews(searchQuery: String){
        viewModelScope.launch {
            //Change the state while making networking call to loading
            searchNews.postValue(Resource.Loading())
            val response = newsRepository.searchNews(searchQuery,searchNewsPage)
            //Handle response state
            searchNews.postValue(handleBreakingNewsResponse(response))
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if(response.isSuccessful){
            response.body()?.let { resultResponse->
                breakingNewsPage++
                if (breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    //Handle Search news response
    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if(response.isSuccessful){
            response.body()?.let {resultResponse->
                searchNewsPage++
                if (searchNewsResponse == null){
                    //Executed for the 1st time
                    searchNewsResponse  = resultResponse
                } else {
                    //Executed from the 2nd time
                    /*val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)*/
                }
                return Resource.Success(searchNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) =
        viewModelScope.launch {
            newsRepository.insertUpdate(article)
        }

    fun getSavedNews() = newsRepository.getSavedArticles()

    fun deleteArticle(article: Article) =
        viewModelScope.launch {
            newsRepository.deleteArticle(article)
        }
}