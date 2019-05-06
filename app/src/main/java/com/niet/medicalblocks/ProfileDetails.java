package com.niet.medicalblocks;

public class ProfileDetails {
    private String user_type;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String uid;
    private String image_url;
    private String doc_name;

    public ProfileDetails() {
    }

    public ProfileDetails(String user_type, String name, String email, String phone, String address, String uid, String image_url) {
        this.user_type = user_type;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.uid = uid;
        this.image_url = image_url;
    }
    public ProfileDetails(String user_type, String name, String email, String phone, String address, String uid, String image_url, String doc_name ) {
        this.user_type = user_type;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.uid = uid;
        this.image_url = image_url;
        this.doc_name = doc_name;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }
}
