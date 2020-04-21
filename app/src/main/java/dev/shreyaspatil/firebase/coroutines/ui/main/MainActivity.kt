package dev.shreyaspatil.firebase.coroutines.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dev.shreyaspatil.firebase.coroutines.State
import dev.shreyaspatil.firebase.coroutines.databinding.ActivityMainBinding
import dev.shreyaspatil.firebase.coroutines.model.Post
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    // Coroutine Scope
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, MainViewModelFactory())
            .get(MainViewModel::class.java)

        binding.buttonLoad.setOnClickListener(this)
        binding.buttonAdd.setOnClickListener(this)
    }

    private suspend fun loadPosts() {
        viewModel.getAllPosts().collect { state ->
            when (state) {
                is State.Loading -> {
                    showToast("Loading")
                }

                is State.Success -> {
                    val postText = state.data.joinToString("\n") {
                        "${it.postContent} ~ ${it.postAuthor}"
                    }

                    binding.textPostContent.text = postText
                }

                is State.Failed -> showToast("Failed! ${state.message}")
            }
        }
    }

    private suspend fun addPost(post: Post) {
        viewModel.addPost(post).collect { state ->
            when (state) {
                is State.Loading -> {
                    showToast("Loading")
                    binding.buttonAdd.isEnabled = false
                }

                is State.Success -> {
                    showToast("Posted")
                    binding.fieldPostContent.setText("")
                    binding.buttonAdd.isEnabled = true
                }

                is State.Failed -> {
                    showToast("Failed! ${state.message}")
                    binding.buttonAdd.isEnabled = true
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            binding.buttonLoad.id -> {
                uiScope.launch {
                    loadPosts()
                }
            }

            binding.buttonAdd.id -> {
                uiScope.launch {
                    addPost(
                        Post(
                            postContent = binding.fieldPostContent.text.toString(),
                            postAuthor = "Anonymous"
                        )
                    )
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
