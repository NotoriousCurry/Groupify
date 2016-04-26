package com.tssssa.sgaheer.groupify;

/**
 * Created by sgaheer on 26/04/2016.
 */
public class GUser {
    private String gUsername;
    private String gEmail;
    private String gPassword;

    public GUser() {

    }

    public GUser(String gUsername, String gEmail, String gPassword) {
        this.gUsername = gUsername;
        this.gEmail = gEmail;
        this.gPassword =gPassword;
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
}
