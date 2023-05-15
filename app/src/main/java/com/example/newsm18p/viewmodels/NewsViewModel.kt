package com.example.newsm18p.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsm18p.NewsApp
import com.example.newsm18p.models.Article
import com.example.newsm18p.models.NewsResponse
import com.example.newsm18p.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.lang.ArithmeticException

class NewsViewModel(app: NewsApp,val newsRepository: NewsRepository) : AndroidViewModel(app) {

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
           /* //Change the state while making networking call to loading
            breakingNews.postValue(Resource.Loading())
            val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
            //Handle response state
            breakingNews.postValue(handleBreakingNewsResponse(response))*/
            safeBreakingNewsCall(countryCode)
        }
    }
    fun getSearchNews(searchQuery: String){
        viewModelScope.launch {
            //Change the state while making networking call to loading
            /*searchNews.postValue(Resource.Loading())
            val response = newsRepository.searchNews(searchQuery,searchNewsPage)
            //Handle response state
            searchNews.postValue(handleBreakingNewsResponse(response))*/
            safeSearchNewsCall(searchQuery)
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

    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try{
            if (hasInternetConnection()){
                val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else {
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network failure"))
                 else -> breakingNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        breakingNews.postValue(Resource.Loading())
        try{
            if (hasInternetConnection()){
                val response = newsRepository.searchNews(searchQuery,breakingNewsPage)
                searchNews.postValue(handleBreakingNewsResponse(response))
            }else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network failure"))
                 else -> searchNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }
    fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<NewsApp>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR)->true
                capabilities.hasTransport(TRANSPORT_ETHERNET)->true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI->true
                    TYPE_MOBILE->true
                    TYPE_ETHERNET->true
                    else->false
                }
            }
        }
        return false
    }
}