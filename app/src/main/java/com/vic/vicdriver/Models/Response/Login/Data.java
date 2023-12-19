
package com.vic.vicdriver.Models.Response.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("access_token")
    @Expose
    private String access_token;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("phone_verified_code")
    @Expose
    private Integer phoneVerifiedCode;
    @SerializedName("is_phone_verified")
    @Expose
    private Integer isPhoneVerified;
    @SerializedName("is_email_verified")
    @Expose
    private Integer isEmailVerified;
    @SerializedName("is_profile_complete")
    @Expose
    private Integer isProfileComplete;
    @SerializedName("is_active")
    @Expose
    private Integer isActive;
    @SerializedName("phone_verified_at")
    @Expose
    private String phoneVerifiedAt;
    @SerializedName("email_verified_at")
    @Expose
    private String emailVerifiedAt;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("profile_image_path")
    @Expose
    private String profileImagePath;

    @SerializedName("bank_detail_id")
    @Expose
    private String bank_detail_id;

    public String getBank_detail_id() {
        return bank_detail_id;
    }

    public void setBank_detail_id(String bank_detail_id) {
        this.bank_detail_id = bank_detail_id;
    }

    @SerializedName("truck_detail")
    @Expose
    private TruckDetail truckDetail;

    public Data(Integer id, String name, String firstName, String lastName, String email, String access_token, String password, String countryCode, String phoneNumber, String address, String latitude, String longitude, String companyName, String paymentType, String profileImage, Integer phoneVerifiedCode, Integer isPhoneVerified, Integer isEmailVerified, Integer isProfileComplete, Integer isActive, String phoneVerifiedAt, String emailVerifiedAt, String lang, String profileImagePath, TruckDetail truckDetail) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.access_token = access_token;
        this.password = password;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.companyName = companyName;
        this.paymentType = paymentType;
        this.profileImage = profileImage;
        this.phoneVerifiedCode = phoneVerifiedCode;
        this.isPhoneVerified = isPhoneVerified;
        this.isEmailVerified = isEmailVerified;
        this.isProfileComplete = isProfileComplete;
        this.isActive = isActive;
        this.phoneVerifiedAt = phoneVerifiedAt;
        this.emailVerifiedAt = emailVerifiedAt;
        this.lang = lang;
        this.profileImagePath = profileImagePath;
        this.truckDetail = truckDetail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Integer getPhoneVerifiedCode() {
        return phoneVerifiedCode;
    }

    public void setPhoneVerifiedCode(Integer phoneVerifiedCode) {
        this.phoneVerifiedCode = phoneVerifiedCode;
    }

    public Integer getIsPhoneVerified() {
        return isPhoneVerified;
    }

    public void setIsPhoneVerified(Integer isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }

    public Integer getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Integer isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public Integer getIsProfileComplete() {
        return isProfileComplete;
    }

    public void setIsProfileComplete(Integer isProfileComplete) {
        this.isProfileComplete = isProfileComplete;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getPhoneVerifiedAt() {
        return phoneVerifiedAt;
    }

    public void setPhoneVerifiedAt(String phoneVerifiedAt) {
        this.phoneVerifiedAt = phoneVerifiedAt;
    }

    public String getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public TruckDetail getTruckDetail() {
        return truckDetail;
    }

    public void setTruckDetail(TruckDetail truckDetail) {
        this.truckDetail = truckDetail;
    }

}
