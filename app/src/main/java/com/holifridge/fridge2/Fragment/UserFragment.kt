package com.holifridge.fridge2.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.holifridge.fridge2.GlideApp
import com.holifridge.fridge2.databinding.FragmentUserBinding

class UserFragment : Fragment() {

    private var mBinding: FragmentUserBinding? = null
    private val binding get() = mBinding!!

    val user = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user.let {
            val name = user?.displayName
            val email = user?.email
            val photoUrl = user?.photoUrl

            GlideApp.with(this).load(photoUrl).centerCrop()
                .override(500).into(binding.pi)

            binding.nt.text = name.toString()
            binding.et.text = email.toString()
        }

    }
}