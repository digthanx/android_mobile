package com.teamforce.thanksapp.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val value: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

suspend inline fun <T> safeApiCall(
    emitter: RemoteErrorEmitter,
    crossinline responseFunction: suspend () -> T
): T? {
    return try {
        val response = withContext(Dispatchers.IO) { responseFunction.invoke() }
        response
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            e.printStackTrace()
            when (e) {
                is HttpException -> {
                    val body = e.response()?.errorBody()
                    emitter.onError(getErrorMessage(body))
                }
                is SocketTimeoutException -> emitter.onError(ErrorType.TIMEOUT)
                is IOException -> emitter.onError(ErrorType.NETWORK)
                else -> emitter.onError(ErrorType.UNKNOWN)
            }
        }
        null
    }
}

interface RemoteErrorEmitter {
    fun onError(msg: String)
    fun onError(errorType: ErrorType)

}

private const val MESSAGE_KEY = "message"
private const val ERROR_KEY = "error"


fun getErrorMessage(responseBody: ResponseBody?): String {
    return try {
        val jsonObject = JSONObject(responseBody!!.string())
        when {
            jsonObject.has(MESSAGE_KEY) -> jsonObject.getString(MESSAGE_KEY)
            jsonObject.has(ERROR_KEY) -> jsonObject.getString(ERROR_KEY)
            else -> "Something wrong happened"
        }
    } catch (e: Exception) {
        "Something wrong happened"
    }
}

enum class ErrorType {
    NETWORK, // IO
    TIMEOUT, // Socket
    UNKNOWN //Anything else
}