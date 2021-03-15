package com.my.example.collaction

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.my.example.collaction.fragments.login.EmailFragment
import com.my.example.collaction.fragments.login.LoginFragment
import com.my.example.collaction.fragments.login.NameFragment
import com.my.example.collaction.interfaces.BaseLoginOnClickListener
import com.my.example.collaction.interfaces.EmailOnClickListener
import com.my.example.collaction.interfaces.LoginOnClickListener
import com.my.example.collaction.interfaces.NameOnClickListener
import com.my.example.collaction.models.User
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener, BaseLoginOnClickListener, LoginOnClickListener, NameOnClickListener, EmailOnClickListener {

    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: DatabaseReference
    private var mName: String = ""
    private var mUsername: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        KeyboardVisibilityEvent.setEventListener(this, this)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance("https://collaction-18109-default-rtdb.europe-west1.firebasedatabase.app/").reference

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.scroll_view,LoginFragment()).commit()
        }

    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        if(isOpen) {
            scroll_view.scrollTo(0, scroll_view.bottom)
        }
        else {
            scroll_view.scrollTo(0, scroll_view.top)
        }

    }


    private fun validate(email: String, password: String) = email.isNotEmpty() && password.isNotEmpty()


    override fun signIn(email: String, password: String) {
        if(validate(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else {
            Toast.makeText(this, "Please fill email and password", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSignUp() {
        supportFragmentManager.beginTransaction().replace(R.id.scroll_view, NameFragment()).addToBackStack(null).commit()
    }

    override fun onNext(name: String, username: String) {
        if(name.isNotEmpty() && username.isNotEmpty()) {
            mName = name
            mUsername = username
            supportFragmentManager.beginTransaction().replace(R.id.scroll_view, EmailFragment()).addToBackStack(null).commit()
        }
        else {
            Toast.makeText(this, "Please fill name and username", Toast.LENGTH_SHORT).show()
        }
    }

    override fun signUp(email: String, password: String) {
        if(validate(email, password)) {
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                if(it.isSuccessful) {
                    if(it.result?.signInMethods?.isEmpty() == true) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                            if(it.isSuccessful) {
                                val user = User(name = mName, username = mUsername, email = email)
                                mDatabase.child("users").child(it.result!!.user.uid).setValue(user).addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        startActivity(Intent(this, HomeActivity::class.java))
                                        finish()
                                    }
                                    else {
                                        Toast.makeText(this, "Failed fill profile", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            } else {
                                Toast.makeText(this, "Failed registration", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else {
                        Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        else {
            Toast.makeText(this, "Please fill email and password", Toast.LENGTH_SHORT).show()
        }
    }

    override fun goToSignIn() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction().replace(R.id.scroll_view, LoginFragment()).commit()
    }


}