package com.trio.picturewall.entity;

import java.util.ArrayList;

public class Picture {

    String imageCode = null;
    ArrayList<String> imageUrlList = null;

    public Picture(){

    }
    public Picture(String imageCode,ArrayList<String> imageUrlList){
        this.imageCode = imageCode;
        this.imageUrlList = imageUrlList;
    }

    public String getImageCode(){
        return imageCode;
    }
    public void setImageCode(String imageCode){
        this.imageCode = imageCode;
    }
    public ArrayList<String> getImageUrlList(){
        return imageUrlList;
    }
    public void setImageUrlList(ArrayList<String> imageUrlList){
        this.imageUrlList = imageUrlList;
    }

    public String toString() {
        return "Picture{" +
                "imageCode='" + imageCode + '\'' +
                ", imageUrlList='" + imageUrlList + '\'' +
                '}';
    }


}
