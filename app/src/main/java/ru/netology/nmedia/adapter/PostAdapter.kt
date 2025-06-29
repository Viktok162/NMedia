package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.quantityWritingRule
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface OnInteractorListener{
    fun onLike(post:Post)
    fun onRemove(post:Post)
    fun onEdit(post:Post)
    fun onShare(post: Post)
    fun onEye(post: Post)
}

class PostAdapter(
    private val onInteractorListener: OnInteractorListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractorListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractorListener: OnInteractorListener
): RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) = with(binding){
        author.text = post.author
        content.text = post.content
        published.text = post.published
        liked.text = quantityWritingRule(post.likes)
        shared.text = quantityWritingRule(post.shares)
        looked.text = quantityWritingRule(post.looks)
        heart.setImageResource(
            if (post.likeByMe)R.drawable.heart_red_24dp else R.drawable.heart_white_24dp
        )

        heart.setOnClickListener {
            onInteractorListener.onLike(post)
        }

        share.setOnClickListener {
            onInteractorListener.onShare(post)
        }

        eye.setOnClickListener {
            onInteractorListener.onEye(post)
        }

        menu.setOnClickListener {
            PopupMenu(it.context, it).apply{
                inflate(R.menu.post_options)
                setOnMenuItemClickListener { item ->
                    when(item.itemId){
                        R.id.remove -> {
                            onInteractorListener.onRemove(post)
                            true
                        }
                        R.id.edit -> {
                            onInteractorListener.onEdit(post)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }
    }
}

object PostDiffCallBack: DiffUtil.ItemCallback<Post>(){

    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem == newItem
    }
}