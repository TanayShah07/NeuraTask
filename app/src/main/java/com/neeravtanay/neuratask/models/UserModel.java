package com.neeravtanay.neuratask.models;

public class UserModel {
    public String uid;
    public String name;
    public String email;
    public int age;
    public String photoUrl;
    public UserModel() {}
    public UserModel(String uid, String name, String email, int age, String photoUrl) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.age = age;
        this.photoUrl = photoUrl;
    }
}
