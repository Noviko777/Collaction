package com.my.example.collaction

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.my.example.collaction.fragments.*
import com.my.example.collaction.interfaces.BaseFragmentListener
import com.my.example.collaction.interfaces.BaseOnClickListener
import com.my.example.collaction.interfaces.HomeListener
import com.my.example.collaction.models.User
import com.my.example.collaction.utilis.ImagesGallery
import com.my.example.collaction.views.PasswordDialog
import kotlinx.android.synthetic.main.activity_home.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(), BaseOnClickListener, BaseFragmentListener,
    EditProfileFragment.Listener, KeyboardVisibilityEventListener, PasswordDialog.Listener,
        ShareFragment.Listener, HomeListener{

    private val TAKE_PICTURE_REQUEST_CODE: Int = 111
    private val CROP_PICTURE_REQUEST_CODE: Int = 112
    private val MY_READ_PERMISSION_CODE: Int = 101


    private lateinit var mImageUri: Uri
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: StorageReference
    private lateinit var mDatabase: DatabaseReference

    private lateinit var mUser: User
    private lateinit var mPendingUser: User

    private val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    private lateinit var mCallback: (photo: String?) -> Unit
    private lateinit var mReloadPhotoCallback: () -> Unit




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.activity_home)
        initBottomNavBar()
        KeyboardVisibilityEvent.setEventListener(this, this)

        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance().reference
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val decorView = window.decorView
        if (hasFocus) {
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
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
            mPendingUser = user
            if(mPendingUser.email == mUser.email) {
                updateUser(mPendingUser)
            }
            else {
                val view = this.currentFocus
                if (view != null) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                PasswordDialog().show(supportFragmentManager, "password_dialog")
            }
        }
        else {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

    }

    override fun loadPhoto(onLoaded: (photo: String?) -> Unit) {
        takeCameraPictire()
        mCallback = onLoaded
    }

    private fun takeCameraPictire() {
        val intent = Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.setType("*/*")
//        if(intent.resolveActivity(packageManager) != null) {
//            val imageFile = createImageFile()
//            mImageUri = FileProvider.getUriForFile(this, "com.my.example.collaction.fileprovider",
//            imageFile)
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
//
//        }
        startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == TAKE_PICTURE_REQUEST_CODE) {

                intent = Intent(this, CropImageActivity::class.java)
                intent.putExtra("imageUri", data!!.data!!.toString())
                startActivityForResult(intent, CROP_PICTURE_REQUEST_CODE)
            }
            else if(requestCode == CROP_PICTURE_REQUEST_CODE) {
                val uid = mAuth.currentUser!!.uid
                val photoUri = Uri.parse(data!!.getStringExtra("result"))
                mStorage.child("users/$uid/photo").putFile(photoUri).addOnCompleteListener {
                    if(it.isSuccessful) {
                        it.result!!.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                            if(it.isSuccessful) {
                                val photo = it.result.toString()
                                mDatabase.child("users/$uid/photo").setValue(it.result.toString()).addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        mCallback(photo)
                                        Toast.makeText(this, "Saved image", Toast.LENGTH_SHORT).show()
                                        contentResolver.delete(photoUri, null, null);
                                    }
                                    else {
                                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                    }
                    else {
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateUser(user: User): String? =
        when {
            user.name.isEmpty() -> "Fill name please"
            user.username.isEmpty() -> "Fill username please"
            user.email.isEmpty() -> "Fill email please"
            else -> null
        }



    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any>()
        if(user.name != mUser.name) updatesMap["name"] = user.name
        if(user.username != mUser.username) updatesMap["username"] = user.username
        if(user.website != mUser.website) updatesMap["website"] = user.website
        if(user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if(user.email != mUser.email) updatesMap["email"] = user.email
        if(user.phone != mUser.phone) updatesMap["phone"] = user.phone

        mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
                supportFragmentManager.popBackStack()
            }
            else {
                Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        if(isOpen) {
            bottom_nav_view.visibility = View.GONE
        }
        else {
            bottom_nav_view.visibility = View.VISIBLE
        }
    }

    override fun onConfirmPassword(password: String) {
        if(!password.isNullOrEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
                if(it.isSuccessful) {
                    mAuth.currentUser!!.updateEmail(mPendingUser.email).addOnCompleteListener {
                        if(it.isSuccessful) {
                            updateUser(mPendingUser)
                        }
                        else {
                            Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun popFragment() {
        supportFragmentManager.popBackStack()
    }


    private fun checkGalleryPermissions(): Boolean {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), MY_READ_PERMISSION_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_READ_PERMISSION_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read external storage permission granted", Toast.LENGTH_SHORT).show()
                mReloadPhotoCallback()
            } else {
                Toast.makeText(this, "Read external storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getGalleryImages(tryRequest: () -> Unit): List<String> {
        if(checkGalleryPermissions()) {
            return ImagesGallery.listOfImages(this)
        }
        mReloadPhotoCallback = tryRequest

        return ArrayList<String>()
    }



}