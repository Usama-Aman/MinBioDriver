package com.vic.vicdriver.Controllers.Interfaces;


import com.vic.vicdriver.Models.Chat.ChatModel;
import com.vic.vicdriver.Models.Request.FeedBackRequest;
import com.vic.vicdriver.Models.Request.LoginRequest;
import com.vic.vicdriver.Models.Request.RegisterRequest;
import com.vic.vicdriver.Models.Response.GenResponse;
import com.vic.vicdriver.Models.Response.GradientsResearch.Gradients;
import com.vic.vicdriver.Models.Response.Login.Login;
import com.vic.vicdriver.Models.Response.PrivacyPolicy.PrivacyPolicyModel;
import com.vic.vicdriver.Models.Response.Support.SupportChat.SupportChatResponse;
import com.vic.vicdriver.Models.Response.Support.SupportChat.SupportSingleChatResponse;
import com.vic.vicdriver.Models.Response.Support.SupportList.SupportResponse;
import com.vic.vicdriver.Models.Response.Support.SupportResponse.TopicsModel;
import com.vic.vicdriver.Models.Response.TransactionHistory.TransactionHistroyModel;
import com.vic.vicdriver.Models.Response.bank_detail.BankDetailResponse;
import com.vic.vicdriver.Models.Response.orders_pack.OrderDetailResponse;
import com.vic.vicdriver.Models.Response.orders_pack.OrdersResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @Headers({"Content-Type:application/json"})
    @POST("driver/register")
    Call<ResponseBody> register(@Header("localization") String language, @Body RegisterRequest register);

    @Multipart
    @Headers({"Accept:application/json"})
    @POST("driver/order/change_status")
    Call<GenResponse> updateOrderStatus(@Header("Authorization") String authHeader,
                                        @Part("status") RequestBody status,
                                        @Part("delivery_address_id") RequestBody deliveryAddressId,
                                        @Part MultipartBody.Part signatureImage);

    @Multipart
    @Headers({"Accept:application/json"})
    @POST("driver/order/change_status")
    Call<GenResponse> updateAcceptOrder(@Header("Authorization") String authHeader,
                                        @Part("status") RequestBody status,
                                        @Part("delivery_address_id") RequestBody deliveryAddressId);

    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("driver/reset_password")
    Call<ResponseBody> resetPassword(@Field("password") String password,
                                     @Field("password_confirmation") String password_confirmation,
                                     @Field("email") String email,
                                     @Field("otp") String otp);

    @Headers({"Content-Type:application/json"})
    @POST("driver/order/reviews")
    Call<GenResponse> updateFeedBack(@Header("Authorization") String authHeader, @Body FeedBackRequest register);


    @Headers({"Accept:application/json"})
    @POST("driver/login")
    Call<Login> login(@Header("localization") String language, @Body LoginRequest loginRequest);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("driver/resend_otp")
    Call<ResponseBody> resendOtp(@Header("localization") String language, @Field("phone_number") String phone);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("driver/verify_otp")
    Call<ResponseBody> confirmOtp(@Header("localization") String language, @Field("phone_number") String phone, @Field("code") String otp);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("driver/forget_password")
    Call<ResponseBody> forgotPassword(@Header("localization") String language, @Field("email") String email);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("driver/resend_email")
    Call<ResponseBody> resendEmail(@Header("localization") String language, @Field("email") String email);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("driver/confirm_email")
    Call<ResponseBody> confirmEmail(@Header("localization") String language, @Field("email") String email);

    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("driver/bank_detail")
    Call<BankDetailResponse> getBankDetail(@Header("Authorization") String authHeader, @Field("bank_detail_id") String id);


    @Headers({"Accept:application/json"})
    @Multipart
    @POST("driver/profile/update")
    Call<Login> updateProfile(@Header("Authorization") String authHeader,
                              @Part("first_name") RequestBody first_name,
                              @Part("last_name") RequestBody last_name,
                              @Part("phone_number") RequestBody phone_number,
                              @Part("address") RequestBody address,
                              @Part("latitude") RequestBody latitude,
                              @Part("longitude") RequestBody longitude,
                              @Part("company_name") RequestBody company_name,
                              @Part("password") RequestBody password,
                              @Part("confirm_password") RequestBody confirm_password,
                              @Part MultipartBody.Part profile_image,
                              @Part MultipartBody.Part truck_plate,
                              @Part MultipartBody.Part proof_of_truck_id,
                              @Part MultipartBody.Part id_card_front,
                              @Part MultipartBody.Part id_card_back,
                              @Part MultipartBody.Part proof_of_license_id,
                              @Part MultipartBody.Part proof_of_insurance,
                              @Part("country_code") RequestBody country_code,
                              @Part("payment_type") RequestBody payment_type,
                              @Part("iso2") RequestBody isoCode,
                              @Part("email") RequestBody email);


    @Headers({"Accept:application/json"})
    @Multipart
    @POST("driver/bank_detail/update")
    Call<BankDetailResponse> updateBankDetail(@Header("Authorization") String authHeader,
                                              @Part("account_name") RequestBody account_name,
                                              @Part("swift_number") RequestBody swift_number,
                                              @Part("iban") RequestBody iban,
                                              @Part MultipartBody.Part bank_detail_photo);

    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("driver/logout")
    Call<ResponseBody> logout(@Header("Authorization") String authHeader,
                              @Field("device_id") String deviceId,
                              @Field("user_type") String user_type);

    @Headers({"Accept:application/json"})
    @GET("driver/settings")
    Call<ResponseBody> getSettings(@Header("Authorization") String authHeader);


    @Headers({"Accept:application/json"})
    @GET("driver/orders")
    Call<OrdersResponse> getMyOrders(@Header("Authorization") String authHeader,
                                     @Query("status") String status,
                                     @Query("page") int page);


    @Headers({"Accept:application/json"})
    @GET("driver/gradients")
    Call<Gradients> getGradients(@Header("Authorization") String authHeader);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("driver/changelang")
    Call<ResponseBody> changeLanguage(@Header("Authorization") String authHeader,
                                      @Field("lang") String language);


    @Multipart
    @Headers({"Accept:application/json"})
    @POST("driver/notifyonpickproduct")
    Call<ResponseBody> pickUpItem(@Header("Authorization") String authHeader,
                                  @Part("order_id") RequestBody orderId,
                                  @Part MultipartBody.Part pickUpSignature);

    @Headers({"Accept:application/json"})
    @GET("driver/notifyonreached")
    Call<ResponseBody> withInRange(@Header("Authorization") String authHeader,
                                   @Query("order_number") String orderNumber);


    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("driver/fcmdevices")
    Call<ResponseBody> sendDeviceToken(@Header("Authorization") String authHeader,
                                       @Field("token") String token,
                                       @Field("type") String type,
                                       @Field("app_mode") String appMode,
                                       @Field("device_id") String deviceId, @Field("user_type") String user_id);


    // Get Chat messages btw driver and buyer of a specific order
    @Headers({"Accept:application/json"})
    @GET("driver/driverbuyerchat")
    Call<ChatModel> getDriverBuyerMessages(@Header("Authorization") String authHeader,
                                           @Query("order_id") int orderId,
                                           @Query("page") int page);


    //   Post Chat Text messages btw driver and seller of a specific order
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("driver/driverbuyerchat")
    Call<ResponseBody> postTextMessageDriverBuyer(@Header("Authorization") String authHeader,
                                                  @Part("order_id") RequestBody orderId,
                                                  @Part("type") RequestBody type,
                                                  @Part("duration") RequestBody duration,
                                                  @Part("message") RequestBody message);


    //   post file chat messages  btw Seller and Buyer
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("driver/driverbuyerchat")
    Call<ResponseBody> postFileMessageDriverBuyer(@Header("Authorization") String authHeader,
                                                  @Part("order_id") RequestBody orderId,
                                                  @Part("type") RequestBody type,
                                                  @Part("duration") RequestBody duration,
                                                  @Part MultipartBody.Part file);


    // Get Chat messages btw driver and buyer of a specific order
    @Headers({"Accept:application/json"})
    @GET("driver/driversellerchat")
    Call<ChatModel> getDriverSellerMessages(@Header("Authorization") String authHeader,
                                            @Query("order_id") int orderId,
                                            @Query("page") int page);


    //   Post Chat Text messages btw driver and seller of a specific order
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("driver/driversellerchat")
    Call<ResponseBody> postTextMessageDriverSeller(@Header("Authorization") String authHeader,
                                                   @Part("order_id") RequestBody orderId,
                                                   @Part("type") RequestBody type,
                                                   @Part("duration") RequestBody duration,
                                                   @Part("message") RequestBody message);


    //   post file chat messages  btw Seller and Buyer
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("driver/driversellerchat")
    Call<ResponseBody> postFileMessageDriverSeller(@Header("Authorization") String authHeader,
                                                   @Part("order_id") RequestBody orderId,
                                                   @Part("type") RequestBody type,
                                                   @Part("duration") RequestBody duration,
                                                   @Part MultipartBody.Part file);


    @Headers({"Accept:application/json"})
    @GET("driver/payments")
    Call<TransactionHistroyModel> getDriverPayment(@Header("Authorization") String authHeader,
                                                   @Query("page") int page);

    @Headers({"Accept:application/json"})
    @GET("driver/support/topics")
    Call<TopicsModel> getSupportData(@Header("Authorization") String authHeader);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("driver/support")
    Call<ResponseBody> postSupportData(@Header("Authorization") String authHeader,
                                       @Field("support_topic_id") int supportId,
                                       @Field("email") String email,
                                       @Field("comment") String comment,
                                       @Field("type") String type);

    @Headers({"Accept:application/json"})
    @GET("driver/profile")
    Call<Login> getProfileData(@Header("Authorization") String authHeader);


    /********  Support Section  ********/

    @Headers({"Accept:application/json"})
    @GET("driver/support")
    Call<SupportResponse> getSupportList(@Header("Authorization") String authHeader, @Query("page") int page, @Query("type") String type);


    //Get Nego Chat Messages btw Buyer and Seller
    @Headers({"Accept:application/json"})
    @GET("driver/support/{supportId}")
    Call<SupportChatResponse> getSupportMessages(@Header("Authorization") String authHeader,
                                                 @Path("supportId") int supportId, @Query("type") String type);

    //   post nego file chat messages  btw Buyer and Seller
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("driver/support/comment")
    Call<SupportSingleChatResponse> postSupportMessage(@Header("Authorization") String authHeader,
                                                       @Part("support_id") RequestBody orderId,
                                                       @Part("comment") RequestBody comment);

    @Headers({"Accept:application/json"})
    @Multipart
    @POST("driver/support/comment")
    Call<SupportSingleChatResponse> postSupportMessageFile(@Header("Authorization") String authHeader,
                                                           @Part("support_id") RequestBody orderId,
                                                           @Part("comment") RequestBody comment,
                                                           @Part MultipartBody.Part file);

    @Headers({"Accept:application/json"})
    @GET("pages/{key}")
    Call<PrivacyPolicyModel> getPrivacyPolicy(@Path("key") String key);

    @Headers({"Accept:application/json"})
    @GET("driver/order/detail")
    Call<OrderDetailResponse> getOrderDetails(@Header("Authorization") String authHeader,
                                              @Query("lat") String orderId,
                                              @Query("lng") String page);

}
