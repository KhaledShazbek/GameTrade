package com.shazbek11.gametrade.utils;

public class VideoGamePost {


    public VideoGamePost(String mUsername, String mLocation, String mGamename, String mPrice, String mUserImage, String mUploadImage) {
        this.mUsername = mUsername;
        this.mLocation = mLocation;
        this.mGamename = mGamename;
        this.mPrice = mPrice;
        this.mUserImage = mUserImage;
        this.mUploadImage = mUploadImage;
    }

    private String mUsername;
    private String mLocation;
    private String mGamename;
    private String mPrice;
    private String mUserImage;
    private String mUploadImage;

    public String getmUsername() {
        return mUsername;
    }

    public String getmLocation() {
        return mLocation;
    }

    public String getmGamename() {
        return mGamename;
    }

    public String getmPrice() {
        if (!mPrice.equals("Trade")){
            return mPrice+" $";
        }else{
            return mPrice;
        }
    }

    public String getmUserImage() {
        return mUserImage;
    }

    public String getmUploadImage() {
        return mUploadImage;
    }



}
