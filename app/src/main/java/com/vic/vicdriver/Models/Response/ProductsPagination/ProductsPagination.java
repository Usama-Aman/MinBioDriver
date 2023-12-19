
package com.vic.vicdriver.Models.Response.ProductsPagination;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductsPagination implements Parcelable {

    @SerializedName("data")
    @Expose
    private ArrayList<Datum> data = null;
    @SerializedName("links")
    @Expose
    private Links links;
    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;

    public ProductsPagination(ArrayList<Datum> data, Links links, Meta meta, Boolean status, String message) {
        super();
        this.data = data;
        this.links = links;
        this.meta = meta;
        this.status = status;
        this.message = message;
    }

    protected ProductsPagination(Parcel in) {
        byte tmpStatus = in.readByte();
        status = tmpStatus == 0 ? null : tmpStatus == 1;
        message = in.readString();
    }

    public static final Creator<ProductsPagination> CREATOR = new Creator<ProductsPagination>() {
        @Override
        public ProductsPagination createFromParcel(Parcel in) {
            return new ProductsPagination(in);
        }

        @Override
        public ProductsPagination[] newArray(int size) {
            return new ProductsPagination[size];
        }
    };

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (status == null ? 0 : status ? 1 : 2));
        parcel.writeString(message);
    }
}
