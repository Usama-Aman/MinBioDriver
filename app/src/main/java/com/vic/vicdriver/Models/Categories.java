package com.vic.vicdriver.Models;

import java.util.ArrayList;

public class Categories {

    private Integer id;
    private String name;
    private String imagePath;
    private int type;
    private ArrayList<SubCategory> subCategories;

    public Categories(Integer id, String name, String imagePath, int type, ArrayList<SubCategory> subCategories) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.type = type;
        this.subCategories = subCategories;
    }

    public Categories(Integer id, String name, String imagePath, int type) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.type = type;
    }

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
