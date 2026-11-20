package com.github.andreyasadchy.xtra.util

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ScrollView
import android.webkit.WebView
import androidx.core.widget.NestedScrollView
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
    val metrics = resources.displayMetrics
    val screenArea = metrics.widthPixels.toLong() * metrics.heightPixels
    window.decorView.viewTreeObserver.addOnGlobalFocusChangeListener { oldFocus, newFocus ->
        oldFocus?.let { clearFocusHighlight(it) }
        newFocus?.let { applyFocusHighlight(it, screenArea) }
    }
}

private fun dp(view: View, value: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, view.resources.displayMetrics)

private fun applyFocusHighlight(view: View, screenArea: Long) {
    if (view is MaterialCardView) {
        view.animate().scaleX(1.05f).scaleY(1.05f).translationZ(dp(view, 12f)).setDuration(120).start()
        return
    }
    if (view is RecyclerView || view is ScrollView || view is NestedScrollView || view is ViewPager2 ||
        view is AbsListView || view is WebView || view is android.view.SurfaceView
    ) return
    if (view.width.toLong() * view.height > screenArea / 3) return
    if (view.getTag(R.id.tvFocusSavedForeground) != null) return
    view.setTag(R.id.tvFocusSavedForeground, SavedForeground(view.foreground))
    view.foreground = createOutline(view)
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
