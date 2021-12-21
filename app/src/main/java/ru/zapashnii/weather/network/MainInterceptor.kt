package ru.zapashnii.weather.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import ru.zapashnii.weather.BuildConfig
import ru.zapashnii.weather.utils.Logger
import java.io.IOException

/** Перехватчик сетевых запросов */
class MainInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val url = getUrl(chain)

        val response: Response = getResponse(chain, builder, url)
        val json = getJsonString(response)

        logNetwork(response, json)

        return response
    }

    /**
     * Сформировать [Response] из [Interceptor.Chain]
     * @param chain     [Interceptor.Chain]
     * @param builder   [Request.Builder]
     * @param url       url в виде строки.
     * @return          [Response]
     */
    private fun getResponse(
        chain: Interceptor.Chain,
        builder: Request.Builder,
        url: String
    ): Response {
        return try {
            chain.proceed(builder.build())
        } catch (ex: Exception) {
            Logger.logError("Interceptor", "* Network log \n\n Cannot proceed request $url", ex)
            throw ex
        }
    }

    /**
     * Получить url из [Interceptor.Chain].
     * Из [Interceptor.Chain] достаётся запрос и у него берётся url.
     * @param chain     [Interceptor.Chain].
     * @return          url в виде строки.
     */
    private fun getUrl(chain: Interceptor.Chain): String {
        return chain.request().url().toString()
    }

    /**
     * Получить json из response.
     * @param response  response от сервера.
     * @return          json в виде строки или пустую строку, если response.body == null.
     */
    private fun getJsonString(response: Response): String {
        return response.body()?.string() ?: ""
    }

    /**
     * Залогировать response
     * @param response      response от сервера.
     * @param responseJson  json ответа.
     */
    private fun logNetwork(response: Response, responseJson: String) {
        val message = getNetworkLogMessage(response, responseJson)

        if (response.code() != 200) {
            Logger.logError("NETWORK", message)
        } else {
            Logger.logDebug("NETWORK", message)
        }
    }

    /**
     * Формирует сообщение для логов
     * @param response      response от сервера.
     * @param responseJson  json ответа от сервера.
     * @return              сформированную строку сообщения в лог.
     */
    private fun getNetworkLogMessage(response: Response, responseJson: String): String {
        return if (BuildConfig.DEBUG) {
            "* Network Log" +
                    "\n*--------------------------------------" +
                    "\n* Request: ${response.request().method()} ${
                        response.request().url()
                    } Response code: ${response.code()}" +
                    "\n* Request body:  ${bodyToString(response.request().body())}" +
                    "\n* Response body $responseJson" +
                    "\n*--------------------------------------"
        } else {
            "* Network Log" +
                    "\n*--------------------------------------" +
                    "\n* Request: ${response.request().method()} ${
                        response.request().url()
                    } Response code: ${response.code()}" +
                    "\n*--------------------------------------"
        }
    }

    /**
     * Сформировать из [RequestBody] строку.
     * @param request   RequestBody.
     * @return          строка с содержимым RequestBody.
     */
    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            Logger.logError(e)
            return "did not work"
        }
    }
}