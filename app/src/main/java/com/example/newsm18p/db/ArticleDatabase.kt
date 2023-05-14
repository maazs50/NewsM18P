package com.example.newsm18p.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsm18p.models.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao
    companion object{
        @Volatile
        private var instace: ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instace ?: synchronized(LOCK){
            instace ?: createDatabase(context).also{ instace  = it}
        }
        fun createDatabase(context: Context): ArticleDatabase{
            val roomDb = Room.databaseBuilder(context.applicationContext,
            ArticleDatabase::class.java,
            "article_db").allowMainThreadQueries()
                .build()
            return roomDb
        }
    }
}