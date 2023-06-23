package com.example.myapplication;

public class Profile {
    private String name;
    private String phoneNo;
    private String location;
    private String salary;
    private String key;
    // ... other attributes and constructors



    public Profile() {
        // Default constructor required for Firebase
    }

    public Profile(String name, String phoneNo, String location, String salary) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.location = location;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
