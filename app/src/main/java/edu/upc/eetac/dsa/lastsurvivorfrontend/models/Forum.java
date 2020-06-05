package edu.upc.eetac.dsa.lastsurvivorfrontend.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Forum implements Parcelable{
    private String id;
    private String admin;
    private String name;
    private String avatar;
    private List<Message> listMessages;


    protected Forum(Parcel in) {
        id = in.readString();
        admin = in.readString();
        name = in.readString();
        avatar = in.readString();
        listMessages = in.createTypedArrayList(Message.CREATOR);
    }

    public static final Creator<Forum> CREATOR = new Creator<Forum>() {
        @Override
        public Forum createFromParcel(Parcel in) {
            return new Forum(in);
        }

        @Override
        public Forum[] newArray(int size) {
            return new Forum[size];
        }
    };

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public List<Message> getListMessages() { return this.listMessages; }
    public void setListMessages(List<Message> listMessages) { this.listMessages = listMessages; }
    public void addMessage(Message message) { this.listMessages.add(message); }

    //Empty Constructor
    public Forum(){}
    public Forum(String name) {
        this.name=name;
    }

    @Override
    public String toString() {
        return "Forum [name= " + this.name + ", id=" + this.id + ",creator="+ this.admin+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(admin);
        parcel.writeString(name);
        parcel.writeString(avatar);
        parcel.writeTypedList(listMessages);
    }
}
