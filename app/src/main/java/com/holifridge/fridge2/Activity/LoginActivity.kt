package com.holifridge.fridge2.Activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.holifridge.fridge2.R
import com.holifridge.fridge2.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!
    private var auth: FirebaseAuth? = null

    //구글 로그인
    val TAG = "googleLogin"
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // 구글 로그인
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            getString(
                R.string.default_web_client_id
            )
        ).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.googleLogin.setOnClickListener {
            signIn()
        }

        binding.login.setOnClickListener {
            login(binding.loginEmail.text.toString(), binding.loginPassword.text.toString())
        }
        binding.gotoForgotPassword.setOnClickListener {
            startActivity(Intent(this, PasswordActivity::class.java))
        }
        binding.gotoSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startForResult.launch(signInIntent)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent: Intent = result.data!!
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.d(ContentValues.TAG, "Google sign in failed", e)
                }
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "로그인 성공")
                auth!!.currentUser
                loginSuccess()
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.exception)
            }
        }
    }

    private fun loginSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // 로그아웃하지 않을 시 자동 로그인 , 회원가입시 바로 로그인 됨
    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    // 로그인
    private fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                    moveMainPage(auth?.currentUser)
                } else {
                    if (task.exception != null)
                        Toast.makeText(baseContext, task.exception.toString(), Toast.LENGTH_SHORT)
                            .show()
                }
            }
        } else {
            Toast.makeText(baseContext, "이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 유저정보 넘겨주고 메인 액티비티 호출
    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}