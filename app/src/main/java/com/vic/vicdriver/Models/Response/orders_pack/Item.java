
package com.vic.vicdriver.Models.Response.orders_pack;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("product_category")
    @Expose
    private String productCategory;
    @SerializedName("product_variety")
    @Expose
    private String productVariety;
    @SerializedName("quantity")
    @Expose
    private float quantity;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("vat")
    @Expose
    private String vat;
    @SerializedName("total")
    @Expose
    private String total;


    public Item(Integer id, Integer orderId, String productCategory, String productVariety, float quantity, String unit, String price, String discount, String vat, String total) {
        this.id = id;
        this.orderId = orderId;
        this.productCategory = productCategory;
        this.productVariety = productVariety;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.discount = discount;
        this.vat = vat;
        this.total = total;
    }


    protected Item(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            orderId = null;
        } else {
            orderId = in.readInt();
        }
        productCategory = in.readString();
        productVariety = in.readString();
        if (in.readByte() == 0) {
            quantity = 0;
        } else {
            quantity = in.readFloat();
        }
        unit = in.readString();
        price = in.readString();
        discount = in.readString();
        vat = in.readString();
        total = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductVariety() {
        return productVariety;
    }

    public void setProductVariety(String productVariety) {
        this.productVariety = productVariety;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (orderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(orderId);
        }
        dest.writeString(productCategory);
        dest.writeString(productVariety);
        if (quantity == 0) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(quantity);
        }
        dest.writeString(unit);
        dest.writeString(price);
        dest.writeString(discount);
        dest.writeString(vat);
        dest.writeString(total);
    }
}
