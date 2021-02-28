package com.nayphilim.showcaseapp;

public class Project {
    private String Title, Category, Description, Credits, Repository, UploadDate;
    private String ImageUrls;

    public Project(){

    }

    public Project(String title, String category, String description, String credits, String repository, String uploadDate, String imageUrls){
        this.Title = title;
        this.Category = category;
        this.Description = description;
        this.Credits = credits;
        this.Repository = repository;
        this.UploadDate = uploadDate;
        this.ImageUrls = imageUrls;
    }


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCredits() {
        return Credits;
    }

    public void setCredits(String credits) {
        Credits = credits;
    }

    public String getRepository() {
        return Repository;
    }

    public void setRepository(String repository) {
        Repository = repository;
    }

    public String getUploadDate() {
        return UploadDate;
    }

    public void setUploadDate(String uploadDate) {
        UploadDate = uploadDate;
    }

    public String getImageUrls() {
        return ImageUrls;
    }

    public void setImageUrls(String imageUrls) {
        ImageUrls = imageUrls;
    }
}
