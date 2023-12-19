package com.vic.vicdriver.Models;

public class CartModel {

    private int session_id;
    private int product_id;
    private int marchant_id;
    private double quantity;
    private String price;
    private String product_name;
    private String product_description;
    private String marchant_name;
    private String product_image;
    private double stock;
    private String unit;
    private String discount;
    private String vat;

    public CartModel(int session_id, int product_id, int marchant_id, double quantity, String price, String product_name, String product_description, String marchant_name, String product_image, double stock, String unit, String discount, String vat) {
        this.session_id = session_id;
        this.product_id = product_id;
        this.marchant_id = marchant_id;
        this.quantity = quantity;
        this.price = price;
        this.product_name = product_name;
        this.product_description = product_description;
        this.marchant_name = marchant_name;
        this.product_image = product_image;
        this.stock = stock;
        this.unit = unit;
        this.discount = discount;
        this.vat = vat;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getMarchant_id() {
        return marchant_id;
    }

    public void setMarchant_id(int marchant_id) {
        this.marchant_id = marchant_id;
    }



    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getMarchant_name() {
        return marchant_name;
    }

    public void setMarchant_name(String marchant_name) {
        this.marchant_name = marchant_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }
}
