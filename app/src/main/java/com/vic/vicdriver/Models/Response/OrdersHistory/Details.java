
package com.vic.vicdriver.Models.Response.OrdersHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Details {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("vat")
    @Expose
    private String vat;
    @SerializedName("delivery_charges")
    @Expose
    private String delivery_charges;
    @SerializedName("is_reviewed")
    @Expose
    private int is_reviewed;
    @SerializedName("products")
    @Expose
    private List<Product> products = null;
    @SerializedName("discount")
    @Expose
    private String discount;

    public Details(String user, String address, String phone, String email, String vat, String delivery_charges, int is_reviewed, List<Product> products, String discount) {
        this.user = user;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.vat = vat;
        this.delivery_charges = delivery_charges;
        this.is_reviewed = is_reviewed;
        this.products = products;
        this.discount = discount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getIs_reviewed() {
        return is_reviewed;
    }

    public void setIs_reviewed(int is_reviewed) {
        this.is_reviewed = is_reviewed;
    }

    public String getDelivery_charges() {
        return delivery_charges;
    }

    public void setDelivery_charges(String delivery_charges) {
        this.delivery_charges = delivery_charges;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
