package edu.upc.eetac.dsa.lastsurvivorfrontend.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Player implements Parcelable{
    private String id;
    private String username;
    private String password;
    private int gamesPlayed;
    private int kills;
    private int experience;
    private int credits;
    private int maxFloor;
    private String avatar;
    private List<Item> listItems;

    public Player(String username, String password, int gamesPlayed, int kills, int experience, int credits) {
        this.username = username;
        this.password = password;
        this.gamesPlayed = gamesPlayed;
        this.kills = kills;
        this.experience = experience;
        this.credits = credits;
    }

    //Empty Constructor
    public Player(){}

    protected Player(Parcel in) {
        id = in.readString();
        username = in.readString();
        password = in.readString();
        gamesPlayed = in.readInt();
        kills = in.readInt();
        experience = in.readInt();
        credits = in.readInt();
        avatar = in.readString();
        listItems = in.createTypedArrayList(Item.CREATOR);
    }


    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public String getAvatar() {return avatar;}

    public void setAvatar(String avatar) {this.avatar = avatar;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public List<Item> getListItems() {
        return listItems;
    }

    public void setListItems(List<Item> listItems) {
        this.listItems = listItems;
    }
    public int getMaxFloor() {return maxFloor;}

    public void setMaxFloor(int maxFloor) {this.maxFloor = maxFloor;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeInt(gamesPlayed);
        dest.writeInt(kills);
        dest.writeInt(experience);
        dest.writeInt(credits);
        dest.writeInt(maxFloor);
        dest.writeString(avatar);
        dest.writeTypedList(listItems);
    }
}
