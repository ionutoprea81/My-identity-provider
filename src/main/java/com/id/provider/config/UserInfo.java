package com.id.provider.config;

public class UserInfo {
    private String userEmail;
    private String authorities;

    public UserInfo(){

    }
    public UserInfo(String userEmail, String authorities) {
        this.userEmail = userEmail;
        this.authorities = authorities;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }
}
