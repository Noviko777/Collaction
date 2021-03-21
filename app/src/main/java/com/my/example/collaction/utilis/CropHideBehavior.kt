package com.my.example.collaction.utilis

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Interpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.appbar.AppBarLayout
import com.my.example.collaction.R


class CropHideBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>(context, attrs) {
    private val INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()

    private val ANIM_STATE_NONE = 0
    private val ANIM_STATE_HIDING = 1
    private val ANIM_STATE_SHOWING = 2

    private var animState = ANIM_STATE_NONE

    private var mDySinceDirectionChange = 0

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0 && mDySinceDirectionChange < 0
                || dy < 0 && mDySinceDirectionChange > 0) {
            // We detected a direction change -- reset our cumulative delta Y
            mDySinceDirectionChange = 0;
        }

        mDySinceDirectionChange += dy;

        if (mDySinceDirectionChange > child.getHeight() && !isOrWillBeHidden(child) && mDySinceDirectionChange > 0) {
            hide(child);
            Log.d("ShareRes", "Hide")
        } else if (mDySinceDirectionChange < 0 && !isOrWillBeShown(child)) {
            show(child);
            Log.d("ShareRes", "Show")

        }
        Log.d("ShareRes", mDySinceDirectionChange.toString())
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        Log.d("ShareRes", "STOP")
    }

    private fun isOrWillBeHidden(view: View): Boolean {
        return if (view.visibility == View.VISIBLE) {
            animState === ANIM_STATE_HIDING
        } else {
            animState !== ANIM_STATE_SHOWING
        }
    }

    private fun isOrWillBeShown(view: View): Boolean {
        return if (view.visibility != View.VISIBLE) {
            animState === ANIM_STATE_SHOWING
        } else {
            animState !== ANIM_STATE_HIDING
        }
    }

    private fun hide(view: View) {
        view.animate().cancel()
        view.animate()
                .translationY(view.height.toFloat())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    private var isCanceled = false
                    override fun onAnimationStart(animation: Animator?) {
                        animState = ANIM_STATE_HIDING
                        isCanceled = false
                        view.visibility = View.VISIBLE
                        val recyclerView = (view.parent as CoordinatorLayout).findViewById<View>(R.id.recyclerview_gallery_images)
                        val params: CoordinatorLayout.LayoutParams = recyclerView.layoutParams as CoordinatorLayout.LayoutParams
                        params.behavior = null
                        recyclerView.requestLayout()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        isCanceled = true
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        animState = ANIM_STATE_NONE
                        if (!isCanceled) {
                            view.visibility = View.INVISIBLE

                        }
                    }
                })
    }

    private fun show(view: View) {
        view.animate().cancel()
        view.animate()
                .translationY(0f)
                .setInterpolator(INTERPOLATOR)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animator: Animator) {
                        animState = ANIM_STATE_SHOWING
                        view.visibility = View.VISIBLE

                    }

                    override fun onAnimationEnd(animator: Animator) {
                        animState = ANIM_STATE_NONE
                        val recyclerView = (view.parent as CoordinatorLayout).findViewById<View>(R.id.recyclerview_gallery_images)
                        val params: CoordinatorLayout.LayoutParams = recyclerView.layoutParams as CoordinatorLayout.LayoutParams
                        params.behavior = AppBarLayout.ScrollingViewBehavior()
                        recyclerView.requestLayout()
                    }
                })
    }
}