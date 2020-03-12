package com.example.werayouapp.UtilsForChat;

import java.util.Date;

public class DisplayAllChat {

    String date;
    String id_expediteur;
    String id_recepteur;
    String dernier_message;
    String isnew;
    long serverTime;

    public DisplayAllChat() {

    }

    public DisplayAllChat(String date, String id_expediteur, String id_recepteur, String dernier_message, String isnew, long serverTime) {
        this.date = date;
        this.id_expediteur = id_expediteur;
        this.id_recepteur = id_recepteur;
        this.dernier_message = dernier_message;
        this.isnew = isnew;
        this.serverTime = serverTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId_expediteur() {
        return id_expediteur;
    }

    public void setId_expediteur(String id_expediteur) {
        this.id_expediteur = id_expediteur;
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

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }
}
