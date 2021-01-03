package com.ak.chatter.ui.main.fragments.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.ak.chatter.data.model.NewPost
import com.ak.chatter.databinding.ItemPostsBinding

class PostsAdapter : ListAdapter<NewPost, PostsAdapter.PostsViewHolder>(POSTS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = ItemPostsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class PostsViewHolder(private val binding: ItemPostsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(newPost: NewPost) {
            binding.apply {
                imageViewProfilePic.load(newPost.profilePhotoImage) {
                    transformations(CircleCropTransformation())
                }
                textViewName.text = newPost.name
                imageViewPost.load(newPost.postImage)
                textViewLikes.text = newPost.likesNumber.toString()
                textViewDescription.text = newPost.description
            }
        }
    }

    companion object {
        private val POSTS_COMPARATOR = object : DiffUtil.ItemCallback<NewPost>() {
            override fun areItemsTheSame(oldItem: NewPost, newItem: NewPost) = oldItem.postId == newItem.postId
            override fun areContentsTheSame(oldItem: NewPost, newItem: NewPost) = oldItem == newItem
        }
    }
}