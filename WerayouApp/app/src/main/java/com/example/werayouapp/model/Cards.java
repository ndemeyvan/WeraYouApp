package com.example.werayouapp.model;

public class Cards {
    private String nom;
    private String prenom;
    private String image;
    private String id;
    private String pays;
    private String ville;
    private String apropos;
    private String age;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Cards(String nom, String prenom, String image, String id, String pays, String ville, String apropos, String age) {
        this.nom = nom;
        this.prenom = prenom;
        this.image = image;
        this.id = id;
        this.pays = pays;
        this.ville = ville;
        this.apropos = apropos;
        this.age = age;
    }

    public String getApropos() {
        return apropos;
    }

    public void setApropos(String apropos) {
        this.apropos = apropos;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Cards() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
