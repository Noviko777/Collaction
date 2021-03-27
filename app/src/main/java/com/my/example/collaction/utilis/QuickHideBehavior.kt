package com.my.example.collaction.utilis

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.my.example.collaction.R
import com.theartofdev.edmodo.cropper.CropImageView


class QuickHideBehavior : CoordinatorLayout.Behavior<View?> {
    /* Tracking direction of user motion */
    private var mScrollingDirection = 0
    private var mLastDirection = 0

    /* Tracking last threshold crossed */
    private var mScrollTrigger = 0

    /* Accumulated scroll distance */
    private var mScrollDistance = 0

    /* Distance threshold to trigger animation */
    private var mScrollThreshold = 0
    private var mAnimator: ObjectAnimator? = null
    private var isHide = false
    private var isStop = true
    private var isAnimStop = true
    private var mHeight: Int? = null

    //Required to instantiate as a default behavior
    constructor() {}

    //Required to attach behavior via XML
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a: TypedArray = context.getTheme()
                .obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        //Use half the standard action bar height
        mScrollThreshold = a.getDimensionPixelSize(0, 0) / 2
        a.recycle()
    }


    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        //Determine direction changes here

           if (dy > 0 && mScrollingDirection != DIRECTION_UP) {
               mScrollingDirection = DIRECTION_UP;
               mScrollDistance = 0;
           } else if (dy < 0 && mScrollingDirection != DIRECTION_DOWN) {
               mScrollingDirection = DIRECTION_DOWN;
               mScrollDistance = 0;
           }
        if(mLastDirection != mScrollingDirection && isAnimStop) {
            isStop = true
        }
        if(isAnimStop) {
            mLastDirection = mScrollingDirection
        }



    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        isStop = true
        mScrollDistance = 0
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        if(isStop && isAnimStop) {
            mScrollDistance += dyConsumed;
            if (mScrollDistance > mScrollThreshold
                    && mScrollTrigger != DIRECTION_UP) {
                //Hide the target view
                mScrollTrigger = DIRECTION_UP;
                restartAnimator(child, getTargetHideValue(coordinatorLayout, child));
            }
            else if (mScrollDistance < -mScrollThreshold * 20
                    && mScrollTrigger != DIRECTION_DOWN) {
                //Return the target view
                mScrollTrigger = DIRECTION_DOWN;
                restartAnimator(child, 0f);
            }
        }

    }

    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: View, target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        if (consumed && isStop && isAnimStop) {
            if (velocityY > 0 && mScrollTrigger != DIRECTION_UP) {
                mScrollTrigger = DIRECTION_UP;
                restartAnimator(child, getTargetHideValue(coordinatorLayout, child));
            }
//            else if (velocityY < 0 && mScrollTrigger != DIRECTION_DOWN) {
//                mScrollTrigger = DIRECTION_DOWN;
//                restartAnimator(child, 0f);
//            }
        }

        return false;
    }

    fun showImage(target: View) {
        if(isHide && isAnimStop) {

            mScrollingDirection = DIRECTION_DOWN
            mLastDirection = DIRECTION_DOWN
            mScrollTrigger = DIRECTION_DOWN
            isHide = false
            val anim = ResizeHeightAnimation(target, mHeight!!)
            anim.duration = 500
            anim.interpolator = FastOutSlowInInterpolator()
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    (target.parent as CoordinatorLayout).findViewById<CropImageView>(R.id.cropImageView).setImageUriAsync((target.parent as CoordinatorLayout).findViewById<CropImageView>(R.id.cropImageView).imageUri)
                    ObjectAnimator.ofFloat(target, "alpha", 0f, 1f).setDuration(500).start()
                    isAnimStop = true
                    isStop = true
                }

                override fun onAnimationStart(animation: Animation?) {
                    isAnimStop = false
                }

            })
            target.startAnimation(anim)
        }

    }
    /* Helper Methods */ //Helper to trigger hide/show animation
    private fun restartAnimator(target: View, value: Float) {
        isStop = false
        if (mAnimator != null) {
            mAnimator!!.cancel()
            mAnimator = null
        }
        if(isHide) {
            isHide = false
            val anim = ResizeHeightAnimation(target, mHeight!!)
            anim.duration = 500
            anim.interpolator = FastOutSlowInInterpolator()
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    (target.parent as CoordinatorLayout).findViewById<CropImageView>(R.id.cropImageView).setImageUriAsync((target.parent as CoordinatorLayout).findViewById<CropImageView>(R.id.cropImageView).imageUri)
                    ObjectAnimator.ofFloat(target, "alpha", 0f, 1f).setDuration(500).start()
                    isAnimStop = true
                }

                override fun onAnimationStart(animation: Animation?) {
                    isAnimStop = false
                }

            })
            target.startAnimation(anim)
        }else {
            isHide = true
            if(mHeight == null) {
                mHeight = target.height
            }
            val anim = ResizeHeightAnimation(target, 0)
            anim.duration = 500
            anim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            isAnimStop = true
                        }

                        override fun onAnimationStart(animation: Animation?) {
                            isAnimStop = false
                        }

                    })
            anim.interpolator = FastOutSlowInInterpolator()
            target.startAnimation(anim)
            ObjectAnimator.ofFloat(target, "alpha", 1f, 0f).setDuration(500).start()
        }



        Log.d("myRecycler", mScrollDistance.toString())
    }

    private fun getTargetHideValue(parent: ViewGroup, target: View): Float {
        return if (target is FrameLayout) -target.getHeight().toFloat() else 0f
    }

    class ResizeHeightAnimation(private val mView: View, private val mHeight: Int) : Animation() {
        private val mStartHeight: Int = mView.height

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            mView.layoutParams.height = mStartHeight + ((mHeight - mStartHeight) * interpolatedTime).toInt()
            mView.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }



    companion object {
        private const val DIRECTION_UP = 1
        private const val DIRECTION_DOWN = -1
    }
}