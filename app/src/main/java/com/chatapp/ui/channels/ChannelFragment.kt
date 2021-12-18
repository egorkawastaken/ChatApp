package com.chatapp.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.chatapp.common.BindingFragment
import com.chatapp.data.remote.CreateChannelResult
import com.chatapp.ui.chat.ChatFragment.Companion.CHANNEL_ID_KEY
import com.chatapp.util.extensions.navigateSafely
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentChannelBinding
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelFragment : BindingFragment<FragmentChannelBinding>() {

    /** We bind @param [channelViewModel] to an activity cause DialogFragment also has access to viewModel. But on dismissing
     * of dialog fragment viewmodel can be destroyed so network call can be dismissed as well. So we bind
     * viewmodel to an Activity that viewmodel isn't destroyed on dismissing od dialog fragment*/

    private val viewModel: ChannelViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChannelBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = viewModel.getCurrentUser()
        if (user == null) {
            findNavController().popBackStack()
            return
        }

       setupChannels()

        binding.channelListHeaderView.setOnUserAvatarClickListener {
            viewModel.logout()
            findNavController().popBackStack()
        }


        binding.channelListHeaderView.setOnActionButtonClickListener{
            findNavController().navigateSafely(R.id.action_channelFragment_to_usersFragment)
        }

        binding.channelListView.setChannelItemClickListener{
            findNavController().navigateSafely(R.id.action_channelFragment_to_chatFragment,
            Bundle().apply {
                putString(CHANNEL_ID_KEY, it.cid)
            })
        }

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
    }

    private fun setupChannels() {
        /** filters for channels */
        val factory = ChannelListViewModelFactory(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.`in`("members", listOf(viewModel.getCurrentUser()!!.id))
            ),
            sort = ChannelListViewModel.DEFAULT_SORT,
            limit = 30
        )

        val channelViewModel: ChannelListViewModel by viewModels { factory }
        val channelHeaderViewModel: ChannelListHeaderViewModel by viewModels()

        channelViewModel.bindView(binding.channelListView, viewLifecycleOwner)
        channelHeaderViewModel.bindView(binding.channelListHeaderView, viewLifecycleOwner)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}