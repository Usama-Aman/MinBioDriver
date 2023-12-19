
package com.vic.vicdriver.Models.Response.TransactionHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_numbers")
    @Expose
    private String orderNumbers;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("driver_amount")
    @Expose
    private String driverAmount;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("date")
    @Expose
    private String date;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Datum() {
    }

    /**
     * 
     * @param date
     * @param orderNumbers
     * @param address
     * @param driverAmount
     * @param id
     * @param paymentStatus
     */
    public Datum(Integer id, String orderNumbers, String address, String driverAmount, String paymentStatus, String date) {
        super();
        this.id = id;
        this.orderNumbers = orderNumbers;
        this.address = address;
        this.driverAmount = driverAmount;
        this.paymentStatus = paymentStatus;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNumbers() {
        return orderNumbers;
    }

    public void setOrderNumbers(String orderNumbers) {
        this.orderNumbers = orderNumbers;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDriverAmount() {
        return driverAmount;
    }

    public void setDriverAmount(String driverAmount) {
        this.driverAmount = driverAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
