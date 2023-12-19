
package com.vic.vicdriver.Models.Response.GradientsResearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Size {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sign")
    @Expose
    private String sign;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Size() {
    }

    /**
     * 
     * @param name
     * @param sign
     * @param id
     */
    public Size(Integer id, String name, String sign) {
        super();
        this.id = id;
        this.name = name;
        this.sign = sign;
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
