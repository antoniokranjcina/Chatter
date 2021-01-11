package com.ak.chatter.ui.main.fragments.main.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ak.chatter.R
import com.ak.chatter.data.model.NewPost
import com.ak.chatter.databinding.FragmentHomeBinding
import com.ak.chatter.ui.auth.AuthActivity
import com.ak.chatter.util.Constants.DATE
import com.ak.chatter.util.Constants.NEW_POSTS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val newPostCollectionRef = Firebase.firestore.collection(NEW_POSTS).orderBy(DATE, Query.Direction.DESCENDING)
    private val postsAdapter = PostsAdapter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        retrieveNewPosts()
        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = postsAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val action: NavDirections

        when (item.itemId) {
            R.id.item_settings -> {
                action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
                findNavController().navigate(action)
            }

            R.id.item_logout -> {
                Firebase.auth.signOut()
                navigateToAuthActivity()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun retrieveNewPosts() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = newPostCollectionRef.get().await()
            val posts = querySnapshot.toObjects<NewPost>()
            withContext(Dispatchers.Main) {
                postsAdapter.submitList(posts)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.localizedMessage, LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}