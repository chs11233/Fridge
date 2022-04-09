package com.example.fridge2.Activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.fridge2.Fragment.MainFragment
import com.example.fridge2.R
import com.example.fridge2.Fragment.UserFragment
import com.example.fridge2.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    private var auth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null

    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // 툴바 생성
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.navi_menu) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(true) // 툴바에 타이틀 보이게
        supportActionBar?.title = "냉장고에 뭐가있니"

        //NavigationDrawer 생성
        drawerLayout = binding.drawerLayout

        //NavigationDrawer 화면의 이벤트 처리 생성
        navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this) //navigation 리스너

        //Fragment간 이동
        //이게 오류뜸
        // navController = findNavController(R.id.nav_host_fragment)

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

        // 로그아웃
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    //NavigationView
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                var logoutIntent = Intent(this, LoginActivity::class.java)
                logoutIntent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(logoutIntent)

                auth?.signOut()
                FirebaseAuth.getInstance().signOut()
                googleSignInClient?.signOut()
            }
        }
        return false
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
}