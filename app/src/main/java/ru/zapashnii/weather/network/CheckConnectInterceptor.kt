package ru.zapashnii.weather.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.zapashnii.weather.R
import ru.zapashnii.weather.di.MainApp
import ru.zapashnii.weather.navigation.ViewRouter
import ru.zapashnii.weather.utils.Logger
import ru.zapashnii.weather.utils.NetworkUtils
import ru.zapashnii.weather.utils.Utils
import javax.inject.Inject

/** Перехватчик сетевых запросов для проверки наличия интернет-соединения */
class CheckConnectInterceptor : Interceptor {

    @Inject
    lateinit var viewRouter: ViewRouter

    init {
        MainApp.instance.applicationComponent.inject(this)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkUtils.isInternetAvailable(MainApp.instance.applicationContext)) {
            viewRouter.singleAlertDialog(Utils.getString(R.string.cannot_connect_to_internet))
        }
        return getResponse(chain, chain.request().newBuilder())
    }

    /**
     * Сформировать [Response] из [Interceptor.Chain]
     * @param chain     [Interceptor.Chain]
     * @param builder   [Request.Builder]
     * @return          [Response]
     */
    private fun getResponse(chain: Interceptor.Chain, builder: Request.Builder): Response {
        return try {
            chain.proceed(builder.build())
        } catch (ex: Exception) {
            Logger.logError(ex)
            throw ex
        }
    }
}