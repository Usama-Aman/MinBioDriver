
package com.vic.vicdriver.Models.Response.ProductsPagination;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datum {

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
    @SerializedName("is_warranted")
    @Expose
    private Integer is_warranted;
    @SerializedName("details")
    @Expose
    private Details details;
    @SerializedName("sellers")
    @Expose
    private List<Object> sellers = null;

    public Datum(Integer id, String name, String imagePath, String productDescription, Integer categoryId, Integer is_warranted, Details details, List<Object> sellers) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.productDescription = productDescription;
        this.categoryId = categoryId;
        this.is_warranted = is_warranted;
        this.details = details;
        this.sellers = sellers;
    }

    public Integer getIs_warranted() {
        return is_warranted;
    }

    public void setIs_warranted(Integer is_warranted) {
        this.is_warranted = is_warranted;
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

    public List<Object> getSellers() {
        return sellers;
    }

    public void setSellers(List<Object> sellers) {
        this.sellers = sellers;
    }

}
