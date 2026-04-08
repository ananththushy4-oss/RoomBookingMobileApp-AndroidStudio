package com.example.ecostayretreat.model;

/**
 * File: UserModel.java
 * Description: Model class representing a user in the database.
 */
public class UserModel {
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String nic;
    private String phone;
    private boolean isAdmin;

    // Default constructor is required for calls to DataSnapshot.getValue(UserModel.class)
    public UserModel() {
    }

    public UserModel(String uid, String firstName, String lastName, String email, String address, String nic, String phone) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.nic = nic;
        this.phone = phone;
        this.isAdmin = false; // Default user role
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}