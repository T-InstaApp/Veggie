package com.instaapp.veggietemp1.network

import com.instaapp.veggietemp1.network.requestModel.*
import com.instaapp.veggietemp1.network.responseModel.*
import com.instaapp.veggietemp1.utils.StaticValue
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MyApi {

    @Headers("Content-Type: application/json")
    @POST("rest-auth/login/v1/")
    suspend fun mainLogin(
        @Body login: MainLogin
    ): Response<MainLoginResponse>

    @Headers("Content-Type: application/json")
    @GET("hour/")
    suspend fun getHour(
        @Header("Authorization") authorization: String?,
        @Query("restaurant_id") id: Int?
    ): Response<HourResponse>

    @GET("v2/customer-payment/")
    suspend fun getOrderList(
        @Header("Authorization") authorization: String?,
        @Query("restaurant_id") restaurant_id: String?,
        @Query("customer_id") customer_id: String?
    ): Response<ArrayList<OrderListResponse>>

    @GET("order-items-x/")
    suspend fun orderItemDetails(
        @Header("Authorization") authorization: String?,
        @Query("order_id") order_id: String?
    ): Response<OrderMenuDetail>


    @Headers("Content-Type: application/json")
    @GET("cart/")
    suspend fun checkCart(
        @Header("Authorization") authorization: String?,
        @Query("restaurant") restaurant_id: String?,
        @Query("customer_id") customer_id: String?
    ): Response<CheckCartResponse>

    @Headers("Content-Type: application/json")
    @GET("cart-items-x/")
    suspend fun getCartList(
        @Header("Authorization") authorization: String?,
        @Query("cart_id") rid: String?,
        @Query("restaurant_id") retroid: String?
    ): Response<CartListResponse>

    @PUT("cart-item-post/{id}/")
    suspend fun updateCart(
        @Header("Authorization") authorization: String?,
        @Path("id") cartItemID: String?,
        @Body cartUpdate: JsonObject?
    ): Response<CartListResponse>

    @Headers("Content-Type: application/json")
    @DELETE("cart-item-x/{id}/")
    suspend fun deleteCartItem(
        @Header("Authorization") authorization: String?,
        @Path("id") rid: String?
    ): Response<HourResponse>


    @Headers("Content-Type: application/json")
    @GET("user/")
    suspend fun getUserID(
        @Header("Authorization") authorization: String?,
        @Query("username") username: String?
    ): Response<UserIDResponse>


    @POST("userrestaurant/")
    suspend fun insertUser(
        @Header("Authorization") authorization: String?,
        @Body login: userModel?
    ): Response<UserRestaurant>

    @Headers("Content-Type: application/json")
    @POST("forgot/user/")
    suspend fun resetUserName(
        @Header("Authorization") authorization: String?,
        @Body login: LoginRequest
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("rest-auth/password/reset/v1/")
    suspend fun resetPassword(
        @Header("Authorization") authorization: String?,
        @Body login: LoginRequest
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("user/")
    suspend fun userRegistration(
        @Header("Authorization") authorization: String?,
        @Body login: UserRegisterRequest?
    ): Response<UserRegistrationResponse>

    @Headers("Content-Type: application/json")
    @POST("customer/")
    suspend fun createUser(
        @Header("Authorization") authorization: String?,
        @Body login: UserRegisterRequest?
    ): Response<ResponseBody>

    @POST("send/code/")
    suspend fun getOTP(
        @Body mobileVerification: MobileVerificationRequest
    ): Response<ResponseBody>

    @POST("guest/verify/")
    suspend fun verifyOtp(
        @Body mobileVerification: MobileVerificationRequest
    ): Response<MobileVerifyResponse>

    @GET("customer/")
    suspend fun getProfile(
        @Header("Authorization") authorization: String?,
        @Query("customer_id") customer_id: String?
    ): Response<ProfileResponse>

    @GET("restaurant/{id}/")
    suspend fun getAboutUs(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?
    ): Response<RestaurantsAboutUS>

    @PUT("user/{id}/")
    suspend fun updateProfile(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body rid: ProfileDataRequest?
    ): Response<UpdateProfileResponse>

    @PUT("customer/{id}/")
    suspend fun updateCustomerProfile(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body rid: ProfileDataRequest
    ): Response<UpdateProfileResponse>

    @POST("rest-auth/password/reset/confirm/v1/")
    suspend fun changePassword(
        @Header("Authorization") authorization: String?,
        @Body rid: ChangePassword?
    ): Response<ChangePasswordResponse>


    @Headers("Content-Type: application/json")
    @DELETE("delete-account/{id}")
    suspend fun deleteAccount(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("shipping/")
    suspend fun getShippingAddress(
        @Header("Authorization") authorization: String?,
        @Query("customer_id") rid: String?
    ): Response<AddressListResponse>

    @Headers("Content-Type: application/json")
    @GET("billing/")
    suspend fun getBillingAddress(
        @Header("Authorization") authorization: String?,
        @Query("customer_id") rid: String?
    ): Response<AddressListResponse>

    @GET("shipping-method/?status=ACTIVE")
    suspend fun getShippingDetails(
        @Header("Authorization") authorization: String?,
        @Query("restaurant_id") rid: String?
    ): Response<ShipmentResponse>

    @Headers("Content-Type: application/json")
    @POST("shipping/")
    suspend fun addShippingAddress(
        @Header("Authorization") authorization: String?,
        @Body addBillingAddress: AddBillingShippingAddressRequest?
    ): Response<AddressUpdateResponse>

    @Headers("Content-Type: application/json")
    @PUT("billing/{id}/")
    suspend fun updateBillingAddress(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body addBillingAddress: AddBillingShippingAddressRequest?
    ): Response<AddressUpdateResponse>

    @Headers("Content-Type: application/json")
    @POST("billing/")
    suspend fun addBillingAddress(
        @Header("Authorization") authorization: String?,
        @Body addBillingAddress: AddBillingShippingAddressRequest?
    ): Response<AddressUpdateResponse>

    @Headers("Content-Type: application/json")
    @PUT("shipping/{id}/")
    suspend fun updateShippingAddress(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body addBillingAddress: AddBillingShippingAddressRequest
    ): Response<AddressUpdateResponse>

    @GET("shipping-method/?status=ACTIVE")
    suspend fun getShipmentMethod(
        @Header("Authorization") authorization: String?,
        @Query("restaurant") rid: String?
    ): Response<ShipmentResponse>

    @POST("order-detail-x/")
    suspend fun updateOrderDetail(
        @Header("Authorization") authorization: String?,
        @Body query: UpdateOrderRequest
    ): Response<OrderDetailResponse>


    @Headers("Content-Type: application/json")
    @POST("print-order-x/")
    suspend fun printOrder(
        @Header("Authorization") authorization: String?,
        @Body printOrder: PrintOrderRequest
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("cart/")
    suspend fun createCart(
        @Header("Authorization") authorization: String?,
        @Body createCart: CreateCartRequest
    ): Response<CreateCartResponse>

    @GET("product-size-prices-x/")
    suspend fun fetchSize(
        @Header("Authorization") authorization: String?,
        @Query("product_id") productId: String?
    ): Response<FetchSizeResponse>

    @GET("product-addons-x/")
    suspend fun productAddOnWithoutSize(
        @Header("Authorization") authorization: String?,
        @Query("product_id") productid: String?
    ): Response<ProductAddOnResponse>


    @GET("product-addons-x/")
    suspend fun productAddOnWithSize(
        @Header("Authorization") authorization: String?,
        @Query("product_id") productid: String?,
        @Query("size") size: Int
    ): Response<ProductAddOnResponse>

    @POST("cart-item-post/")
    suspend fun addToCart(
        @Header("Authorization") authorization: String?,
        @Body login: JsonObject?
    ): Response<CartListResponse>

    @PUT("cart-item-post/{id}/")
    suspend fun updateCartDetails(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body cartUpdate: JsonObject?
    ): Response<AddToCartResponse>

    @POST("graphql/")
    suspend fun searchCart(
        @Body query: RequestBody?
    ): Response<ProductSearchResponse>

    @GET("restaurant-details/")
    suspend fun getAppDetails(
        @Query("restaurant_id") restroID: Int,
        @Query("type") type: String?
    ): Response<CompanyData>


    @Headers("Content-Type: application/json")
    @GET("get-master-category/{id}/")
    suspend fun getMasterCat(
        @Header("Authorization") authorization: String?,
        @Path("id") type: String?,
    ): Response<ArrayList<MasterCategoryDataModel>>

    @Headers("Content-Type: application/json")
    @GET("tax/?country_code=US")
    suspend fun getStateList(
        @Header("Authorization") authorization: String?
    ): Response<StateListRequest>

    @POST("v2/fee-x/")
    suspend fun getFees(
        @Header("Authorization") authorization: String?,
        @Body query: AddFreeRequest?,
    ): Response<FreeResponse>

    @Headers("Content-Type: application/json")
    @GET("v2/category/?")
    suspend fun getCategory(
        @Header("Authorization") authorization: String?,
        @Query("restaurant_id") id: String?,
        @Query("master_category_id") masterCatID: Int
    ): Response<ArrayList<CategoryResponse>>

    @GET("v2/addon-contents-x/")
    suspend fun productContents(
        @Header("Authorization") authorization: String?,
        @Query("parent_addon_id") parent_addon_id: Int
    ): Response<ProductContain>

    @GET("v2/get-sub-addon-content/")
    suspend fun getSubAddonsForAddons(
        @Header("Authorization") authorization: String?,
        @Query("addoncontent") addonContentId: Int
    ): Response<AddonContentSubAddon>

    @Headers("Content-Type: application/json")
    @GET("v2/catalog/?status=ACTIVE")
    suspend fun getMenuList2(
        @Header("Authorization") authorization: String,
        @Query("restaurant_id") rid: String,
        @Query("category_id") cid: Int,
        @Query("page") page: Int
    ): MenuResponse

    @Headers("Content-Type: application/json")
    @GET("v2/catalog/?status=ACTIVE")
    suspend fun getMenuList(
        @Header("Authorization") authorization: String,
        @Query("restaurant_id") rid: String,
        @Query("category_id") cid: Int,
        @Query("page") page: Int
    ): Response<MenuResponse>

    @POST("v2/payment/")
    suspend fun placeOrder(
        @Header("Authorization") authorization: String?,
        @Body cartUpdate: JsonObject
    ): Response<ResponseBody>

    @POST("payment/")
    suspend fun placeOrderForOtherCountry(
        @Header("Authorization") authorization: String?,
        @Body cartUpdate: JsonObject
    ): Response<ResponseBody>


    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): MyApi {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(networkConnectionInterceptor)
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(StaticValue.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}