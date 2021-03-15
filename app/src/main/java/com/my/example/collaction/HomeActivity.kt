package com.my.example.collaction

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.my.example.collaction.fragments.*
import com.my.example.collaction.interfaces.BaseOnClickListener
import com.my.example.collaction.models.User
import kotlinx.android.synthetic.main.activity_home.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


class HomeActivity : AppCompatActivity(), BaseOnClickListener, EditProfileFragment.Listener, KeyboardVisibilityEventListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.activity_home)
        initBottomNavBar()
        KeyboardVisibilityEvent.setEventListener(this, this)

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
                    mUser = snapshot.getValue(User::class.java)!!
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

    override fun getUser(): User = mUser

    override fun updateProfile(user: User) {
        val error = validateUser(user)
        if(error == null) {
            if(user.email == mUser.email) {
                val updatesMap = mutableMapOf<String, Any>()
                if(user.name != mUser.name) updatesMap["name"] = user.name
                if(user.username != mUser.username) updatesMap["username"] = user.username
                if(user.website != mUser.website) updatesMap["website"] = user.website
                if(user.bio != mUser.bio) updatesMap["bio"] = user.bio
                if(user.email != mUser.email) updatesMap["email"] = user.email
                if(user.phone != mUser.phone) updatesMap["phone"] = user.phone

                mDatabase.child("users").child(mAuth.currentUser.uid).updateChildren(updatesMap).addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
                        supportFragmentManager.popBackStack()
                    }
                    else {
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {

            }
        }
        else {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

    }

    private fun validateUser(user: User): String? =
        when {
            user.name.isEmpty() -> "Fill name please"
            user.username.isEmpty() -> "Fill username please"
            user.email.isEmpty() -> "Fill email please"
            else -> null
        }

    override fun onVisibilityChanged(isOpen: Boolean) {
        if(isOpen) {
            bottom_nav_view.visibility = View.GONE
        }
        else {
            bottom_nav_view.visibility = View.VISIBLE
        }
    }

}