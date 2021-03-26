package com.nayphilim.showcaseapp;

public class Project {
    private String Title, Category, Description, Credits, Repository, UploadDate, User, ProjectList;
    private String ImageUrls;



    public Project(){

    }

    public Project(String title, String category, String description, String credits, String repository, String uploadDate, String imageUrls, String user){
        this.Title = title;
        this.Category = category;
        this.Description = description;
        this.Credits = credits;
        this.Repository = repository;
        this.UploadDate = uploadDate;
        this.ImageUrls = imageUrls;
        this.User = user;
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

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getProjectList() {
        return ProjectList;
    }

    public void setProjectList(String projectList) {
        ProjectList = projectList;
    }
}
