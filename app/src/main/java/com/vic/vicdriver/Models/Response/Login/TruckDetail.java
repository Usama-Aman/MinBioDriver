package com.vic.vicdriver.Models.Response.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TruckDetail {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("truck_plate")
    @Expose
    private String truckPlate;
    @SerializedName("proof_of_truck_id")
    @Expose
    private String proofOfTruckId;
    @SerializedName("id_card_front")
    @Expose
    private String idCardFront;
    @SerializedName("id_card_back")
    @Expose
    private String idCardBack;
    @SerializedName("proof_of_license_id")
    @Expose
    private String proofOfLicenseId;
    @SerializedName("proof_of_insurance")
    @Expose
    private String proofOfInsurance;
    @SerializedName("driver_id")
    @Expose
    private Integer driverId;
    @SerializedName("truck_plate_path")
    @Expose
    private String truckPlatePath;
    @SerializedName("proof_of_truck_id_path")
    @Expose
    private String proofOfTruckIdPath;
    @SerializedName("id_card_front_path")
    @Expose
    private String idCardFrontPath;
    @SerializedName("id_card_back_path")
    @Expose
    private String idCardBackPath;
    @SerializedName("proof_of_license_id_path")
    @Expose
    private String proofOfLicenseIdPath;
    @SerializedName("proof_of_insurance_path")
    @Expose
    private String proofOfInsurancePath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTruckPlate() {
        return truckPlate;
    }

    public void setTruckPlate(String truckPlate) {
        this.truckPlate = truckPlate;
    }

    public String getProofOfTruckId() {
        return proofOfTruckId;
    }

    public void setProofOfTruckId(String proofOfTruckId) {
        this.proofOfTruckId = proofOfTruckId;
    }

    public String getIdCardFront() {
        return idCardFront;
    }

    public void setIdCardFront(String idCardFront) {
        this.idCardFront = idCardFront;
    }

    public String getIdCardBack() {
        return idCardBack;
    }

    public void setIdCardBack(String idCardBack) {
        this.idCardBack = idCardBack;
    }

    public String getProofOfLicenseId() {
        return proofOfLicenseId;
    }

    public void setProofOfLicenseId(String proofOfLicenseId) {
        this.proofOfLicenseId = proofOfLicenseId;
    }

    public String getProofOfInsurance() {
        return proofOfInsurance;
    }

    public void setProofOfInsurance(String proofOfInsurance) {
        this.proofOfInsurance = proofOfInsurance;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getTruckPlatePath() {
        return truckPlatePath;
    }

    public void setTruckPlatePath(String truckPlatePath) {
        this.truckPlatePath = truckPlatePath;
    }

    public String getProofOfTruckIdPath() {
        return proofOfTruckIdPath;
    }

    public void setProofOfTruckIdPath(String proofOfTruckIdPath) {
        this.proofOfTruckIdPath = proofOfTruckIdPath;
    }

    public String getIdCardFrontPath() {
        return idCardFrontPath;
    }

    public void setIdCardFrontPath(String idCardFrontPath) {
        this.idCardFrontPath = idCardFrontPath;
    }

    public String getIdCardBackPath() {
        return idCardBackPath;
    }

    public void setIdCardBackPath(String idCardBackPath) {
        this.idCardBackPath = idCardBackPath;
    }

    public String getProofOfLicenseIdPath() {
        return proofOfLicenseIdPath;
    }

    public void setProofOfLicenseIdPath(String proofOfLicenseIdPath) {
        this.proofOfLicenseIdPath = proofOfLicenseIdPath;
    }

    public String getProofOfInsurancePath() {
        return proofOfInsurancePath;
    }

    public void setProofOfInsurancePath(String proofOfInsurancePath) {
        this.proofOfInsurancePath = proofOfInsurancePath;
    }

}
