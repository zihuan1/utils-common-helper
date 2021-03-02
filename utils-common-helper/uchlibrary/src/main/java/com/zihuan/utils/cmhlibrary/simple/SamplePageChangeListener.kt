package com.zihuan.utils.cmhlibrary.simple

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.ViewPropertyAnimator
import androidx.viewpager.widget.ViewPager

open class SamplePageChangeListener : ViewPager.OnPageChangeListener {
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}

/**
 * ViewPager简洁监听
 */
fun ViewPager.onPageScrolled(action: (Int,Float,Int) -> Unit) {
    addOnPageChangeListener(object : SamplePageChangeListener() {
        override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
            action(position,offset,offsetPixels)
        }
    })
}
fun ViewPager.onPageSelected(action: (position: Int) -> Unit) {
    addOnPageChangeListener(object : SamplePageChangeListener() {
        override fun onPageSelected(position: Int) {
            action(position)
        }
    })
}
fun ViewPager.onPageScrollStateChanged(action: (Int) -> Unit) {
    addOnPageChangeListener(object : SamplePageChangeListener() {
        override fun onPageScrollStateChanged(state: Int) {
            action(state)
        }
    })
}


fun Animator.animStart(action: (animation: Animator) -> Unit): Animator {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            super.onAnimationStart(animation)
            action(animation)
        }
    })
    return this
}

fun ValueAnimator.animUpdate(action: (ValueAnimator) -> Unit): ValueAnimator {
    addUpdateListener {
        action(it)
    }
    return this
}

fun Animator.animEnd(action: (animation: Animator) -> Unit): Animator {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            action(animation)
        }
    })
    return this
}

fun ViewPropertyAnimator.animStart(action: (animation: Animator) -> Unit) {
    setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            super.onAnimationStart(animation)
            action(animation)
        }
    })
}

fun ViewPropertyAnimator.animUpdate(action: (ValueAnimator) -> Unit) {
    setUpdateListener {
        action(it)
    }
}

fun ViewPropertyAnimator.animEnd(action: (animation: Animator) -> Unit) {
    setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            action(animation)
        }
    })
}
