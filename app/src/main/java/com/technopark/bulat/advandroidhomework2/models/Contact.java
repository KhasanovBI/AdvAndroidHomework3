package com.technopark.bulat.advandroidhomework2.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by bulat on 21.01.16.
 */
public class Contact implements Parcelable {
    private int myid;
    private String name;
    private String phone;
    private String email;

    public Contact(int myid, String name, String phone, String email) {
        this.myid = myid;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public Contact() {
    }

    public int getMyid() {
        return myid;
    }

    public void setMyid(int myid) {
        this.myid = myid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
