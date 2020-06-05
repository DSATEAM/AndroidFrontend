package edu.upc.eetac.dsa.lastsurvivorfrontend.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable{
    String id;
    String parentId;
    String username;
    String avatar;
    String message;


    protected Message(Parcel in) {
        id = in.readString();
        parentId = in.readString();
        username = in.readString();
        avatar = in.readString();
        message = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
    //Empty Constructor
    public Message(){}
    public Message(String username, String message, String parentId) {
        this.username = username;
        this.parentId=parentId;
        this.message=message;

    }

    @Override
    public String toString() {
        return "Message [username= " + username + ", message= " + message + ", id=" +id + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(parentId);
        parcel.writeString(username);
        parcel.writeString(avatar);
        parcel.writeString(message);
    }
}
