package com.vic.vicdriver.Models.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AcceptOrderRequest {


    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("delivery_address_id")
    @Expose
    private String delivery_address_id;

    public AcceptOrderRequest(String status, String delivery_address_id) {

        this.status = status;
        this.delivery_address_id = delivery_address_id;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelivery_address_id() {
        return delivery_address_id;
    }

    public void setDelivery_address_id(String delivery_address_id) {
        this.delivery_address_id = delivery_address_id;
    }
}
