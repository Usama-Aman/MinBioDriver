
package com.vic.vicdriver.Models.Response.ProductDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("product_description")
    @Expose
    private String productDescription;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("details")
    @Expose
    private Details details;
    @SerializedName("sellers")
    @Expose
    private ArrayList<Seller> sellers = null;
    @SerializedName("unit")
    @Expose
    private String unit;

    public Data(Integer id, String name, String imagePath, String productDescription, Integer categoryId, Details details, ArrayList<Seller> sellers, String unit) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.productDescription = productDescription;
        this.categoryId = categoryId;
        this.details = details;
        this.sellers = sellers;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public ArrayList<Seller> getSellers() {
        return sellers;
    }

    public void setSellers(ArrayList<Seller> sellers) {
        this.sellers = sellers;
    }

}
