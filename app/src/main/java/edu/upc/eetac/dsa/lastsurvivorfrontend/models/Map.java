package edu.upc.eetac.dsa.lastsurvivorfrontend.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Map implements Parcelable {
    private String id;
    private String name;
    private int level; //Indicates the level and the precedence of use in Unity
    private String type1Map;
    private String type2Objects;//All of the Object and Enemy Position

    public Map() {}
    //Complete Constructor

    public Map(String id, String name, int level, String type1Map,String type2Objects) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.type1Map = type1Map;
        this.type2Objects = type2Objects;
    }

    protected Map(Parcel in) {
        id = in.readString();
        name = in.readString();
        level = in.readInt();
        type1Map = in.readString();
        type2Objects = in.readString();
    }

    public static final Creator<Map> CREATOR = new Creator<Map>() {
        @Override
        public Map createFromParcel(Parcel in) {
            return new Map(in);
        }

        @Override
        public Map[] newArray(int size) {
            return new Map[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getType1Map() {
        return type1Map;
    }

    public void setType1Map(String type1Map) {
        this.type1Map = type1Map;
    }

    public String getType2Objects() {
        return type2Objects;
    }

    public void setType2Objects(String type2Objects) {
        this.type2Objects = type2Objects;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(level);
        dest.writeString(type1Map);
        dest.writeString(type2Objects);
    }
}
