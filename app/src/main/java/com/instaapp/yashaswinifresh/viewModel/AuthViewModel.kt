package com.instaapp.yashaswinifresh.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.instaapp.yashaswinifresh.listener.AuthListener
import com.instaapp.yashaswinifresh.network.repositories.UserRepository
import com.instaapp.yashaswinifresh.network.requestModel.ChangePassword
import com.instaapp.yashaswinifresh.network.requestModel.ProfileDataRequest
import com.instaapp.yashaswinifresh.network.requestModel.UserRegisterRequest
import com.instaapp.yashaswinifresh.utils.*

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    var userName: String? = null
    var password: String? = null
    var salutation: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var code: String? = null
    var mobile: String? = null
    var confirmPassword: String? = null

    var authListener: AuthListener? = null

    fun onLoginButton(userName: String, password: String, restID: String) {
        authListener?.onStarted()
        if (userName.isEmpty() || userName.length < 5) {
            authListener?.onFailure("Enter valid username", "NA")
            return
        } else if (password.isEmpty() || password.length < 3) {
            authListener?.onFailure("Enter valid password", "NA")
            return
        }

        Coroutines.main {
            try {
                Log.d("TAG", "onLoginButton: $restID")
                val authResponse =
                    repository.userLogin(userName, password, restID)
                authResponse.let {
                    authListener?.onSuccess(it, "userLoginCheck")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun mainLogin(user: String, pass: String, restID: String) {
        log("mainLogin ", " Called")
        authListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.mainLogin(user, pass, restID)
                mainLoginResponse.let {
                    authListener?.onSuccess(mainLoginResponse, "mainLogin")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getUserID(token: String) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getUserID(token, userName!!)
                response.let {
                    authListener?.onSuccess(response, "getUserID")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun insertUser(token: String, userId: String, restID: String) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.insertUser(token, userId, restID)
                response.let {
                    authListener?.onSuccess(response, "insertUser")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun resetUserName(token: String, email: String) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.resetUserName(token, email)
                response.let {
                    authListener?.onSuccess(response, "resetUserName")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun resetPassword(token: String, username: String) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.resetPassword(token, username)
                response.let {
                    authListener?.onSuccess(response, "resetPassword")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun checkLogin(restID: String) {
        authListener?.onStarted()
        if (userName.isNullOrEmpty() || userName!!.length < 5) {
            authListener?.onFailure("Enter valid username!", "NA")
            return
        } else if (password.isNullOrEmpty() || password!!.length < 3) {
            authListener?.onFailure("Enter valid password!", "NA")
            return
        }

        Coroutines.main {
            try {
                val authResponse =
                    repository.userLogin(userName!!, password!!, restID)
                authResponse.let {
                    authListener?.onSuccess(it, "userLoginCheck")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    // On SignUp
    fun onSignUpButton(view: View) {
        authListener?.onStarted()
        if (userName.isNullOrEmpty() || userName!!.length < 2) {
            authListener?.onFailure("Enter your username", "validation")
            return
        } else if (salutation.isNullOrEmpty() || salutation!!.isEmpty()) {
            authListener?.onFailure("Select your salutation", "validation")
            return
        } else if (firstName.isNullOrEmpty() || firstName!!.length < 2) {
            authListener?.onFailure("Enter your first name", "validation")
            return
        } else if (lastName.isNullOrEmpty() || lastName!!.length < 2) {
            authListener?.onFailure("Enter your last name", "validation")
            return
        } else if (email.isNullOrEmpty() || email!!.length < 5) {
            authListener?.onFailure("Enter your email", "validation")
            return
        } else if (code.isNullOrEmpty() || code!!.length < 2) {
            authListener?.onFailure("Enter your phone code", "validation")
            return
        } else if (!code!!.trim().contains("+")) {
            authListener?.onFailure("Enter valid country code", "validation")
            return
        } else if (mobile.isNullOrEmpty() || mobile!!.length < 9) {
            authListener?.onFailure("Enter your mobile no", "validation")
            return
        } else if (password.isNullOrEmpty() || password!!.length < 6) {
            authListener?.onFailure("Enter your password", "validation")
            return
        } else if (confirmPassword.isNullOrEmpty() || confirmPassword!!.length < 6) {
            authListener?.onFailure("Enter confirm password", "validation")
            return
        } else if (password != confirmPassword) {
            authListener?.onFailure("Password and confirm password does not match", "validation")
            return
        }

        mainLogin(StaticValue.REST_USER_NAME, StaticValue.REST_PASSWORD, StaticValue.TEMP_RESTRO_ID)


    }

    fun registerUser(token: String, restID: String) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val authResponse =
                    repository.registerUser(
                        "Token $token",
                        UserRegisterRequest(
                            userName,
                            email,
                            firstName,
                            lastName,
                            password,
                            restID,
                            "N",
                            "",
                            "",
                            "",
                            "",
                            ""
                        )
                    )
                authResponse.let {
                    authListener?.onSuccess(it, "registerUser")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    fun createUser(id: String, token: String) {
        Coroutines.main {
            try {
                val authResponse =
                    repository.createUser(
                        "Token $token",
                        UserRegisterRequest(
                            "",
                            email,
                            firstName,
                            lastName,
                            "",
                            "",
                            "",
                            id,
                            CurrentTimeStamp(),
                            "extra",
                            salutation,
                            code + mobile
                        )
                    )
                authResponse.let {
                    authListener?.onSuccess(it, "createUser")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun generateOtp(code: String, mobile: String) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getOTP(code + mobile)
                response.let {
                    authListener?.onSuccess(response, "generateOtp")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun verifyOTP(otp: String) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.verifyOTP(code + mobile, otp)
                response.let {
                    authListener?.onSuccess(response, "verifyOTP")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getUserProfile(token: String, userId: String) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getProfile(token, userId)
                response.let {
                    authListener?.onSuccess(response, "getUserProfile")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun onUpdateUserProfile(token: String, userId: String) {
        authListener?.onStarted()
        if (userName.isNullOrEmpty() || userName!!.length < 2) {
            authListener?.onFailure("Enter your username!", "validation")
            return
        } else if (salutation.isNullOrEmpty() || salutation!!.length < 1) {
            authListener?.onFailure("Select your salutation!", "validation")
            return
        } else if (firstName.isNullOrEmpty() || firstName!!.length < 2) {
            authListener?.onFailure("Enter your first name!", "validation")
            return
        } else if (lastName.isNullOrEmpty() || lastName!!.length < 2) {
            authListener?.onFailure("Enter your last name!", "validation")
            return
        } else if (email.isNullOrEmpty() || email!!.length < 5) {
            authListener?.onFailure("Enter your email!", "validation")
            return
        } else if (mobile.isNullOrEmpty() || mobile!!.length < 9) {
            authListener?.onFailure("Enter your mobile no!", "validation")
            return
        }
        Coroutines.main {
            try {
                val response = repository.updateProfile(
                    token, userId, ProfileDataRequest(
                        salutation!!, firstName!!, lastName!!, userName!!, email!!,
                        CurrentTimeStamp(), code + mobile
                    )
                )
                response.let {
                    authListener?.onSuccess(response, "onUpdateUserProfile")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun updateCustomerProfile(token: String, userId: String) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.updateCustomerProfile(
                    token, userId, ProfileDataRequest(
                        salutation!!, firstName!!, lastName!!, userName!!, email!!,
                        CurrentTimeStamp(), code + mobile
                    )
                )
                response.let {
                    authListener?.onSuccess(response, "updateCustomerProfile")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun changePassword(token: String, data: ChangePassword) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.changePassword(
                    token, data
                )
                response.let {
                    authListener?.onSuccess(response, "changePassword")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun deleteAccount(token: String, userID: String) {
        authListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.deleteAccount(
                    token, userID
                )
                response.let {
                    authListener?.onSuccess(response, "deleteAccount")
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    fun logout() {

    }


}