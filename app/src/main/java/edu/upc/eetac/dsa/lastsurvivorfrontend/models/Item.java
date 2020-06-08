package edu.upc.eetac.dsa.lastsurvivorfrontend.models;

import android.os.Parcel;
import android.os.Parcelable;
public class Item implements Parcelable {

    private String id;
    private String parentId ="";
    private String name;
    private String type;
    private String rarity;
    private int credit;
    private String description;
    private int offense;
    private int defense;
    private float hitRange;
    private float attackCooldown;
    //Empty Constructor
    public Item() {
    }
    public Item(String parentId, String name, String type, String rarity, int credit, String description, int offense, int defense, float hitRange, float attackCooldown) {
        this.parentId = parentId;
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.credit = credit;
        this.description = description;
        this.offense = offense;
        this.defense = defense;
        this.hitRange = hitRange;
        this.attackCooldown = attackCooldown;
    }

    protected Item(Parcel in) {
        id = in.readString();
        parentId = in.readString();
        name = in.readString();
        type = in.readString();
        rarity = in.readString();
        credit = in.readInt();
        description = in.readString();
        offense = in.readInt();
        defense = in.readInt();
        hitRange = in.readFloat();
        attackCooldown = in.readFloat();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() { return parentId;}

    public void setParentId(String parentId) {this.parentId = parentId;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRarity() {
        return rarity;
    }
    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {this.credit = credit;}

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public int getOffense() {
        return offense;
    }

    public void setOffense(int offense) {
        this.offense = offense;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public float getHitRange() {
        return hitRange;
    }

    public void setHitRange(float hitRange) {
        this.hitRange = hitRange;
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(float attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(parentId);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(rarity);
        dest.writeInt(credit);
        dest.writeString(description);
        dest.writeInt(offense);
        dest.writeInt(defense);
        dest.writeFloat(hitRange);
        dest.writeFloat(attackCooldown);
    }
}
