package com.example.werayouapp.model;

public class LastMessageModel {
    String id_recepteur;
    String dernier_message;

    public LastMessageModel() {
    }

    public LastMessageModel(String id_recepteur, String dernier_message) {
        this.id_recepteur = id_recepteur;
        this.dernier_message = dernier_message;
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
}
