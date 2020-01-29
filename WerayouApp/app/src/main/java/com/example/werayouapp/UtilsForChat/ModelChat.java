package com.example.werayouapp.UtilsForChat;

public class ModelChat {

    String expediteur;
    String recepteur;
    String message;
    String createdDate;
    String type;
    String image;

    public ModelChat() {
    }

    public ModelChat(String expediteur, String recepteur, String message, String createdDate, String type, String image) {
        this.expediteur = expediteur;
        this.recepteur = recepteur;
        this.message = message;
        this.createdDate = createdDate;
        this.type = type;
        this.image = image;
    }

    public String getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }

    public String getRecepteur() {
        return recepteur;
    }

    public void setRecepteur(String recepteur) {
        this.recepteur = recepteur;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
