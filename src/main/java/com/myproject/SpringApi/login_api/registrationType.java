package com.myproject.SpringApi.login_api;

public class registrationType {
    private String fName;
    private String lName;
    private String emailId;
    private String password;


    public String getPassword() {
        return password;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getFName() {
        return fName;
    }

    public String getLName() {
        return lName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public registrationType(String fName,String lName, String password, String emailId) {
        this.fName = fName;
        this.lName = lName;
        this.password = password;
        this.emailId = emailId;
    }

    @Override
    public String toString() {
        return "registrationType{" +
                "fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
