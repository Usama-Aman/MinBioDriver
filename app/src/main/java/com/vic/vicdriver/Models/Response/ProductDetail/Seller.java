
package com.vic.vicdriver.Models.Response.ProductDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Seller {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("flag_image_path")
    @Expose
    private String flagImagePath;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("is_bio")
    @Expose
    private Integer isBio;
    @SerializedName("is_nego")
    @Expose
    private Integer isNego;
    @SerializedName("stock")
    @Expose
    private double stock;
    @SerializedName("sale_case")
    @Expose
    private Integer sale_case;
    @SerializedName("minprice")
    @Expose
    private double minprice;
    @SerializedName("maxprice")
    @Expose
    private double maxprice;
    @SerializedName("discount")
    @Expose
    private double discount;
    @SerializedName("vat")
    @Expose
    private double vat;
    @Expose
    private int hasStock;

    public Seller(String id, String name, String flagImagePath, Integer rating, String price, Integer isBio, Integer isNego, double stock, Integer sale_case, double minprice, double maxprice, double discount, double vat, int hasStock) {
        this.id = id;
        this.name = name;
        this.flagImagePath = flagImagePath;
        this.rating = rating;
        this.price = price;
        this.isBio = isBio;
        this.isNego = isNego;
        this.stock = stock;
        this.sale_case = sale_case;
        this.minprice = minprice;
        this.maxprice = maxprice;
        this.discount = discount;
        this.vat = vat;
        this.hasStock = hasStock;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public double getMinprice() {
        return minprice;
    }

    public void setMinprice(double minprice) {
        this.minprice = minprice;
    }

    public double getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(double maxprice) {
        this.maxprice = maxprice;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Integer getSale_case() {
        return sale_case;
    }

    public void setSale_case(Integer sale_case) {
        this.sale_case = sale_case;
    }

    public int getHasStock() {
        return hasStock;
    }

    public void setHasStock(int hasStock) {
        this.hasStock = hasStock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlagImagePath() {
        return flagImagePath;
    }

    public void setFlagImagePath(String flagImagePath) {
        this.flagImagePath = flagImagePath;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getIsBio() {
        return isBio;
    }

    public void setIsBio(Integer isBio) {
        this.isBio = isBio;
    }

    public Integer getIsNego() {
        return isNego;
    }

    public void setIsNego(Integer isNego) {
        this.isNego = isNego;
    }

}
