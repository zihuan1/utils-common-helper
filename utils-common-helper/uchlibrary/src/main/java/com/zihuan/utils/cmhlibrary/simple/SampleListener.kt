package com.zihuan.utils.cmhlibrary.simple

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewPropertyAnimator
import android.widget.EditText
import androidx.viewpager.widget.ViewPager
import com.zihuan.utils.cmhlibrary.OnSoftKeyBoardChangeListener
import com.zihuan.utils.cmhlibrary.SoftKeyBoardListener

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
fun ViewPager.onPageScrolled(action: (Int, Float, Int) -> Unit) {
    addOnPageChangeListener(object : SamplePageChangeListener() {
        override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
            action(position, offset, offsetPixels)
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

/**
 * EditText简洁监听
 */
open class TextWatcherSample : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {}
}

fun EditText.textChangeListener(before: (String) -> Unit = {}, onText: (String) -> Unit = {}, after: (String) -> Unit = {}) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            before(s.toString())
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onText(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
            after(s.toString())
        }
    })
}

fun EditText.beforeTextChanged(action: (String) -> Unit) {
    textChangeListener(before = action)
}

fun EditText.onTextChanged(action: (String) -> Unit) {
    textChangeListener(onText = action)
}

fun EditText.afterTextChanged(action: (String) -> Unit) {
    textChangeListener(after = action)
}

fun ValueAnimator.onEnd(action: (Boolean) -> Unit): ValueAnimator {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
            action(isReverse)
        }
    })
    return this
}

fun ValueAnimator.onStart(action: (Boolean) -> Unit): ValueAnimator {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
            action(isReverse)
        }
    })
    return this
}

/**
 * 动画监听
 */
fun ValueAnimator.onUpdate(action: (ValueAnimator) -> Unit): ValueAnimator {
    addUpdateListener {
        action(it)
    }
    return this
}


fun ViewPropertyAnimator.onUpdate(action: (ValueAnimator) -> Unit) {
    setUpdateListener {
        action(it)
    }
}

/**
 * 监听键盘显示和隐藏
 */
fun Activity.softKeyBoard(action: (Boolean) -> Unit) {
    SoftKeyBoardListener.setListener(this, object : OnSoftKeyBoardChangeListener {
        override fun keyBoardShow(height: Int) {
            action(true)
        }

        override fun keyBoardHide(height: Int) {
            action(false)
        }
    })
}
