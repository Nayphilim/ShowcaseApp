package com.nayphilim.showcaseapp;

import android.net.Uri;

public class FeedbackFeed {
    String ProjectTitle, UserName, Date, FeedbackId, FeedbackDialog, ProjectId;
    Uri PostPic;



    public FeedbackFeed(String feedbackId, String title, String projectId, String userName, String date, Uri postPic, String feedbackDialog){
        this.ProjectTitle = title;
        this.UserName = userName;
        this.Date = date;
        this.PostPic = postPic;
        this.FeedbackId = feedbackId;
        this.FeedbackDialog = feedbackDialog;
        this.ProjectId = projectId;
    }

    public String getProjectTitle() {
        return ProjectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.ProjectTitle = projectTitle;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
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

    public String getFeedbackId() {
        return FeedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        FeedbackId = feedbackId;
    }

    public String getFeedbackDialog() {
        return FeedbackDialog;
    }

    public void setFeedbackDialog(String feedbackDialog) {
        FeedbackDialog = feedbackDialog;
    }

    public String getProjectId() {
        return ProjectId;
    }

    public void setProjectId(String projectId) {
        ProjectId = projectId;
    }
}
