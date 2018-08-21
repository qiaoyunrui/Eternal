package me.juhezi.notepad.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.juhezi.eternal.extension.isEmpty
import me.juhezi.eternal.global.formatTime
import me.juhezi.eternal.model.Article
import me.juhezi.notepad.R

class ArticleAdapter(list: List<Article>? = null) : RecyclerView.Adapter<ArticleHolder>() {

    private val mList = ArrayList<Article>()

    var onClickItemClickListener: ((Article, Int) -> Unit)? = null

    var emptyView: View? = null

    init {
        if (list != null) {
            mList.clear()
            mList.addAll(list)
        }
    }

    fun setArticles(list: List<Article>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun addArticle(article: Article, position: Int) {
        mList.add(position, article)
        notifyItemInserted(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.notepad_item_article, parent, false)
        return ArticleHolder(view)
    }

    override fun getItemCount(): Int {
        val result = mList.size
        if (result <= 0) {
            emptyView?.visibility = View.VISIBLE
        } else {
            emptyView?.visibility = View.GONE
        }
        return result
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        val article = mList[position]
        if (isEmpty(article.title)) {   // 标题为空
            holder.tvContent.visibility = View.GONE
            holder.tvTitle.text = article.content
        } else {
            holder.tvContent.visibility = View.VISIBLE
            holder.tvContent.text = article.content
            holder.tvTitle.text = article.title
        }
        holder.tvTime.text = formatTime(article.createTime.toLong())
        holder.itemView.setOnClickListener {
            onClickItemClickListener?.invoke(article, position)
        }
    }

}

class ArticleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvTitle: TextView
    val tvContent: TextView
    val tvTime: TextView

    init {
        tvTitle = itemView.findViewById(R.id.tv_item_article_title)
        tvContent = itemView.findViewById(R.id.tv_item_article_content)
        tvTime = itemView.findViewById(R.id.tv_item_article_time)
    }

}