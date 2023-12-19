package com.vic.vicdriver.Models.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileRequest {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("siret_no")
    @Expose
    private String siretNo;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("password_confirmation")
    @Expose
    private String passwordConfirmation;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("iso2")
    @Expose
    private String iso2;

    public ProfileRequest(String email, String name, String lastName, String phone, String companyName, String siretNo, String password, String passwordConfirmation, String countryCode, String iso2) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.companyName = companyName;
        this.siretNo = siretNo;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.countryCode = countryCode;
        this.iso2 = iso2;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSiretNo() {
        return siretNo;
    }

    public void setSiretNo(String siretNo) {
        this.siretNo = siretNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }


}
