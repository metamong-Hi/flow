package com.example.myapplication.data;

public class Call {
    private int userImg;
    private String name;
    private String phoneNum;
    private int diffImg;


    public Call(int userImg, String name, String phoneNum, int diffImg){
        this.userImg = userImg;
        this.name = name;
        this.phoneNum = phoneNum;
        this.diffImg = diffImg;
    }

    public int getUserImg() {
        return userImg;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public int getDiffImg() {return diffImg;}
}
