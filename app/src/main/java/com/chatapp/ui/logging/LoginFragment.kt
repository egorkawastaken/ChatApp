package com.chatapp.ui.logging

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.chatapp.common.BindingFragment
import com.chatapp.data.remote.LoggingResult
import com.chatapp.util.Constants
import com.chatapp.util.extensions.navigateSafely
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    private val loginViewModel: LoggingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            setupUiConnectingState()
            loginViewModel.connectUser(binding.etUsername.text.toString())
            findNavController().navigateSafely(R.id.action_loginFragment_to_channelFragment)
        }

        binding.etUsername.apply {
            addTextChangedListener {
                binding.etUsername.error = null
            }

            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    setupUiConnectingState()
                    loginViewModel.connectUser(binding.etUsername.text.toString())
                    findNavController().navigateSafely(R.id.action_loginFragment_to_channelFragment)
                    true
                } else {
                    false
                }

            }
        }

        subscribeToEvents()
    }

    private fun setupUiConnectingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnConfirm.visibility = View.GONE
        binding.etUsername.visibility = View.GONE
        binding.tvLogin.visibility = View.GONE

    }

    private fun setupUiIdleState() {
        binding.progressBar.visibility = View.GONE
        binding.btnConfirm.visibility = View.VISIBLE
        binding.etUsername.visibility = View.VISIBLE
        binding.tvLogin.visibility = View.VISIBLE
    }

    private fun subscribeToEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loggingEvent.collect {
                    when(it) {
                        is LoggingResult.ErrorInputTooShort -> {
                            setupUiIdleState()
                            binding.etUsername.error = getString(R.string.error_username_too_short, Constants.MINIMUM_USERNAME_LENGTH)
                        }
                        is LoggingResult.Error -> {
                            setupUiIdleState()
                            showToast(it.message)
                        }
                        is LoggingResult.Success -> {
                            setupUiIdleState()
                            showToast("Welcome, ${it.name}.")
                        }
                    }
                }

            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }
}