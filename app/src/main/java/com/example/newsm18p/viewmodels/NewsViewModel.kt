package com.example.newsm18p.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsm18p.models.NewsResponse
import com.example.newsm18p.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
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

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if(response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }
}