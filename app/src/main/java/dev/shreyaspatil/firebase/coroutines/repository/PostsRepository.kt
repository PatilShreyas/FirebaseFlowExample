package dev.shreyaspatil.firebase.coroutines.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dev.shreyaspatil.firebase.coroutines.Constants
import dev.shreyaspatil.firebase.coroutines.State
import dev.shreyaspatil.firebase.coroutines.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

/**
 * Repository for the data of posts.
 * This will be a single source of data throughout the application.
 */
@ExperimentalCoroutinesApi
class PostsRepository {

    private val mPostsCollection =
        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_POST)

    /**
     * Returns Flow of [State] which retrieves all posts from cloud firestore collection.
     */
    fun getAllPosts() = flow<State<List<Post>>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mPostsCollection.get().await()
        val posts = snapshot.toObjects(Post::class.java)

        // Emit success state with data
        emit(State.success(posts))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
     * Adds post [post] into the cloud firestore collection.
     * @return The Flow of [State] which will store state of current action.
     */
    fun addPost(post: Post) = flow<State<DocumentReference>> {

        // Emit loading state
        emit(State.loading())

        val postRef = mPostsCollection.add(post).await()

        // Emit success state with post reference
        emit(State.success(postRef))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}
