package com.chatapp.ui.users

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.chatapp.adapter.OnItemClickCallback
import com.chatapp.adapter.UserAdapter
import com.chatapp.common.BindingFragment
import com.chatapp.data.remote.CreateChannelResult
import com.chatapp.ui.chat.ChatFragment.Companion.CHANNEL_ID_KEY
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentUsersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsersFragment : BindingFragment<FragmentUsersBinding>(), OnItemClickCallback {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentUsersBinding::inflate

    private val viewModel: UsersViewModel by viewModels()
    private var usersAdapter = UserAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        setupToolbar()
        initializeView()
        viewModel.getAllUsers()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createChannelEvent.collect {
                    when (it) {
                        is CreateChannelResult.Error -> {
                            showToast(it.error)
                        }
                        is CreateChannelResult.Success -> {
                            showToast(getString(R.string.channel_created))
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listOfUsers.collect {
                    usersAdapter.differ.submitList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect {
                    showToast(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createChannelId.collect {
                    findNavController().apply {
                        navigate(R.id.action_usersFragment_to_chatFragment,
                            Bundle().apply {
                                putString(CHANNEL_ID_KEY, it)
                            })
                    }
                }
            }
        }
    }

    override fun onItemClick(id: String) {
        viewModel.createNewChannel(id)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.users_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query!!.isEmpty()) {
                    viewModel.getAllUsers()
                } else {
                    viewModel.searchUser(query)
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            viewModel.getAllUsers()
            false
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    private fun initializeView() {
        binding.usersRecyclerView.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}