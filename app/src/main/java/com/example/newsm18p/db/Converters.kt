package com.example.newsm18p.db

import androidx.room.TypeConverter
import com.example.newsm18p.models.Source

class Converters {
    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name
    }

    @TypeConverter
    fun toSource(name:String): Source = Source(name,name)
}