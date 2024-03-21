package com.instaapp.veggietemp1.network

import com.instaapp.veggietemp1.utils.ApiException
import com.instaapp.veggietemp1.utils.log
import org.json.JSONObject
import retrofit2.Response


abstract class SafeApiRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T? {
        val response = call.invoke()
        if (response.isSuccessful) {
            return response.body()
        } else {
            val error = response.errorBody()?.string()
            log("PaymentActivity SafeApiRequest "," $error")
            var message = "NA"
            error?.let {
                message = try {
                    val jObjError = JSONObject(error)
                    jObjError.get("msg").toString()
                } catch (e: Exception) {
                    "The system is temporarily unavailable. Please try again later"
                }
            }
            throw ApiException(message + "@" + response.code() + "@" + error)
        }
    }
}