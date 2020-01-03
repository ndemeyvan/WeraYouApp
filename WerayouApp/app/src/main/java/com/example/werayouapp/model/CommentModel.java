package com.example.werayouapp.model;

public class CommentModel {
    String createdDate;
    String commentaire;
    String id;

    public CommentModel() {
    }

    public CommentModel(String createdDate, String commentaire, String id) {
        this.createdDate = createdDate;
        this.commentaire = commentaire;
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
