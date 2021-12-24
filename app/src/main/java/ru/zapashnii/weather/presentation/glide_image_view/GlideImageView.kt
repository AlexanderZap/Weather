package ru.zapashnii.weather.presentation.glide_image_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import ru.zapashnii.weather.R

/** Кастомный ImageView для загрузки изображение через Glide */
class GlideImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    /**
     * Загрузить изображение
     *
     * @param uri       [Uri] изображенич
     * @param options   Настройки для Glide
     */
    fun loadImage(uri: Uri, options: Options = Options()) {
        val requestListener = getRequestListener(options.onError, options.onImageLoaded)

        alpha = 0.3F

        Glide.with(this)
            .load(uri)
            .apply(options.requestOptions)
            .listener(requestListener)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(options.placeholderIcon)
            .error(options.errorIcon)
            .into(this)
    }

    /**
     * Загрузить изображение
     *
     * @param url       ссылка на изображение
     * @param options   Настройки для Glide
     */
    fun loadImage(url: String, options: Options = Options()) {
        val requestListener = getRequestListener(options.onError, options.onImageLoaded)

        alpha = 0.3F

        Glide.with(this)
            .load(url)
            .apply(options.requestOptions)
            .listener(requestListener)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(options.placeholderIcon)
            .error(options.errorIcon)
            .into(this)
    }

    /**
     * Загрузить изображение
     *
     * @param bitmap    [Bitmap] изображения
     * @param options   Настройки для Glide
     */
    fun setBitmap(bitmap: Bitmap?, options: Options) {
        val requestListener = getRequestListener(options.onError, options.onImageLoaded)

        alpha = 0.3F

        Glide.with(this)
            .load(bitmap)
            .apply(options.requestOptions)
            .listener(requestListener)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(options.placeholderIcon)
            .error(options.errorIcon)
            .into(this)

    }

    private fun getRequestListener(
        onError: (errorMessage: String) -> Unit,
        onImageLoaded: () -> Unit
    ): RequestListener<Drawable> {
        return object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                alpha = 0.3F
                onError(e?.localizedMessage ?: "No glide exception message")
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                alpha = 0.87F
                onImageLoaded()
                return false
            }
        }
    }

    /**
     * Настройки для Glide
     *
     * @property requestOptions
     * @property placeholderIcon        Изображение показываемое при загрузке
     * @property errorIcon              Изображение показываемое при неудачной   загрузке
     * @property onError                CallBack при неудачной загрузке
     * @property onImageLoaded          CallBack при удачной загрузке
     */
    data class Options(
        val requestOptions: RequestOptions = RequestOptions.centerCropTransform(),
        val placeholderIcon: Int = R.drawable.ic_photo_black_24dp,
        val errorIcon: Int = R.drawable.ic_broken_image_black_24dp,
        val onError: (errorMessage: String) -> Unit = {},
        val onImageLoaded: () -> Unit = {}
    )

}