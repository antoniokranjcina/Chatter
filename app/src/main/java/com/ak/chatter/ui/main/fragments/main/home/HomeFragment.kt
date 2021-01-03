package com.ak.chatter.ui.main.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ak.chatter.data.model.NewPost
import com.ak.chatter.databinding.FragmentHomeBinding
import com.ak.chatter.util.Constants.NEW_POST
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val newPostCollectionRef = Firebase.firestore.collection(NEW_POST)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appCompat = requireActivity() as AppCompatActivity
        appCompat.setSupportActionBar(binding.toolbarMain)

        retrieveNewPosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun retrieveNewPosts() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = newPostCollectionRef.get().await()
            querySnapshot.documents.forEach {
                val newPost = it.toObject<NewPost>()
                Log.d(TAG, "retrieveNewPosts: $newPost")
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveNewPosts: ${e.localizedMessage}")
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}