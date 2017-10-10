package com.jjmrive.eplug;


import java.io.Serializable;

public class Plug implements Serializable{

    private double latitude;
    private double longitude;
    private String name;
    private String description;
    private String urlPhoto;
    private boolean free;

    public Plug(double latitude, double longitude, String name, String description, String urlPhoto, boolean free){
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.description = description;
        this.urlPhoto = urlPhoto;
        this.free = free;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public boolean isFree() {
        return free;
    }

    @Override
    public boolean equals(Object o){
        boolean eq = false;
        Plug vs = (Plug) o;
        if ((this.name.equals(vs.name)) && (this.latitude == vs.latitude) && (this.longitude == vs.longitude)){
            eq = true;
        }
        return eq;
    }
}
