
package com.vic.vicdriver.Models.Response.OrdersHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("product")
    @Expose
    private String product;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("subtotal")
    @Expose
    private String subtotal;
    @SerializedName("merchant")
    @Expose
    private String merchant;
    @SerializedName("unit")
    @Expose
    private String unit;

    public Product(String product, String quantity, String subtotal, String merchant, String unit) {
        this.product = product;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.merchant = merchant;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }


    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

}
