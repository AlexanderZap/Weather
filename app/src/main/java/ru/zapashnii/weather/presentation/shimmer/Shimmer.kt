package ru.zapashnii.weather.presentation.shimmer

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import androidx.annotation.Px
import ru.zapashnii.weather.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Shimmer это объект, подробно описывающий все параметры конфигурации, доступные для [ShimmerConstraintLayout]
 */
class Shimmer internal constructor() {
    /** Форма Shimmer. По умолчанию используется ЛИНЕЙНЫЙ.  */
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [Shape.LINEAR, Shape.RADIAL])
    annotation class Shape {
        companion object {
            /** Линейный дает эффект отражения лучей.  */
            const val LINEAR = 0

            /** Радиальный дает эффект прожектора.  */
            const val RADIAL = 1
        }
    }

    /** Направление развертки мерцания.  */
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [Direction.LEFT_TO_RIGHT,
        Direction.TOP_TO_BOTTOM,
        Direction.RIGHT_TO_LEFT,
        Direction.BOTTOM_TO_TOP])
    annotation class Direction {
        companion object {
            const val LEFT_TO_RIGHT = 0
            const val TOP_TO_BOTTOM = 1
            const val RIGHT_TO_LEFT = 2
            const val BOTTOM_TO_TOP = 3
        }
    }

    val positions = FloatArray(COMPONENT_COUNT)
    val colors = IntArray(COMPONENT_COUNT)
    val bounds = RectF()

    @Direction
    var direction = Direction.LEFT_TO_RIGHT

    @ColorInt
    var highlightColor = Color.WHITE

    @ColorInt
    var baseColor = 0x4cffffff

    @Shape
    var shape = Shape.LINEAR
    var fixedWidth = 0
    var fixedHeight = 0
    var widthRatio = 1f
    var heightRatio = 1f
    var intensity = 0f
    var dropOff = 0.5f
    var tilt = 20f
    var clipToChildren = true
    var autoStart = true
    var alphaShimmer = false
    var repeatCount = ValueAnimator.INFINITE
    var repeatMode = ValueAnimator.RESTART
    var animationDuration = 1000L
    var repeatDelay: Long = 0
    var startDelay: Long = 0
    fun width(width: Int): Int {
        return if (fixedWidth > 0) fixedWidth else (widthRatio * width).roundToInt()
    }

    fun height(height: Int): Int {
        return if (fixedHeight > 0) fixedHeight else (heightRatio * height).roundToInt()
    }

    fun updateColors() {
        when (shape) {
            Shape.LINEAR -> {
                colors[0] = baseColor
                colors[1] = highlightColor
                colors[2] = highlightColor
                colors[3] = baseColor
            }
            Shape.RADIAL -> {
                colors[0] = highlightColor
                colors[1] = highlightColor
                colors[2] = baseColor
                colors[3] = baseColor
            }
            else -> {
                colors[0] = baseColor
                colors[1] = highlightColor
                colors[2] = highlightColor
                colors[3] = baseColor
            }
        }
    }

    fun updatePositions() {
        when (shape) {
            Shape.LINEAR -> {
                positions[0] = max((1f - intensity - dropOff) / 2f, 0f)
                positions[1] = max((1f - intensity - 0.001f) / 2f, 0f)
                positions[2] = min((1f + intensity + 0.001f) / 2f, 1f)
                positions[3] = min((1f + intensity + dropOff) / 2f, 1f)
            }
            Shape.RADIAL -> {
                positions[0] = 0f
                positions[1] = min(intensity, 1f)
                positions[2] = min(intensity + dropOff, 1f)
                positions[3] = 1f
            }
            else -> {
                positions[0] = max((1f - intensity - dropOff) / 2f, 0f)
                positions[1] = max((1f - intensity - 0.001f) / 2f, 0f)
                positions[2] = min((1f + intensity + 0.001f) / 2f, 1f)
                positions[3] = min((1f + intensity + dropOff) / 2f, 1f)
            }
        }
    }

    fun updateBounds(viewWidth: Int, viewHeight: Int) {
        val magnitude = max(viewWidth, viewHeight)
        val rad = Math.PI / 2f - Math.toRadians((tilt % 90f).toDouble())
        val hyp = magnitude / sin(rad)
        val padding = 3 * ((hyp - magnitude).toFloat() / 2f).roundToInt()
        bounds[-padding.toFloat(), -padding.toFloat(), (width(viewWidth) + padding).toFloat()] =
            (height(viewHeight) + padding).toFloat()
    }

    abstract class Builder<T : Builder<T>?> {
        val mShimmer = Shimmer()

        // Обходит неконтролируемый каст
        protected abstract val `this`: T

        /** Применяет все указанные параметры из [AttributeSet].  */
        fun consumeAttributes(context: Context, attrs: AttributeSet?): T {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ShimmerFrameLayout, 0, 0)
            return consumeAttributes(a)
        }

        open fun consumeAttributes(a: TypedArray): T {
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_clip_to_children)) {
                setClipToChildren(a.getBoolean(R.styleable.ShimmerConstraintLayout_s_clip_to_children,
                    mShimmer.clipToChildren))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_auto_start)) {
                setAutoStart(a.getBoolean(R.styleable.ShimmerConstraintLayout_s_auto_start,
                    mShimmer.autoStart))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_base_alpha)) {
                setBaseAlpha(a.getFloat(R.styleable.ShimmerConstraintLayout_s_base_alpha, 0.3f))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_highlight_alpha)) {
                setHighlightAlpha(a.getFloat(R.styleable.ShimmerConstraintLayout_s_highlight_alpha, 1f))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_duration)) {
                setDuration(a.getInt(R.styleable.ShimmerConstraintLayout_s_duration,
                    mShimmer.animationDuration.toInt()).toLong())
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_repeat_count)) {
                setRepeatCount(a.getInt(R.styleable.ShimmerConstraintLayout_s_repeat_count,
                    mShimmer.repeatCount))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_repeat_delay)) {
                setRepeatDelay(a.getInt(R.styleable.ShimmerConstraintLayout_s_repeat_delay,
                    mShimmer.repeatDelay.toInt()).toLong())
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_repeat_mode)) {
                setRepeatMode(a.getInt(R.styleable.ShimmerConstraintLayout_s_repeat_mode,
                    mShimmer.repeatMode))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_start_delay)) {
                setStartDelay(a.getInt(R.styleable.ShimmerConstraintLayout_s_start_delay,
                    mShimmer.startDelay.toInt()).toLong())
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_direction)) {
                when (a.getInt(R.styleable.ShimmerConstraintLayout_s_direction, mShimmer.direction)) {
                    Direction.LEFT_TO_RIGHT -> setDirection(Direction.LEFT_TO_RIGHT)
                    Direction.TOP_TO_BOTTOM -> setDirection(Direction.TOP_TO_BOTTOM)
                    Direction.RIGHT_TO_LEFT -> setDirection(Direction.RIGHT_TO_LEFT)
                    Direction.BOTTOM_TO_TOP -> setDirection(Direction.BOTTOM_TO_TOP)
                    else -> setDirection(Direction.LEFT_TO_RIGHT)
                }
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_shape)) {
                when (a.getInt(R.styleable.ShimmerConstraintLayout_s_shape, mShimmer.shape)) {
                    Shape.LINEAR -> setShape(Shape.LINEAR)
                    Shape.RADIAL -> setShape(Shape.RADIAL)
                    else -> setShape(Shape.LINEAR)
                }
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_dropoff)) {
                setDropoff(a.getFloat(R.styleable.ShimmerConstraintLayout_s_dropoff, mShimmer.dropOff))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_fixed_width)) {
                setFixedWidth(a.getDimensionPixelSize(R.styleable.ShimmerConstraintLayout_s_fixed_width,
                    mShimmer.fixedWidth))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_fixed_height)) {
                setFixedHeight(a.getDimensionPixelSize(R.styleable.ShimmerConstraintLayout_s_fixed_height,
                    mShimmer.fixedHeight))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_intensity)) {
                setIntensity(a.getFloat(R.styleable.ShimmerConstraintLayout_s_intensity, mShimmer.intensity))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_width_ratio)) {
                setWidthRatio(a.getFloat(R.styleable.ShimmerConstraintLayout_s_width_ratio,
                    mShimmer.widthRatio))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_height_ratio)) {
                setHeightRatio(a.getFloat(R.styleable.ShimmerConstraintLayout_s_height_ratio,
                    mShimmer.heightRatio))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_tilt)) {
                setTilt(a.getFloat(R.styleable.ShimmerConstraintLayout_s_tilt, mShimmer.tilt))
            }
            return `this`
        }

        /** Копирует конфигурацию уже построенного Shimmer в этот builder.  */
        fun copyFrom(other: Shimmer): T {
            setDirection(other.direction)
            setShape(other.shape)
            setFixedWidth(other.fixedWidth)
            setFixedHeight(other.fixedHeight)
            setWidthRatio(other.widthRatio)
            setHeightRatio(other.heightRatio)
            setIntensity(other.intensity)
            setDropoff(other.dropOff)
            setTilt(other.tilt)
            setClipToChildren(other.clipToChildren)
            setAutoStart(other.autoStart)
            setRepeatCount(other.repeatCount)
            setRepeatMode(other.repeatMode)
            setRepeatDelay(other.repeatDelay)
            setStartDelay(other.startDelay)
            setDuration(other.animationDuration)
            mShimmer.baseColor = other.baseColor
            mShimmer.highlightColor = other.highlightColor
            return `this`
        }

        /** Устанавливает направление развертки мерцания. См. [Direction].  */
        fun setDirection(@Direction direction: Int): T {
            mShimmer.direction = direction
            return `this`
        }

        /** Устанавливает форму мерцания. См [Shape].  */
        fun setShape(@Shape shape: Int): T {
            mShimmer.shape = shape
            return `this`
        }

        /** Устанавливает фиксированную ширину мерцания в пикселях..  */
        fun setFixedWidth(@Px fixedWidth: Int): T {
            require(fixedWidth >= 0) { "Получена недопустимая ширина: $fixedWidth" }
            mShimmer.fixedWidth = fixedWidth
            return `this`
        }

        /** Устанавливает фиксированную высоту мерцания в пикселях..  */
        fun setFixedHeight(@Px fixedHeight: Int): T {
            require(fixedHeight >= 0) { "Получена недопустимая высота: $fixedHeight" }
            mShimmer.fixedHeight = fixedHeight
            return `this`
        }

        /** Устанавливает коэффициент ширины мерцания, умноженный на общую ширину макета.  */
        fun setWidthRatio(widthRatio: Float): T {
            require(widthRatio >= 0f) { "Получено недопустимое соотношение ширины: $widthRatio" }
            mShimmer.widthRatio = widthRatio
            return `this`
        }

        /** Устанавливает соотношение высоты мерцания, умноженное на общую высоту макета.  */
        fun setHeightRatio(heightRatio: Float): T {
            require(heightRatio >= 0f) { "Получено недопустимое соотношение высоты: $heightRatio" }
            mShimmer.heightRatio = heightRatio
            return `this`
        }

        /** Устанавливает интенсивность мерцания. Чем больше значение, тем больше мерцание.  */
        fun setIntensity(intensity: Float): T {
            require(intensity >= 0f) { "Получено недопустимое значение интенсивности: $intensity" }
            mShimmer.intensity = intensity
            return `this`
        }

        /**
         * Устанавливает, как быстро спадает градиент мерцания. Чем больше значение, тем резче спад.
         */
        fun setDropoff(dropoff: Float): T {
            require(dropoff >= 0f) { "Получено недопустимое значение спадает градиента: $dropoff" }
            mShimmer.dropOff = dropoff
            return `this`
        }

        /** Устанавливает угол наклона мерцания в градусах.  */
        fun setTilt(tilt: Float): T {
            mShimmer.tilt = tilt
            return `this`
        }

        /**
         * Устанавливает базовую альфа, которая является альфой нижележащих дочерних элементов, в диапазоне [0, 1].
         */
        fun setBaseAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): T {
            val intAlpha = (clamp(0f, 1f, alpha) * 255f).toInt()
            mShimmer.baseColor = intAlpha shl 24 or (mShimmer.baseColor and 0x00FFFFFF)
            return `this`
        }

        /** Устанавливает уровень мерцания альфа в диапазоне [0, 1].  */
        fun setHighlightAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): T {
            val intAlpha = (clamp(0f, 1f, alpha) * 255f).toInt()
            mShimmer.highlightColor = intAlpha shl 24 or (mShimmer.highlightColor and 0x00FFFFFF)
            return `this`
        }

        /**
         * Устанавливает, будет ли мерцание закрепляться на дочернем содержимом или будет непрозрачно рисоваться поверх дочернего содержимого.
         */
        fun setClipToChildren(status: Boolean): T {
            mShimmer.clipToChildren = status
            return `this`
        }

        /** Устанавливает, будет ли автоматически запускаться мерцающая анимация.  */
        fun setAutoStart(status: Boolean): T {
            mShimmer.autoStart = status
            return `this`
        }

        /**
         * Устанавливает, как часто будет повторяться мерцающая анимация. См. [ ][android.animation.ValueAnimator.setRepeatCount].
         */
        fun setRepeatCount(repeatCount: Int): T {
            mShimmer.repeatCount = repeatCount
            return `this`
        }

        /**
         * Устанавливает, как будет повторяться мерцающая анимация. См [ ][android.animation.ValueAnimator.setRepeatMode].
         */
        fun setRepeatMode(mode: Int): T {
            mShimmer.repeatMode = mode
            return `this`
        }

        /** Устанавливает, как долго ждать между повторами мерцающей анимации.  */
        fun setRepeatDelay(millis: Long): T {
            require(millis >= 0) { "Получена отрицательная задержка повтора: $millis" }
            mShimmer.repeatDelay = millis
            return `this`
        }

        /** Устанавливает, как долго ждать начала мерцающей анимации.  */
        fun setStartDelay(millis: Long): T {
            require(millis >= 0) { "Получена отрицательная задержка старта: $millis" }
            mShimmer.startDelay = millis
            return `this`
        }

        /** Устанавливает время, необходимое мерцающей анимации для выполнения одного полного прохода.  */
        fun setDuration(millis: Long): T {
            require(millis >= 0) { "Получена отрицательная длительность: $millis" }
            mShimmer.animationDuration = millis
            return `this`
        }

        fun build(): Shimmer {
            mShimmer.updateColors()
            mShimmer.updatePositions()
            return mShimmer
        }

        companion object {
            private fun clamp(min: Float, max: Float, value: Float): Float {
                return min(max, max(min, value))
            }
        }
    }

    class AlphaHighlightBuilder : Builder<AlphaHighlightBuilder>() {
        override val `this`: AlphaHighlightBuilder
            get() = this

        init {
            mShimmer.alphaShimmer = true
        }
    }

    class ColorHighlightBuilder : Builder<ColorHighlightBuilder>() {
        /** Устанавливает цвет выделения для мерцания.  */
        fun setHighlightColor(@ColorInt color: Int): ColorHighlightBuilder {
            mShimmer.highlightColor = color
            return `this`
        }

        /** Устанавливает базовый цвет для мерцания.  */
        fun setBaseColor(@ColorInt color: Int): ColorHighlightBuilder {
            mShimmer.baseColor = mShimmer.baseColor and -0x1000000 or (color and 0x00FFFFFF)
            return `this`
        }

        override fun consumeAttributes(a: TypedArray): ColorHighlightBuilder {
            super.consumeAttributes(a)
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_base_color)) {
                setBaseColor(a.getColor(R.styleable.ShimmerConstraintLayout_s_base_color, mShimmer.baseColor))
            }
            if (a.hasValue(R.styleable.ShimmerConstraintLayout_s_highlight_color)) {
                setHighlightColor(a.getColor(R.styleable.ShimmerConstraintLayout_s_highlight_color,
                    mShimmer.highlightColor))
            }
            return `this`
        }

        override val `this`: ColorHighlightBuilder
            get() = this

        init {
            mShimmer.alphaShimmer = false
        }
    }

    companion object {
        private const val COMPONENT_COUNT = 4
    }
}