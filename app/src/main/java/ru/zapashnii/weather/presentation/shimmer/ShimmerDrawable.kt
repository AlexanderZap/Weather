package ru.zapashnii.weather.presentation.shimmer

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import kotlin.math.sqrt
import kotlin.math.tan

class ShimmerDrawable : Drawable() {
    private val mUpdateListener = ValueAnimator.AnimatorUpdateListener { invalidateSelf() }

    private val mShimmerPaint = Paint()
    private val mDrawRect = Rect()
    private val mShaderMatrix = Matrix()

    private var mValueAnimator: ValueAnimator? = null

    private var mShimmer: Shimmer? = null

    init {
        mShimmerPaint.isAntiAlias = true
    }

    fun setShimmer(shimmer: Shimmer?) {
        mShimmer = shimmer
        if (mShimmer != null) {
            mShimmerPaint.xfermode =
                PorterDuffXfermode(if (mShimmer!!.alphaShimmer) PorterDuff.Mode.DST_IN else PorterDuff.Mode.SRC)
        }
        updateShader()
        updateValueAnimator()
        invalidateSelf()
    }

    fun getShimmer(): Shimmer? {
        return mShimmer
    }

    /** Запускает анимацию мерцания. */
    fun startShimmer() {
        if (mValueAnimator != null && !isShimmerStarted() && callback != null) {
            mValueAnimator!!.start()
        }
    }

    /** Останавливает анимацию мерцания. */
    fun stopShimmer() {
        if (mValueAnimator != null && isShimmerStarted()) {
            mValueAnimator!!.cancel()
        }
    }

    /** Возвращает, была ли запущена анимация мерцания. */
    fun isShimmerStarted(): Boolean {
        return mValueAnimator != null && mValueAnimator!!.isStarted
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mDrawRect.set(bounds)
        updateShader()
        maybeStartShimmer()
    }

    override fun draw(canvas: Canvas) {
        if (mShimmer == null || mShimmerPaint.shader == null) {
            return
        }

        val tiltTan = tan(Math.toRadians(mShimmer!!.tilt.toDouble())).toFloat()
        val translateHeight: Float = mDrawRect.height() + tiltTan * mDrawRect.width()
        val translateWidth: Float = mDrawRect.width() + tiltTan * mDrawRect.height()
        var dx = 0f
        var dy = 0f
        val animatedValue = mValueAnimator?.animatedValue as? Float ?: 0f
        when (mShimmer!!.direction) {
            Shimmer.Direction.LEFT_TO_RIGHT -> {
                dx = offset(-translateWidth, translateWidth, animatedValue)
                dy = 0f
            }
            Shimmer.Direction.RIGHT_TO_LEFT -> {
                dx = offset(translateWidth, -translateWidth, animatedValue)
                dy = 0f
            }
            Shimmer.Direction.TOP_TO_BOTTOM -> {
                dx = 0f
                dy = offset(-translateHeight, translateHeight, animatedValue)
            }
            Shimmer.Direction.BOTTOM_TO_TOP -> {
                dx = 0f
                dy = offset(translateHeight, -translateHeight, animatedValue)
            }
        }

        mShaderMatrix.reset()
        mShaderMatrix.setRotate(mShimmer!!.tilt, mDrawRect.width() / 2f, mDrawRect.height() / 2f)
        mShaderMatrix.postTranslate(dx, dy)
        mShimmerPaint.shader.setLocalMatrix(mShaderMatrix)
        canvas.drawRect(mDrawRect, mShimmerPaint)
    }

    override fun setAlpha(alpha: Int) {
        // Вместо этого измените объект Shimmer, который вы передаете
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // Вместо этого измените объект Shimmer, который вы передаете
    }

    @Override
    override fun getOpacity(): Int {
        return if (mShimmer != null && (mShimmer!!.clipToChildren || mShimmer!!.alphaShimmer)) PixelFormat.TRANSLUCENT
        else PixelFormat.OPAQUE
    }

    private fun offset(start: Float, end: Float, percent: Float): Float {
        return start + (end - start) * percent
    }

    private fun updateValueAnimator() {
        if (mShimmer == null) {
            return
        }

        val started = if (mValueAnimator != null) {
            mValueAnimator!!.cancel()
            mValueAnimator!!.removeAllUpdateListeners()
            mValueAnimator!!.isStarted
        } else {
            false
        }

        mValueAnimator =
            ValueAnimator.ofFloat(0f, 1f + (mShimmer!!.repeatDelay / mShimmer!!.animationDuration).toFloat())
        mValueAnimator?.interpolator = LinearInterpolator()
        mValueAnimator?.repeatMode = mShimmer!!.repeatMode
        mValueAnimator?.startDelay = mShimmer!!.startDelay
        mValueAnimator?.repeatCount = mShimmer!!.repeatCount
        mValueAnimator?.duration = mShimmer!!.animationDuration + mShimmer!!.repeatDelay
        mValueAnimator?.addUpdateListener(mUpdateListener)
        if (started) {
            mValueAnimator!!.start()
        }
    }

    fun maybeStartShimmer() {
        if (mValueAnimator != null
            && !mValueAnimator!!.isStarted
            && mShimmer != null
            && mShimmer!!.autoStart
            && callback != null
        ) {
            mValueAnimator!!.start()
        }
    }

    private fun updateShader() {
        val bounds = bounds
        val boundsWidth = bounds.width()
        val boundsHeight = bounds.height()
        if (boundsWidth == 0 || boundsHeight == 0 || mShimmer == null) {
            return
        }
        val width = mShimmer!!.width(boundsWidth)
        val height = mShimmer!!.height(boundsHeight)

        val shader: Shader = when (mShimmer!!.shape) {
            Shimmer.Shape.LINEAR -> {
                val vertical =
                    mShimmer!!.direction == Shimmer.Direction.TOP_TO_BOTTOM || mShimmer!!.direction == Shimmer.Direction.BOTTOM_TO_TOP
                val endX = if (vertical) 0 else width
                val endY = if (vertical) height else 0
                LinearGradient(0f,
                    0f,
                    endX.toFloat(),
                    endY.toFloat(),
                    mShimmer!!.colors,
                    mShimmer!!.positions,
                    Shader.TileMode.CLAMP)
            }
            Shimmer.Shape.RADIAL -> {
                RadialGradient(width / 2f, height / 2f, (width.coerceAtLeast(height) / sqrt(2f)),
                    mShimmer!!.colors,
                    mShimmer!!.positions,
                    Shader.TileMode.CLAMP)
            }
            else -> {
                val vertical =
                    mShimmer!!.direction == Shimmer.Direction.TOP_TO_BOTTOM || mShimmer!!.direction == Shimmer.Direction.BOTTOM_TO_TOP
                val endX = if (vertical) 0 else width
                val endY = if (vertical) height else 0
                LinearGradient(0f,
                    0f,
                    endX.toFloat(),
                    endY.toFloat(),
                    mShimmer!!.colors,
                    mShimmer!!.positions,
                    Shader.TileMode.CLAMP)
            }
        }

        mShimmerPaint.shader = shader
    }
}