
package com.vic.vicdriver.Models.Response.TransactionHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Extra {

    @SerializedName("pending")
    @Expose
    private String pending;
    @SerializedName("received")
    @Expose
    private String received;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Extra() {
    }

    /**
     * 
     * @param pending
     * @param received
     */
    public Extra(String pending, String received) {
        super();
        this.pending = pending;
        this.received = received;
    }

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

}
