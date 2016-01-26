package com.technopark.bulat.advandroidhomework3.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bulat on 08.11.15.
 */
public class User implements Parcelable {
    public static String descriptionKey = "USER_DESCRIPTION_KEY";
    private String myid;
    private String uid;
    private String login;
    private String nick;
    private String status;
    private String email;
    private String phone;
    private String picture;

    public User() {
    }

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    protected User(Parcel in) {
        myid = in.readString();
        uid = in.readString();
        login = in.readString();
        nick = in.readString();
        status = in.readString();
        email = in.readString();
        phone = in.readString();
        picture = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myid);
        dest.writeString(uid);
        dest.writeString(login);
        dest.writeString(nick);
        dest.writeString(status);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(picture);
    }
}
