
package com.vic.vicdriver.Models.Response.OrdersHistory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("grandtotal")
    @Expose
    private String grandtotal;
    @SerializedName("subtotal")
    @Expose
    private String subtotal;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("details")
    @Expose
    private Details details;


    public Datum(Integer id, String date, String grandtotal, String subtotal, String status, Details details) {
        this.id = id;
        this.date = date;
        this.grandtotal = grandtotal;
        this.subtotal = subtotal;
        this.status = status;
        this.details = details;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public static Creator<Datum> getCREATOR() {
        return CREATOR;
    }

    protected Datum(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        date = in.readString();
        grandtotal = in.readString();
        status = in.readString();
    }

    public static final Creator<Datum> CREATOR = new Creator<Datum>() {
        @Override
        public Datum createFromParcel(Parcel in) {
            return new Datum(in);
        }

        @Override
        public Datum[] newArray(int size) {
            return new Datum[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGrandtotal() {
        return grandtotal;
    }

    public void setGrandtotal(String grandtotal) {
        this.grandtotal = grandtotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(date);
        parcel.writeString(grandtotal);
        parcel.writeString(status);
    }
}
