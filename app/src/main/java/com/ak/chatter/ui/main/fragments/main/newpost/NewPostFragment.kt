package com.ak.chatter.ui.main.fragments.main.newpost

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.ak.chatter.data.model.NewPost
import com.ak.chatter.data.model.User
import com.ak.chatter.databinding.FragmentNewPostBinding
import com.ak.chatter.util.Constants.DOT_JPG
import com.ak.chatter.util.Constants.IMAGE_TYPE
import com.ak.chatter.util.Constants.NEW_POSTS
import com.ak.chatter.util.Constants.POSTS_STORAGE_REF
import com.ak.chatter.util.Constants.REQUEST_CODE
import com.ak.chatter.util.Constants.USERS
import com.ak.chatter.util.KeyboardBehaviour
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*


class NewPostFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    private val usersCollectionRef = Firebase.firestore.collection(USERS)
    private val newPostCollectionRef = Firebase.firestore.collection(NEW_POSTS)
    private val storageRef = Firebase.storage.reference.child(POSTS_STORAGE_REF)

    private var imageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && intent!!.data != null) {
            imageUri = intent.data
            binding.imageViewPost.load(imageUri)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            binding.imageViewPost.id -> {
                selectImageFromGallery()
            }
            binding.buttonSubmit.id -> {
                addImageToStorageAndPostToFireStore()
            }
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.apply {
            type = IMAGE_TYPE
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun addImageToStorageAndPostToFireStore() {
        showLoading()

        if (imageUri != null) {
            val imageName = System.currentTimeMillis().toString() + Random().nextInt() + DOT_JPG
            val imageFileRef = storageRef.child(imageName)

            imageFileRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    val result = it.metadata!!.reference!!.downloadUrl
                    result
                        .addOnSuccessListener { uri ->
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val querySnapshot = usersCollectionRef.get().await()
                                    querySnapshot.documents.forEach { firebaseUser ->
                                        val user = firebaseUser.toObject<User>()!!
                                        val uid = FirebaseAuth.getInstance().uid!!
                                        if (user.uid == uid) {
                                            addPostToFireStore(user, uri)
                                            return@forEach
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Log.e(TAG, "addNewPost: ${e.localizedMessage}")
                                        Toast.makeText(requireContext(), e.localizedMessage, LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                        .addOnFailureListener { imageException ->
                            hideLoading()
                            Log.e(TAG, imageException.localizedMessage!!)
                            Toast.makeText(requireContext(), imageException.localizedMessage, LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener {
                    hideLoading()
                    Log.e(TAG, "addNewPost: ${it.localizedMessage}")
                    Toast.makeText(requireContext(), it.localizedMessage, LENGTH_LONG).show()
                }
        }
    }

    private fun addPostToFireStore(user: User, uri: Uri) {
        val description = binding.textInputLayoutDescription.editText?.text.toString()

        val newPost = NewPost(
            user = user,
            profilePhotoImage = "",
            postImage = uri.toString(),
            likesNumber = 0,
            description = description,
        )

        newPostCollectionRef.add(newPost)
            .addOnSuccessListener {
                val newPostId = it.id
                newPost.idPostDocument = newPostId

                newPostCollectionRef.document(newPostId).set(newPost)
                    .addOnSuccessListener {
                        Log.d(TAG, "addPostToFireStore: new post successfully added.")
                        val action = NewPostFragmentDirections.actionNewPostFragmentToHomeFragment()
                        findNavController().navigate(action)
                    }
                    .addOnFailureListener { newPostIdException ->
                        hideLoading()
                        Log.e(TAG, "addPostToFireStore: ${newPostIdException.localizedMessage}")
                        Toast.makeText(requireContext(), newPostIdException.localizedMessage, LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { newPostException ->
                hideLoading()
                Log.e(TAG, "addPostToFireStore: ${newPostException.localizedMessage}")
                Toast.makeText(requireContext(), newPostException.localizedMessage, LENGTH_LONG).show()
            }
    }

    private fun setOnClickListeners() {
        binding.apply {
            imageViewPost.setOnClickListener(this@NewPostFragment)
            buttonSubmit.setOnClickListener(this@NewPostFragment)
        }
    }

    private fun showLoading() {
        KeyboardBehaviour.closeKeyboard(requireActivity())
        binding.apply {
            progressBar.visibility = VISIBLE
            textInputLayoutDescription.visibility = GONE
            imageViewPost.visibility = GONE
            buttonSubmit.visibility = GONE
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressBar.visibility = GONE
            textInputLayoutDescription.visibility = VISIBLE
            imageViewPost.visibility = VISIBLE
            buttonSubmit.visibility = VISIBLE
        }
    }

    companion object {
        private const val TAG = "NewPostFragment"
    }
}