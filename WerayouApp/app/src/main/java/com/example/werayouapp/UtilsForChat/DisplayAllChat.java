package com.example.werayouapp.UtilsForChat;

public class DisplayAllChat {

    String date;
    String id_expediteur;
    String id_recepteur;
    String dernier_message;
    String isnew;

    public DisplayAllChat() {

    }

    public DisplayAllChat(String date, String id_expediteur, String id_recepteur, String dernier_message, String isnew) {
        this.date = date;
        this.id_expediteur = id_expediteur;
        this.id_recepteur = id_recepteur;
        this.dernier_message = dernier_message;
        this.isnew = isnew;
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
}
