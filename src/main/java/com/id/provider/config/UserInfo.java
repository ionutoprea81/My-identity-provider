package com.id.provider.config;

public class UserInfo {
    private String userEmail;

    private String name;

    private String institution;
    private String authorities;

    public UserInfo(){

    }
    public UserInfo(String userEmail, String authorities, String name, String institution) {
        this.userEmail = userEmail;
        this.authorities = authorities;
        this.name = name;
        this.institution = institution;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }
}
