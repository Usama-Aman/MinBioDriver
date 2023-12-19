package com.vic.vicdriver.Models.countries;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountriesResponse {
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CountriesResponse(Data data, Boolean status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }
}
