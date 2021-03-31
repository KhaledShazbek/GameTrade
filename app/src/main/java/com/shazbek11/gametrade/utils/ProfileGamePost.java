package com.shazbek11.gametrade.utils;

public class ProfileGamePost {

    public ProfileGamePost(String mID, String mGamename, String mPrice, String mUploadImage) {
        this.mID = mID;
        this.mGamename = mGamename;
        this.mPrice = mPrice;
        this.mUploadImage = mUploadImage;
    }

    private String mGamename;
    private String mPrice;
    private String mUploadImage;
    private String mID;

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

    public String getmUploadImage() {
        return mUploadImage;
    }

    public String getmID() { return mID; }

}
