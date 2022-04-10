package com.example.fridge2.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.fridge2.Activity.JangActivity
import com.example.fridge2.R
import com.example.fridge2.databinding.FragmentMainBinding

class MainFragment : Fragment() {


    private var mBinding: FragmentMainBinding? = null
    private val binding get() = mBinding!!

    @Suppress("UNREACHABLE_CODE")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nj.setOnClickListener {
            activity?.let{
                val intent = Intent(context, JangActivity::class.java)
                startActivity(intent)
            }
        }
    }
}