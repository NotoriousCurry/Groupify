package com.tssssa.sgaheer.groupify;

/**
 * Created by sgaheer on 26/04/2016.
 * This is a constructor class for users
 */
public class GUser {
    private String idNumber;
    private String gUsername;
    private String gEmail;
    private String gPassword;
    private String gCourse;
    private String onCampus;

    public GUser() {

    }

    public GUser(String gUsername, String gEmail, String gPassword, String gCourse, String onCampus) {
        this.gUsername = gUsername;
        this.gEmail = gEmail;
        this.gPassword =gPassword;
        this.idNumber = "";
        this.gCourse = gCourse;
        this.onCampus = onCampus;
    }

    public String getUsername() {
        return gUsername;
    }

    public String getEmail() {
        return gEmail;
    }

    public String getPassword() {
        return gPassword;
    }

    public String getOnCampus() {
        return onCampus;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getgCourse() {
        return gCourse;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setOnCampus(String onCampus) {
        this.onCampus = onCampus;
    }
}
