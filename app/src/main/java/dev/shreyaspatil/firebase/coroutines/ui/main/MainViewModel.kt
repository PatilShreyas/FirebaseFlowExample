package dev.shreyaspatil.firebase.coroutines.ui.main

import androidx.lifecycle.ViewModel
import dev.shreyaspatil.firebase.coroutines.model.Post
import dev.shreyaspatil.firebase.coroutines.repository.PostsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainViewModel(private val repository: PostsRepository) : ViewModel() {

    fun getAllPosts() = repository.getAllPosts()

    fun addPost(post: Post) = repository.addPost(post)
}