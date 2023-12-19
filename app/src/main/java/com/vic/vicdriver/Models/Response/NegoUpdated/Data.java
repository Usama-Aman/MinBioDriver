
package com.vic.vicdriver.Models.Response.NegoUpdated;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Data implements Parcelable {

    @SerializedName("delivery_charges")
    @Expose
    private String deliveryCharges;
    @SerializedName("merchant_name")
    @Expose
    private String merchantName;
    @SerializedName("minprice")
    @Expose
    private double minprice;
    @SerializedName("maxprice")
    @Expose
    private double maxprice;
    @Expose
    private String merchantId;
    @Expose
    private String productId;
    @SerializedName("negotiations")
    @Expose
    private ArrayList<NegotiationList> negotiations = null;

    public Data(String deliveryCharges, String merchantName, double minprice, double maxprice, String merchantId, String productId, ArrayList<NegotiationList> negotiations) {
        this.deliveryCharges = deliveryCharges;
        this.merchantName = merchantName;
        this.minprice = minprice;
        this.maxprice = maxprice;
        this.merchantId = merchantId;
        this.productId = productId;
        this.negotiations = negotiations;
    }

    public Data(String deliveryCharges, String merchantName, ArrayList<NegotiationList> negotiations) {
        super();
        this.deliveryCharges = deliveryCharges;
        this.merchantName = merchantName;
        this.negotiations = negotiations;
    }

    protected Data(Parcel in) {
        deliveryCharges = in.readString();
        merchantName = in.readString();
        minprice = in.readDouble();
        maxprice = in.readDouble();
        merchantId = in.readString();
        productId = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public double getMinprice() {
        return minprice;
    }

    public void setMinprice(double minprice) {
        this.minprice = minprice;
    }

    public double getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(double maxprice) {
        this.maxprice = maxprice;
    }

    public static Creator<Data> getCREATOR() {
        return CREATOR;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public ArrayList<NegotiationList> getNegotiations() {
        return negotiations;
    }

    public void setNegotiations(ArrayList<NegotiationList> negotiations) {
        this.negotiations = negotiations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(deliveryCharges);
        parcel.writeString(merchantName);
    }
}
