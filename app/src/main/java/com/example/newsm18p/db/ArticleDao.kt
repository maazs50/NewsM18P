package com.example.newsm18p.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsm18p.models.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrInsert(article: Article): Long

    //Here we will use live data so no need of coroutines
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}