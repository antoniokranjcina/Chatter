package com.ak.chatter.ui.main.fragments.main.newpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ak.chatter.data.model.NewPost
import com.ak.chatter.data.model.User
import com.ak.chatter.databinding.FragmentNewPostBinding
import com.ak.chatter.util.Constants.NEW_POSTS
import com.ak.chatter.util.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NewPostFragment : Fragment() {

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    private val usersCollectionRef = Firebase.firestore.collection(USERS)
    private val newPostCollectionRef = Firebase.firestore.collection(NEW_POSTS)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonSubmit.setOnClickListener {
                addNewPost()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addNewPost() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = usersCollectionRef.get().await()
                querySnapshot.documents.forEach {
                    val user = it.toObject<User>()!!
                    val uid = FirebaseAuth.getInstance().uid!!
                    if (user.uid == uid) {
                        val postId = it.id
                        val name = "${user.firstName} ${user.lastName}"
                        val description = binding.textInputLayoutDescription.editText?.text.toString()

                        val newPost = NewPost(
                            userId = uid,
                            postId = postId,
                            profilePhotoImage = "",
                            name = name,
                            postImage = "",
                            likesNumber = 0,
                            description = description,
                        )

                        try {
                            newPostCollectionRef.add(newPost).await()
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                            }
                        }
                        return@forEach
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private const val TAG = "NewPostFragment"
    }
}