package com.vic.vicdriver.Models.countries;

import com.vic.vicdriver.Models.Response.GradientsResearch.Category;
import com.vic.vicdriver.Models.Response.GradientsResearch.Country;
import com.vic.vicdriver.Models.Response.GradientsResearch.Size;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;
    @SerializedName("countries")
    @Expose
    private List<Country> countries = null;
    @SerializedName("classes")
    @Expose
    private List<Class> classes = null;
    @SerializedName("sizes")
    @Expose
    private List<Size> sizes = null;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }
}