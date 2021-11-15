package com.example.journeyjournal;

//Getter/Setters and Constructors
public class Data {
     String title;
     String imgUrl;
     String description;
     String journeyID;
     String date_time;


    public Data(){}

    public Data(String title, String imgUrl, String journeyID, String timestamp) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.journeyID = journeyID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(String journeyID) {
        this.journeyID = journeyID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }


}
