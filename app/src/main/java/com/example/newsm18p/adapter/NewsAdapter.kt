package com.example.newsm18p.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsm18p.R
import com.example.newsm18p.models.Article

class NewsAdapter(val listener: OnClickListener): RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        private val image: ImageView = itemView.findViewById(R.id.ivArticleImage)
        private val tvSource: TextView = itemView.findViewById(R.id.tvSource)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvPublishedAt: TextView = itemView.findViewById(R.id.tvPublishedAt)
        init {
            itemView.setOnClickListener(this)
        }
        fun bind(article: Article){
            Glide.with(itemView).load(article.urlToImage).placeholder(R.drawable.ic_launcher_background).into(image)
            tvSource.text = article.source.name
            tvTitle.text = article.title
            tvPublishedAt.text = article.publishedAt
        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onClick(differ.currentList[position])
            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsAdapter.ArticleViewHolder {
        return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_article_preview,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: NewsAdapter.ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    interface OnClickListener{
        fun onClick(article: Article)
    }
}