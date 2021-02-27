package com.nayphilim.showcaseapp;

public class ProfileFeed {
    String Title, Category, Date;
    int PostPic;



    public ProfileFeed(String title, String category, String date, int postPic){
        this.Title = title;
        this.Category = category;
        this.Date = date;
        this.PostPic = postPic;
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

    public int getPostPic() {
        return PostPic;
    }

    public void setPostPic(int postPic) {
        PostPic = postPic;
    }
}
