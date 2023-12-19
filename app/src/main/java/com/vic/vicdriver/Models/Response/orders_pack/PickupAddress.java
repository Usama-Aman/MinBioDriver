
package com.vic.vicdriver.Models.Response.orders_pack;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PickupAddress implements Parcelable {

    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("delivery_status")
    @Expose
    private String deliveryStatus;
    @SerializedName("is_reached")
    @Expose
    private boolean is_reached;

    protected PickupAddress(Parcel in) {
        company = in.readString();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        id = in.readInt();
        deliveryStatus = in.readString();
        is_reached = in.readByte() != 0;
    }

    public static final Creator<PickupAddress> CREATOR = new Creator<PickupAddress>() {
        @Override
        public PickupAddress createFromParcel(Parcel in) {
            return new PickupAddress(in);
        }

        @Override
        public PickupAddress[] newArray(int size) {
            return new PickupAddress[size];
        }
    };

    public boolean isIs_reached() {
        return is_reached;
    }

    public void setIs_reached(boolean is_reached) {
        this.is_reached = is_reached;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(company);
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeInt(id);
        dest.writeString(deliveryStatus);
        dest.writeByte((byte) (is_reached ? 1 : 0));
    }
}
