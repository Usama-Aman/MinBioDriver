package com.vic.vicdriver.Models.Response.Support.SupportChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SupportChatUserDetail {
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
    @SerializedName("iso2")
    @Expose
    private String iso2;
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("profile_image_path")
    @Expose
    private String profileImagePath;
    @SerializedName("full_name")
    @Expose
    private String fullName;

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

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}