package com.vic.vicdriver.Models.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankDetailRequest {

    @SerializedName("bank_name")
    @Expose
    private String bank_name;
    @SerializedName("bank_address")
    @Expose
    private String bank_address;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("zip_code")
    @Expose
    private String zip_code;
    @SerializedName("account_name")
    @Expose
    private String account_name;
    @SerializedName("account_number")
    @Expose
    private String account_number;
    @SerializedName("iso2")
    @Expose
    private String iso2;

//    public BankDetailRequest(String name, String email, String password, String passwordConfirmation,  String country_code, String phone, String iso2) {
//        this.name = name;
//        this.email = email;
//        this.password = password;
//        this.passwordConfirmation = passwordConfirmation;
//        this.country_code = country_code;
//        this.phone = phone;
//        this.iso2 = iso2;
//    }

//    public String getIso2() {
//        return iso2;
//    }
//
//    public void setIso2(String iso2) {
//        this.iso2 = iso2;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getCountry_code() {
//        return country_code;
//    }
//
//    public void setCountry_code(String country_code) {
//        this.country_code = country_code;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getPasswordConfirmation() {
//        return passwordConfirmation;
//    }
//
//    public void setPasswordConfirmation(String passwordConfirmation) {
//        this.passwordConfirmation = passwordConfirmation;
//    }


}
