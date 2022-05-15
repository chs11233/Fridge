package com.holifridge.fridge2.Fragment

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.holifridge.fridge2.Activity.*
import com.holifridge.fridge2.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var isFabOpen = false // Fab 버튼 default는 닫혀있음

    private var mBinding: FragmentMainBinding? = null
    private val binding get() = mBinding!!

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
            activity?.let {
                val intent = Intent(context, JangActivity::class.java)
                startActivity(intent)
            }
        }
        binding.nd.setOnClickListener {
            activity?.let {
                val intent = Intent(context, DongActivity::class.java)
                startActivity(intent)
            }
        }
        binding.so.setOnClickListener {
            activity?.let {
                val intent = Intent(context, SilonActivity::class.java)
                startActivity(intent)
            }
        }

        setFABClickEvent()
    }

    private fun setFABClickEvent() {
        // 플로팅 버튼 클릭시 애니메이션 동작 기능
        binding.fabMain.setOnClickListener {
            toggleFab()
        }

        binding.fabAdd.setOnClickListener {
            activity?.let {
                val intent = Intent(context, AddActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun toggleFab() {
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION, 45f, 0f).apply { start() }
        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY", -180f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION, 0f, 45f).apply { start() }
        }

        isFabOpen = !isFabOpen
    }
}