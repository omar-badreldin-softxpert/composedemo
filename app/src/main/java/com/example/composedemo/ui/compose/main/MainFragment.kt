package com.example.composedemo.ui.compose.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.composedemo.R
import com.example.composedemo.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    lateinit var binding: FragmentMainBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
    }
}