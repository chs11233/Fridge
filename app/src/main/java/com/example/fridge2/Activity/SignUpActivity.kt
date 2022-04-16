package com.example.fridge2.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fridge2.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private var mBinding: ActivitySignUpBinding? = null
    private val binding get() = mBinding!!

    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        binding.pCB.setOnClickListener {
            if (binding.pET.length() > 0 && binding.pCET.length() > 0) {
                if (binding.pET.length() > 5 && binding.pCET.length() > 5) {
                    if (binding.pET.text.toString() == binding.pCET.text.toString()) {
                        Toast.makeText(this, "비밀번호 일치", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "비밀번호를 6자리 이상  설정해주세요.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.sUB.setOnClickListener {
            signUp(binding.eET.text.toString(), binding.nET.text.toString())
        }
    }

    private fun signUp(email: String, password: String) {
        if (binding.eET.text.toString().isNotBlank() && binding.pET.text.toString().isNotBlank() &&
            binding.pCET.text.toString().isNotBlank() && binding.nET.text.toString().isNotBlank()
        ) {
            mAuth = FirebaseAuth.getInstance()
            mAuth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this, OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입이 되었습니다.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}