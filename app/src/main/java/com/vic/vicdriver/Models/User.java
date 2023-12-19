package com.vic.vicdriver.Models;

public class User {

    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String companyName;
//    private String siretNo;
    private String phone;
    private String lang;
    private String countryCode;
    private int is_merchant;

    public User(Integer id, String name, String lastName, String email, String companyName, String phone, String lang, String countryCode, int is_merchant) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.companyName = companyName;

        this.phone = phone;
        this.lang = lang;
        this.countryCode = countryCode;
        this.is_merchant = is_merchant;
    }

    public int getIs_merchant() {
        return is_merchant;
    }

    public void setIs_merchant(int is_merchant) {
        this.is_merchant = is_merchant;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

//    public String getSiretNo() {
//        return siretNo;
//    }
//
//    public void setSiretNo(String siretNo) {
//        this.siretNo = siretNo;
//    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }


}
