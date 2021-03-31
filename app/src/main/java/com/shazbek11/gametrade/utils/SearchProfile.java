package com.shazbek11.gametrade.utils;

public class SearchProfile {

    public SearchProfile(String mUsername, String mUserProfilePic, String mUserEmail, String mUserPhoneNumber, String mUserLocation) {
        this.mUsername = mUsername;
        this.mUserProfilePic = mUserProfilePic;
        this.mUserEmail = mUserEmail;
        this.mUserPhoneNumber = mUserPhoneNumber;
        this.mUserLocation = mUserLocation;
    }

    public String getmUsername() {
        return mUsername;
    }

    public String getmUserProfilePic() {
        return mUserProfilePic;
    }

    public String getmUserEmail() {
        return mUserEmail;
    }

    public String getmUserPhoneNumber() {
        return mUserPhoneNumber;
    }

    public String getmUserLocation() {
        return mUserLocation;
    }

    private String mUsername;
    private String mUserProfilePic;
    private String mUserEmail;
    private String mUserPhoneNumber;
    private String mUserLocation;
}
