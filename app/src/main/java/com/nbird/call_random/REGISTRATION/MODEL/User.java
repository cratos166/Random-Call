package com.nbird.call_random.REGISTRATION.MODEL;

public class User {

    String name,imageURL,uid,dis,gender;


    public User() {
    }

    public User(String name, String imageURL, String uid, String gender,String dis) {
        this.name = name;
        this.imageURL = imageURL;
        this.uid = uid;
        this.gender = gender;
        this.dis = dis;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
