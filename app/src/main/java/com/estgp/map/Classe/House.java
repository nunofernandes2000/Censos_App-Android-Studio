package com.estgp.map.Classe;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


////Classe House
public class House implements Serializable {

    private String houseOwner;
    private LocalDate dateLimit;
    private Boolean deliveryStatus;
    private Boolean submitted;
    private Double latitude;
    private Double Longitude;

    public House() {
    }

    public House(String houseOwner, LocalDate dateLimit, Boolean deliveryStatus, Boolean submitted, Double latitude, Double longitude) {
        this.houseOwner = houseOwner;
        this.dateLimit = dateLimit;
        this.deliveryStatus = deliveryStatus;
        this.submitted = submitted;
        this.latitude = latitude;
        Longitude = longitude;
    }

    public String getHouseOwner() {
        return houseOwner;
    }

    public void setHouseOwner(String houseOwner) {
        this.houseOwner = houseOwner;
    }

    public LocalDate getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(LocalDate dateLimit) {
        this.dateLimit = dateLimit;
    }

    public Boolean getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Boolean deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Boolean getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Boolean submitted) {
        this.submitted = submitted;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public String toString() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //formato da data

        //retorna na forma de string os dados do objeto
        return
                " houseOwner: " + (houseOwner == null ? "Sem informação" : houseOwner) + "\n" +
                " dateLimit: " + (dateLimit == null ? "Sem informação" : dateLimit.format(formatter)) + "\n" +
                " deliveryStatus: " + (deliveryStatus == false ? "Not delivered" : "Delivered") + "\n" +
                " submitted : "  + (submitted == false  ? "Not Submitted" :  "Submitted") + "\n" +
                " latitude: " + (latitude == null ? "Not defined" : latitude) + "\n" +
                " Longitude: " + (Longitude == null ? "Not defined" : Longitude ) + "\n";


    }
}
