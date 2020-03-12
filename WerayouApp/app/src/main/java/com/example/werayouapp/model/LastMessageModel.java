package com.example.werayouapp.model;
import android.util.Log;

import java.util.Collections;
import java.util.Date;

public class LastMessageModel implements Comparable< LastMessageModel >{
    String id_recepteur;
    String dernier_message;
    String isnew;
    Long serverTime;


    public LastMessageModel() {
    }

    public LastMessageModel(String id_recepteur, String dernier_message, String isnew, Long serverTime) {
        this.id_recepteur = id_recepteur;
        this.dernier_message = dernier_message;
        this.isnew = isnew;
        this.serverTime = serverTime;
    }

    public String getId_recepteur() {
        return id_recepteur;
    }

    public void setId_recepteur(String id_recepteur) {
        this.id_recepteur = id_recepteur;
    }

    public String getDernier_message() {
        return dernier_message;
    }

    public void setDernier_message(String dernier_message) {
        this.dernier_message = dernier_message;
    }

    public String getIsnew() {
        return isnew;
    }

    public void setIsnew(String isnew) {
        this.isnew = isnew;
    }

    public Long getServerTime() {
        return serverTime;
    }

    public void setServerTime(Long serverTime) {
        this.serverTime = serverTime;
    }

//    @Override
//    public int compareTo(LastMessageModel o) {
//        int result = this.serverTime.compareTo(serverTime);
//        return  this.serverTime.compareTo(serverTime);
//    }

        @Override
    public int compareTo(LastMessageModel o) {
        int result=o.getServerTime().intValue();
        Log.i("ResultComparable",result+"");
        return result-this.serverTime.intValue();
    }


}
