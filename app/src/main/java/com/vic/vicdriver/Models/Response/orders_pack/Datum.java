
package com.vic.vicdriver.Models.Response.orders_pack;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum implements Parcelable {

    @SerializedName("is_driver_review")
    @Expose
    private Integer isDriverReview;
    @SerializedName("is_driver_app_review")
    @Expose
    private Integer isDriverAppReview;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("buyer_order_number")
    @Expose
    private String buyerOrderNumber;
    @SerializedName("telephone")
    @Expose
    private String telephone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("delivery_address_id")
    @Expose
    private Integer deliveryAddressId;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("delivery_status")
    @Expose
    private String deliveryStatus;
    @SerializedName("product_total")
    @Expose
    private String productTotal;
    @SerializedName("product_discount")
    @Expose
    private String productDiscount;
    @SerializedName("product_sub_total")
    @Expose
    private String productSubTotal;
    @SerializedName("product_vat")
    @Expose
    private String productVat;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("buyer_company")
    @Expose
    private String buyerCompany;
    @SerializedName("merchant_company")
    @Expose
    private String merchantCompany;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("pickup_addresses")
    @Expose
    private ArrayList<PickupAddress> pickupAddresses = null;
    @SerializedName("items")
    @Expose
    private ArrayList<Item> items = null;
    @SerializedName("driver_amount")
    @Expose
    private String driverAmount;


    public Datum(Integer isDriverReview, Integer isDriverAppReview, String orderNumber, String buyerOrderNumber, String telephone, String email, String address, String latitude, String longitude, Integer deliveryAddressId, int userId, String deliveryStatus, String productTotal, String productDiscount, String productSubTotal, String productVat, String userImage, String buyerCompany, String merchantCompany, String date, String time, ArrayList<PickupAddress> pickupAddresses, ArrayList<Item> items, String driverAmount) {
        this.isDriverReview = isDriverReview;
        this.isDriverAppReview = isDriverAppReview;
        this.orderNumber = orderNumber;
        this.buyerOrderNumber = buyerOrderNumber;
        this.telephone = telephone;
        this.email = email;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryAddressId = deliveryAddressId;
        this.userId = userId;
        this.deliveryStatus = deliveryStatus;
        this.productTotal = productTotal;
        this.productDiscount = productDiscount;
        this.productSubTotal = productSubTotal;
        this.productVat = productVat;
        this.userImage = userImage;
        this.buyerCompany = buyerCompany;
        this.merchantCompany = merchantCompany;
        this.date = date;
        this.time = time;
        this.pickupAddresses = pickupAddresses;
        this.items = items;
        this.driverAmount = driverAmount;
    }

    protected Datum(Parcel in) {
        if (in.readByte() == 0) {
            isDriverReview = null;
        } else {
            isDriverReview = in.readInt();
        }
        if (in.readByte() == 0) {
            isDriverAppReview = null;
        } else {
            isDriverAppReview = in.readInt();
        }
        orderNumber = in.readString();
        buyerOrderNumber = in.readString();
        telephone = in.readString();
        email = in.readString();

        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        if (in.readByte() == 0) {
            deliveryAddressId = null;
        } else {
            deliveryAddressId = in.readInt();
        }
        userId = in.readInt();
        deliveryStatus = in.readString();
        productTotal = in.readString();
        productDiscount = in.readString();
        productSubTotal = in.readString();
        productVat = in.readString();
        userImage = in.readString();
        buyerCompany = in.readString();
        merchantCompany = in.readString();
        date = in.readString();
        time = in.readString();
        driverAmount = in.readString();
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

    public String getBuyerOrderNumber() {
        return buyerOrderNumber;
    }

    public void setBuyerOrderNumber(String buyerOrderNumber) {
        this.buyerOrderNumber = buyerOrderNumber;
    }

    public Integer getIsDriverReview() {
        return isDriverReview;
    }

    public void setIsDriverReview(Integer isDriverReview) {
        this.isDriverReview = isDriverReview;
    }

    public Integer getIsDriverAppReview() {
        return isDriverAppReview;
    }

    public void setIsDriverAppReview(Integer isDriverAppReview) {
        this.isDriverAppReview = isDriverAppReview;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Integer getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(Integer deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(String productTotal) {
        this.productTotal = productTotal;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getProductSubTotal() {
        return productSubTotal;
    }

    public void setProductSubTotal(String productSubTotal) {
        this.productSubTotal = productSubTotal;
    }

    public String getProductVat() {
        return productVat;
    }

    public void setProductVat(String productVat) {
        this.productVat = productVat;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getBuyerCompany() {
        return buyerCompany;
    }

    public void setBuyerCompany(String buyerCompany) {
        this.buyerCompany = buyerCompany;
    }

    public String getMerchantCompany() {
        return merchantCompany;
    }

    public void setMerchantCompany(String merchantCompany) {
        this.merchantCompany = merchantCompany;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<PickupAddress> getPickupAddresses() {
        return pickupAddresses;
    }

    public void setPickupAddresses(ArrayList<PickupAddress> pickupAddresses) {
        this.pickupAddresses = pickupAddresses;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public String getDriverAmount() {
        return driverAmount;
    }

    public void setDriverAmount(String driverAmount) {
        this.driverAmount = driverAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (isDriverReview == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isDriverReview);
        }
        if (isDriverAppReview == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isDriverAppReview);
        }
        dest.writeString(orderNumber);
        dest.writeString(buyerOrderNumber);
        dest.writeString(telephone);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
        if (deliveryAddressId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(deliveryAddressId);
        }
        dest.writeInt(userId);
        dest.writeString(deliveryStatus);
        dest.writeString(productTotal);
        dest.writeString(productDiscount);
        dest.writeString(productSubTotal);
        dest.writeString(productVat);
        dest.writeString(userImage);
        dest.writeString(buyerCompany);
        dest.writeString(merchantCompany);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(driverAmount);
    }
}
