package com.my.example.collaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.my.example.collaction.fragments.*
import com.my.example.collaction.interfaces.BaseOnClickListener
import com.my.example.collaction.models.User
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), BaseOnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initBottomNavBar()
        mAuth = FirebaseAuth.getInstance()
        mAuth.addAuthStateListener {
            if(it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        mDatabase = FirebaseDatabase.getInstance("https://collaction-18109-default-rtdb.europe-west1.firebasedatabase.app/").reference
        if(mAuth.currentUser != null) {
            mDatabase.child("users").child(mAuth.currentUser!!.uid).addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)!!
                }

            })
        }



        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment, HomeFragment()).commit()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    fun openOtherFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null).commit()
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
            val nextFragment: Fragment = when(it.itemId) {
                R.id.nav_item_home -> HomeFragment()
                R.id.nav_item_search -> SearchFragment()
                R.id.nav_item_share -> ShareFragment()
                R.id.nav_item_likes -> LikesFragment()
                R.id.nav_item_profile -> ProfileFragment()
                else -> {
                    HomeFragment()
                }
            }

            if(nextFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment, nextFragment).commit()
                true
            }
            else {
                false
            }
        }
    }

    override fun signOut() {
        mAuth.signOut()
    }

    override fun getUser(): User = user
}