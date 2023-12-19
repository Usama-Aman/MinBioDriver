
package com.vic.vicdriver.Models.Response.ChartData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Year {

    @SerializedName("averageprice")
    @Expose
    private String averageprice;
    @SerializedName("report")
    @Expose
    private List<Report> report = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Year() {
    }

    /**
     * 
     * @param averageprice
     * @param report
     */
    public Year(String averageprice, List<Report> report) {
        super();
        this.averageprice = averageprice;
        this.report = report;
    }

    public String getAverageprice() {
        return averageprice;
    }

    public void setAverageprice(String averageprice) {
        this.averageprice = averageprice;
    }

    public List<Report> getReport() {
        return report;
    }

    public void setReport(List<Report> report) {
        this.report = report;
    }

}
