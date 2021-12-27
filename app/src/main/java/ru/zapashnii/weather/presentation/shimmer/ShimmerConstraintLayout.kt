package ru.zapashnii.weather.presentation.shimmer

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.databinding.BindingAdapter
import ru.zapashnii.weather.R


class ShimmerConstraintLayout @JvmOverloads constructor(
    context: Context,
    var attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0,
    val defStyleRes: Int = 0,
) : ConstraintLayout(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
) {

    private var shimmer: Shimmer? = null
    private val contentPaint = Paint()

    private val shimmerDrawableList = mutableListOf<ShimmerDrawable>()

    private var isShowShimmer = true
    private var isIgnoreAll = false

    init {
        setWillNotDraw(false)

        if (attrs == null) {
            this.shimmer = Shimmer.AlphaHighlightBuilder().build()
            setShimmer(shimmer)
        } else {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShimmerConstraintLayout, 0, 0)

            try {

                isIgnoreAll = typedArray.getBoolean(R.styleable.ShimmerConstraintLayout_shimmer_tools_ignore_all, false)
                val shimmerBuilder = if (isHasLayoutColoredAttrs(typedArray)) {
                    Shimmer.ColorHighlightBuilder()
                } else {
                    Shimmer.AlphaHighlightBuilder()
                }
                this.shimmer = shimmerBuilder.consumeAttributes(typedArray)?.build()
                setShimmer(shimmer)
            } finally {
                typedArray.recycle()
            }
        }
    }

    private fun isHasLayoutColoredAttrs(typedArray: TypedArray) =
        (typedArray.hasValue(R.styleable.ShimmerConstraintLayout_s_colored)
                && typedArray.getBoolean(R.styleable.ShimmerConstraintLayout_s_colored, false))

    fun setShimmer(shimmer: Shimmer?): ShimmerConstraintLayout {
        if (shimmer != null && shimmer.clipToChildren) {
            setLayerType(LAYER_TYPE_HARDWARE, contentPaint)
        } else {
            setLayerType(LAYER_TYPE_NONE, null)
        }
        return this
    }

    fun startShimmer() {
        for (drawable in shimmerDrawableList) {
            drawable.startShimmer()
        }
    }

    fun stopShimmer() {
        for (drawable in shimmerDrawableList) {
            drawable.stopShimmer()
        }
    }

    fun isShimmerStarted(): Boolean {
        return shimmerDrawableList.count { it.isShimmerStarted() } > 0
    }

    fun showShimmer(startShimmer: Boolean) {
        isShowShimmer = startShimmer
        if (startShimmer) {
            startShimmer()
        } else {
            stopShimmer()
        }
        invalidate()
    }

    fun hideShimmer() {
        if (!isShowShimmer) {
            return
        }

        stopShimmer()
        isShowShimmer = false
        invalidate()
    }

    fun isShimmerVisible() = isShowShimmer

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isIgnoreAll) return
        shimmerDrawableList.clear()
        children.iterator().forEach { view ->
            if (!isIgnoreShimmer(view)) {
                val drawable = ShimmerDrawable().apply {
                    val top = view.top
                    val left = view.left
                    val right = left + view.width
                    val bottom = top + view.height
                    setBounds(left, top, right, bottom)
                    setShimmer(shimmer)
                    callback = this@ShimmerConstraintLayout
                    maybeStartShimmer()
                }
                shimmerDrawableList.add(drawable)
            }
        }
    }

    private fun isIgnoreShimmer(view: View) = (view.layoutParams as? LayoutParams)?.ignore == true

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        for (drawable in shimmerDrawableList) {
            drawable.maybeStartShimmer()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopShimmer()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (isShowShimmer) {
            for (drawable in shimmerDrawableList) {
                drawable.draw(canvas)
            }
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || shimmerDrawableList.count { who == it } > 0
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is LayoutParams
    }

    override fun generateLayoutParams(attrs: AttributeSet?): ConstraintLayout.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): ConstraintLayout.LayoutParams {
        return LayoutParams(context, attrs)
    }

    class LayoutParams(context: Context, attrs: AttributeSet?) : ConstraintLayout.LayoutParams(context, attrs) {
        var ignore = false

        init {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ShimmerConstraintLayout_Layout)
            ignore = a.getBoolean(R.styleable.ShimmerConstraintLayout_Layout_layout_shimmer_ignore, false)
            a.recycle()
        }
    }
}

@BindingAdapter("bind:isShimmer")
fun setShimmer(view: ShimmerConstraintLayout, isShimmer: Boolean) {
    view.showShimmer(isShimmer)
}