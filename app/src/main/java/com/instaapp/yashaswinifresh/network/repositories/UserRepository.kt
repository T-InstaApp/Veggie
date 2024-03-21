package com.instaapp.yashaswinifresh.network.repositories

import com.instaapp.yashaswinifresh.network.MyApi
import com.instaapp.yashaswinifresh.network.SafeApiRequest
import com.instaapp.yashaswinifresh.network.requestModel.*
import com.instaapp.yashaswinifresh.network.responseModel.*
import okhttp3.ResponseBody


class UserRepository(private val api: MyApi) : SafeApiRequest() {

    suspend fun userLogin(user: String, pass: String, restID: String): MainLoginResponse? {
        return apiRequest {
            api.mainLogin(MainLogin(user, pass, restID))
        }
    }

    suspend fun mainLogin(user: String, pass: String, restID: String): MainLoginResponse? {
        return apiRequest {
            api.mainLogin(MainLogin(user, pass, restID))
        }
    }

    suspend fun getUserID(token: String, user: String): UserIDResponse? {
        return apiRequest { api.getUserID(token, user) }
    }

    suspend fun insertUser(token: String, userId: String, restroID: String): UserRestaurant? {
        return apiRequest { api.insertUser(token, userModel(userId, restroID)) }
    }

    suspend fun resetUserName(token: String, email: String): ResponseBody? {
        return apiRequest { api.resetUserName(token, LoginRequest(email, "", "")) }
    }

    suspend fun resetPassword(token: String, userName: String): ResponseBody? {
        return apiRequest { api.resetPassword(token, LoginRequest("", "", userName)) }
    }

    suspend fun registerUser(
        token: String,
        userData: UserRegisterRequest
    ): UserRegistrationResponse? {
        return apiRequest { api.userRegistration(token, userData) }
    }

    suspend fun createUser(token: String, userData: UserRegisterRequest
    ): ResponseBody? {
        return apiRequest { api.createUser(token, userData) }
    }

    suspend fun getOTP(mobile: String): ResponseBody? {
        return apiRequest { api.getOTP(MobileVerificationRequest(mobile, "")) }
    }

    suspend fun verifyOTP(mobile: String, otp: String): MobileVerifyResponse? {
        return apiRequest { api.verifyOtp(MobileVerificationRequest(mobile, otp)) }
    }

    suspend fun getProfile(token: String, userID: String): ProfileResponse? {
        return apiRequest { api.getProfile(token, userID) }
    }

    suspend fun updateProfile(
        token: String,
        userID: String,
        data: ProfileDataRequest
    ): UpdateProfileResponse? {
        return apiRequest { api.updateProfile(token, userID, data) }
    }

    suspend fun updateCustomerProfile(
        token: String,
        userID: String,
        data: ProfileDataRequest
    ): UpdateProfileResponse? {
        return apiRequest { api.updateCustomerProfile(token, userID, data) }
    }

    suspend fun changePassword(
        token: String,
        data: ChangePassword
    ): ChangePasswordResponse? {
        return apiRequest { api.changePassword(token, data) }
    }

    suspend fun deleteAccount(token: String, userID: String): ResponseBody? {
        return apiRequest { api.deleteAccount(token, userID) }
    }


}


