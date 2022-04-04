package com.example.fridge2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.fridge2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // 툴바 생성
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("냉장고에 뭐가있니") // 툴바 제목 설정


        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        // navi_menu.xml 에서 설정했던 각 아이템들의 id를 통해 알맞은 프래그먼트로 변경하게 한다.
        binding.bnvMain.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.main -> {
                        // 다른 프래그먼트 화면으로 이동하는 기능
                        val mainFragment = MainFragment()
                        supportFragmentManager.beginTransaction().replace(
                            R.id.fl_container,
                            mainFragment
                        ).commit()
                    }
                    R.id.user -> {
                        // 다른 프래그먼트 화면으로 이동하는 기능
                        val userFragment = UserFragment()
                        supportFragmentManager.beginTransaction().replace(
                            R.id.fl_container,
                            userFragment
                        ).commit()
                    }
                }
                true
            }
            selectedItemId = R.id.main
        }
    }
}