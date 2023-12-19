
package com.vic.vicdriver.Models.Response.Comments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rating {

    @SerializedName("pricing")
    @Expose
    private Integer pricing;
    @SerializedName("quality")
    @Expose
    private Integer quality;
    @SerializedName("availability")
    @Expose
    private Integer availability;
    @SerializedName("relation")
    @Expose
    private Integer relation;
    @SerializedName("trustrelation")
    @Expose
    private Integer trustrelation;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Rating() {
    }

    /**
     * 
     * @param trustrelation
     * @param availability
     * @param pricing
     * @param quality
     * @param relation
     */
    public Rating(Integer pricing, Integer quality, Integer availability, Integer relation, Integer trustrelation) {
        super();
        this.pricing = pricing;
        this.quality = quality;
        this.availability = availability;
        this.relation = relation;
        this.trustrelation = trustrelation;
    }

    public Integer getPricing() {
        return pricing;
    }

    public void setPricing(Integer pricing) {
        this.pricing = pricing;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }

    public Integer getTrustrelation() {
        return trustrelation;
    }

    public void setTrustrelation(Integer trustrelation) {
        this.trustrelation = trustrelation;
    }

}
