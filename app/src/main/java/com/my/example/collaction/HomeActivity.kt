package com.my.example.collaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initBottomNavBar()
        mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
//        val auth = FirebaseAuth.getInstance()
//        auth.signInWithEmailAndPassword("admin@gmail.com", "123123").addOnCompleteListener {
//            if(it.isSuccessful) {
//                Log.d("signIn", "success")
//            }
//            else {
//                Log.d("signIn", "failed")
//            }
//        }
    }

    override fun onStart() {
        super.onStart()

        if(mAuth.currentUser == null) {
            startActivity(Intent(this,  LoginActivity::class.java))
            finish()
        }
    }

    fun openOtherFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().addToBackStack(fragment.tag).replace(R.id.fragment, fragment).commit()
    }

    private fun initBottomNavBar() {
        bottom_nav_view.setTextVisibility(false)
        bottom_nav_view.enableItemShiftingMode(false)
        bottom_nav_view.enableShiftingMode(false)
        bottom_nav_view.enableAnimation(false)
        for(i in 0 until bottom_nav_view.menu.size()) {
            bottom_nav_view.setIconTintList(i, null)
        }
        bottom_nav_view.setOnNavigationItemSelectedListener {
            val nextFragment = when(it.itemId) {
                R.id.nav_item_home -> Fragment(R.layout.fragment_home)
                R.id.nav_item_search -> Fragment(R.layout.fragment_search)
                R.id.nav_item_share -> Fragment(R.layout.fragment_share)
                R.id.nav_item_likes -> Fragment(R.layout.fragment_likes)
                R.id.nav_item_profile -> Fragment(R.layout.fragment_profile)
                else -> {
                    Log.d("nextFragment", "null")
                }
            }

            if(nextFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment, nextFragment as Fragment).commit()
                true
            }
            else {
                false
            }
        }
    }
}