package edu.upc.eetac.dsa.lastsurvivorfrontend.models;

import java.util.LinkedList;
import java.util.List;

public class Player {
    private String id;
    private String username;
    private String password;
    private int gamesPlayed;
    private int kills;
    private int deaths;
    private int experience;
    private int wins;
    //TODO Recursive Mapping of Objects Item and Material
    List<Item> listItems;
    List<Material> listMaterials;
    //Empty Constructor
    public Player(){}
    public Player(String username, String password, int gamesPlayed, int kills, int deaths, int experience, int wins) {
        this.username = username;
        this.password = password;
        this.gamesPlayed = gamesPlayed;
        this.kills = kills;
        this.deaths = deaths;
        this.experience = experience;
        this.wins = wins;
    }
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

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public List<Item> getListItems() {
        return listItems;
    }

    public void setListItems(List<Item> listItems) {
        this.listItems = listItems;
    }

    public List<Material> getListMaterials() {
        return listMaterials;
    }

    public void setListMaterials(List<Material> listMaterials) {
        this.listMaterials = listMaterials;
    }

}
