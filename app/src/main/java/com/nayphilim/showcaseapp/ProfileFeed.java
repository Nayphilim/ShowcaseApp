package com.nayphilim.showcaseapp;

import android.net.Uri;

public class ProfileFeed {
    String Title, Category, Date, ProjectId;
    Uri PostPic;



    public ProfileFeed(String projectId, String title, String category, String date, Uri postPic){
        this.Title = title;
        this.Category = category;
        this.Date = date;
        this.PostPic = postPic;
        this.ProjectId = projectId;
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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Uri getPostPic() {
        return PostPic;
    }

    public void setPostPic(Uri postPic) {
        PostPic = postPic;
    }

    public String getProjectId() {
        return ProjectId;
    }

    public void setProjectId(String projectId) {
        ProjectId = projectId;
    }
}
