package com.chatapp.ui.logging

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.chatapp.common.BindingFragment
import com.example.chatapp.databinding.FragmentLoggingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoggingFragment : BindingFragment<FragmentLoggingBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoggingBinding::inflate


}