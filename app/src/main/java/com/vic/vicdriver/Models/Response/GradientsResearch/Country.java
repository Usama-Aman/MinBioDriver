
package com.vic.vicdriver.Models.Response.GradientsResearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_fr")
    @Expose
    private String nameFr;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @Expose
    private boolean isSelected;

    public Country(Integer id, String name, String nameFr, String imagePath, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.nameFr = nameFr;
        this.imagePath = imagePath;
        this.isSelected = isSelected;
    }

    public String getNameFr() {
        return nameFr;
    }

    public void setNameFr(String nameFr) {
        this.nameFr = nameFr;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

}
