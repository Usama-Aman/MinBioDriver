package com.vic.vicdriver.Models.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedBackRequest {
    @SerializedName("delivery_address_id")
    @Expose
    private String delivery_address_id;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("type")
    @Expose
    private String type;

    public FeedBackRequest(String delivery_address_id, String rating, String comment, String type) {
        this.delivery_address_id = delivery_address_id;
        this.rating = rating;
        this.comment = comment;
        this.type = type;
    }

    public String getDelivery_address_id() {
        return delivery_address_id;
    }

    public void setDelivery_address_id(String delivery_address_id) {
        this.delivery_address_id = delivery_address_id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
