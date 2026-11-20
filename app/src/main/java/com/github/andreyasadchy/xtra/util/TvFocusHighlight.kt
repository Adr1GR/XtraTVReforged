package com.github.andreyasadchy.xtra.util

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.AbsListView
import android.widget.ScrollView
import android.webkit.WebView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.andreyasadchy.xtra.R
import com.google.android.material.card.MaterialCardView

private class SavedForeground(val drawable: Drawable?)

/**
 * Makes the D-pad "cursor" obvious on TV: the focused card is enlarged and raised, and any other
 * focused control gets a high-contrast white outline with a dark halo (visible on light, dark and
 * video backgrounds alike). Call once per activity after setContentView.
 */
fun Activity.setupTvFocusHighlight() {
    window.setupTvFocusHighlight()
}

/** Same as the Activity version, for the separate window of a dialog or bottom sheet. */
fun Window.setupTvFocusHighlight() {
    val decor = decorView
    if (decor.getTag(R.id.tvFocusListenerInstalled) != null) return
    decor.setTag(R.id.tvFocusListenerInstalled, true)
    val metrics = decor.resources.displayMetrics
    val screenArea = metrics.widthPixels.toLong() * metrics.heightPixels
    decor.viewTreeObserver.addOnGlobalFocusChangeListener { oldFocus, newFocus ->
        oldFocus?.let { clearFocusHighlight(it) }
        newFocus?.let { applyFocusHighlight(it, screenArea) }
    }
}

/** Highlights dialogs shown through DialogFragment (bottom sheets, preference dialogs...). */
fun FragmentActivity.setupTvDialogFocusHighlight() {
    supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            if (f is DialogFragment) {
                f.dialog?.window?.setupTvFocusHighlight()
            }
        }
    }, true)
}

private fun dp(view: View, value: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, view.resources.displayMetrics)

private fun applyFocusHighlight(view: View, screenArea: Long) {
    if (view is MaterialCardView) {
        view.animate().scaleX(1.05f).scaleY(1.05f).translationZ(dp(view, 12f)).setDuration(120).start()
        return
    }
    if (view is AbsListView) {
        // list rows are not focusable on their own: the list draws a selector for the active row
        if (view.getTag(R.id.tvFocusSavedForeground) == null) {
            view.setTag(R.id.tvFocusSavedForeground, SavedForeground(null))
            view.setSelector(createOutline(view))
            view.setDrawSelectorOnTop(true)
        }
        return
    }
    if (view is RecyclerView || view is ScrollView || view is NestedScrollView || view is ViewPager2 ||
        view is AbsListView || view is WebView || view is android.view.SurfaceView
    ) return
    if (view.width.toLong() * view.height > screenArea / 3) return
    if (view.getTag(R.id.tvFocusSavedForeground) != null) return
    val original = view.foreground
    view.setTag(R.id.tvFocusSavedForeground, SavedForeground(original))
    // some controls draw their icon as the foreground: keep it under the outline
    view.foreground = if (original != null) LayerDrawable(arrayOf(original, createOutline(view))) else createOutline(view)
}

private fun clearFocusHighlight(view: View) {
    if (view is MaterialCardView) {
        view.animate().scaleX(1f).scaleY(1f).translationZ(0f).setDuration(120).start()
        return
    }
    val saved = view.getTag(R.id.tvFocusSavedForeground) as? SavedForeground ?: return
    view.foreground = saved.drawable
    view.setTag(R.id.tvFocusSavedForeground, null)
}

private fun createOutline(view: View): Drawable {
    val radius = dp(view, 8f)
    val halo = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = radius
        setColor(Color.TRANSPARENT)
        setStroke(dp(view, 6f).toInt(), 0xAA000000.toInt())
    }
    val line = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = radius
        setColor(Color.TRANSPARENT)
        setStroke(dp(view, 3f).toInt(), Color.WHITE)
    }
    return LayerDrawable(arrayOf(halo, line)).apply {
        val inset = dp(view, 1.5f).toInt()
        setLayerInset(1, inset, inset, inset, inset)
    }
}
