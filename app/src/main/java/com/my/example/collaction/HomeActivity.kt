package com.my.example.collaction

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.*
import com.my.example.collaction.adapters.FeedPostAdapter
import com.my.example.collaction.adapters.UsersAdapter
import com.my.example.collaction.fragments.*
import com.my.example.collaction.interfaces.BaseFragmentListener
import com.my.example.collaction.interfaces.BaseOnClickListener
import com.my.example.collaction.interfaces.HomeListener
import com.my.example.collaction.models.FeedPost
import com.my.example.collaction.models.User
import com.my.example.collaction.utilis.FirebaseHelper
import com.my.example.collaction.utilis.ImagesGallery
import com.my.example.collaction.utilis.showToast
import com.my.example.collaction.views.PasswordDialog
import kotlinx.android.synthetic.main.activity_home.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(), BaseOnClickListener, BaseFragmentListener, SearchFragment.Listener,
    EditProfileFragment.Listener, KeyboardVisibilityEventListener, PasswordDialog.Listener,
        ShareFragment.Listener, PublishPostFragment.Listener, HomeListener, UsersAdapter.Listener, FeedPostAdapter.Listener{

    private val TAKE_PICTURE_REQUEST_CODE: Int = 111
    private val CROP_PICTURE_REQUEST_CODE: Int = 112
    private val MY_READ_PERMISSION_CODE: Int = 101

    private val mFirebase = FirebaseHelper(this)
    private var mUsersEventListener: ValueEventListener? = null
    private var mPostsEventListener: ValueEventListener? = null

    private var mUser: User = User()
    private lateinit var mPendingUser: User
    private lateinit var mPosts: List<String>
    private var mUsers: List<User> = emptyList()

    private var mFeedPosts: MutableList<FeedPost>? = null

    private lateinit var mCallback: (photo: String?) -> Unit
    private var mFeedPostsCallback: ((posts: List<FeedPost>) -> Unit)? = null
    private var mAllUsersCallback: ((posts: List<User>) -> Unit)? = null
    private lateinit var mReloadPhotoCallback: () -> Unit

    private lateinit var mPostBitmap: Bitmap

    private var isPostsWantLoaded: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.activity_home)
        initBottomNavBar()
        KeyboardVisibilityEvent.setEventListener(this, this)

        mFirebase.auth.addAuthStateListener {
            if(it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        if(mFirebase.auth.currentUser != null) {
            mFirebase.database.child("users").child(mFirebase.auth.currentUser!!.uid).addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    mUser = snapshot.getValue(User::class.java)!!.copy(uid = snapshot.key!!)
                    if(isPostsWantLoaded) {
                        getAllFeedPosts(mFeedPostsCallback!!)
                        isPostsWantLoaded = false
                    }
                }

            })

            mFirebase.database.child("images").child(mFirebase.auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    mPosts = snapshot.children.map { it.getValue(String::class.java)!! }

                }

            })
        }



        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment, HomeFragment()).commit()
        }


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
                if(nextFragment is SearchFragment)
                    supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.fragment, nextFragment).commit()
                else
                    supportFragmentManager.beginTransaction().replace(R.id.fragment, nextFragment).commit()

                true
            }
            else {
                false
            }
        }
    }

    override fun signOut() = mFirebase.auth.signOut()
    override fun getUser(): User = mUser
    override fun getCurrentUid(): String = mFirebase.auth.currentUser!!.uid


    override fun updateProfile(user: User) {
        val error = validateUser(user)
        if(error == null) {
            mPendingUser = user
            if(mPendingUser.email == mUser!!.email) {
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
            showToast(error)
        }

    }

    override fun loadPhoto(onLoaded: (photo: String?) -> Unit) {
        takeCameraPicture()
        mCallback = onLoaded
    }

    private fun takeCameraPicture() {
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
                val photoUri = Uri.parse(data!!.getStringExtra("result"))
                mFirebase.uploadUserPhoto(photoUri) {
                    if(photoUri != null) {
                        mFirebase.updateUserPhoto(it.toString()) {
                            mCallback(it)
                            showToast("Saved image")
                            contentResolver.delete(photoUri, null, null)
                        }
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
        if(user.name != mUser!!.name) updatesMap["name"] = user.name
        if(user.username != mUser!!.username) updatesMap["username"] = user.username
        if(user.website != mUser!!.website) updatesMap["website"] = user.website
        if(user.bio != mUser!!.bio) updatesMap["bio"] = user.bio
        if(user.email != mUser!!.email) updatesMap["email"] = user.email
        if(user.phone != mUser!!.phone) updatesMap["phone"] = user.phone

        mFirebase.updateUser(updatesMap) {
            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
            supportFragmentManager.popBackStack()
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
            mFirebase.onConfirmPassword(mUser!!.email, mPendingUser.email, password) {
                updateUser(mPendingUser)
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

    override fun getUserPosts(): List<String> = mPosts
    override fun getAllFeedPosts(postsCallBack: (List<FeedPost>) -> Unit) {
        mFeedPostsCallback = postsCallBack
        mFirebase.database.child("feed-posts").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(!mUser.uid.isNullOrEmpty()) {
                    mFeedPosts = ArrayList<FeedPost>()
                    mFeedPosts!!.addAll(snapshot.child(mFirebase.auth.currentUser!!.uid).children.map { it.getValue(FeedPost::class.java)!!.copy(id = it.key!!) }.toTypedArray())
                    mUser!!.follows.keys.forEach {
                        mFeedPosts!!.addAll(snapshot.child(it).children.map { it.getValue(FeedPost::class.java)!!.copy(id = it.key!!) }.toTypedArray())
                    }
                    mFeedPosts = mFeedPosts!!.map { it }.sortedByDescending { it.timeStampDate() }.toMutableList()
                    if(mFeedPostsCallback != null) {
                        mFeedPostsCallback!!(mFeedPosts!!)
                    }
                }
                else {
                    isPostsWantLoaded = true
                }
            }

        })

    }

    override fun detachFeedPosts() {
        mFeedPostsCallback = null
    }

    override fun openPostFragment(bitmap: Bitmap) {
        mPostBitmap = bitmap
        supportFragmentManager.beginTransaction().add(R.id.fragment, PublishPostFragment()).addToBackStack(null).commit()
    }

    override fun getPostBitmap(): Bitmap = mPostBitmap

    override fun shareImage(caption: String) {

        val photoUri = getImageUri(this, mPostBitmap)
        val uid = mFirebase.auth.currentUser!!.uid
        fun finishShareFragment() {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction().replace(R.id.fragment, ProfileFragment()).commit()
            bottom_nav_view.menu.get(bottom_nav_view.menu.size() - 1).isChecked = true
        }
        mFirebase.storage.child("users").child(uid)
                .child("images").child(photoUri!!.lastPathSegment!!).putFile(photoUri).addOnCompleteListener {
                    contentResolver.delete(photoUri, null, null)
                    if(it.isSuccessful) {
                        it.result!!.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                            if(it.isSuccessful) {
                                val photoUrl = it.result.toString()
                                mFirebase.database.child("images").child(uid).push()
                                        .setValue(photoUrl).addOnCompleteListener {
                                            mFirebase.database.child("feed-posts").child(uid).push()
                                                    .setValue(FeedPost(uid = uid, username = mUser!!.username, photo = mUser!!.photo,
                                                    image = photoUrl, caption = caption)).addOnCompleteListener {
                                                        finishShareFragment()
                                                    }
                                        }
                            }
                            else {
                                showToast(it.exception!!.message)
                                finishShareFragment()
                            }
                        }
                    }
                    else {
                        showToast(it.exception!!.message)
                        finishShareFragment()
                    }
                }

    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(),
                inImage,
                "Title",
                null
        )
        return Uri.parse(path)
    }

    override fun getAllUsers(onSuccess: (List<User>) -> Unit) {

           mFirebase.database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onCancelled(error: DatabaseError) {
               }

               override fun onDataChange(snapshot: DataSnapshot) {
                   val allUsers = snapshot.children.map { it.getValue(User::class.java)!!.copy(uid = it.key!!) }
                   val (userList, otherUsersList) = allUsers.partition { it.uid ==  mFirebase.auth.currentUser!!.uid}
                   mUsers = otherUsersList
                   mAllUsersCallback!!(mUsers)
               }

           })

        mAllUsersCallback = onSuccess
        if(mAllUsersCallback != null) {
            mAllUsersCallback!!(mUsers)
        }
    }

    override fun detachAllUsers() {
       // mFirebase.database.removeEventListener(mUsersEventListener!!)
        mUsersEventListener = null
        mAllUsersCallback = null
    }

    override fun follow(uid: String) {
        val setFollow = mFirebase.database.child("users").child(mFirebase.auth.currentUser!!.uid)
                .child("follows").child(uid).setValue(true)
        val setFollower = mFirebase.database.child("users").child(uid).child("followers")
                .child(mFirebase.auth.currentUser!!.uid).setValue(true)

        setFollow.continueWithTask {setFollower}.addOnCompleteListener {
            if(it.isSuccessful) {
                if(mPostsEventListener != null)
                    mFirebase.database.removeEventListener(mPostsEventListener!!)
            } else {
                showToast(it.exception!!.message)
            }
        }
    }

    override fun unFollow(uid: String) {
        val setFollow = mFirebase.database.child("users").child(mFirebase.auth.currentUser!!.uid)
                .child("follows").child(uid).removeValue()
        val setFollower = mFirebase.database.child("users").child(uid).child("followers")
                .child(mFirebase.auth.currentUser!!.uid).removeValue()

        setFollow.continueWithTask {setFollower}.addOnCompleteListener {
            if(it.isSuccessful) {
                if(mPostsEventListener != null)
                    mFirebase.database.removeEventListener(mPostsEventListener!!)
            } else {
                showToast(it.exception!!.message)
            }
        }
    }

    override fun toggleLike(uid: String, feedId: String, onSuccess: (post: FeedPost) -> Unit) {
        val ref = mFirebase.database.child("feed-posts").child(uid).child(feedId)
        ref.child("likes").child(mFirebase.auth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    ref.child("likes").child(mFirebase.auth.currentUser!!.uid).removeValue()
                } else {
                    ref.child("likes").child(mFirebase.auth.currentUser!!.uid).setValue(true)
                }
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        onSuccess(snapshot.getValue(FeedPost::class.java)!!.copy(id = snapshot.key!!))
                    }

                })

            }

        })
    }





}