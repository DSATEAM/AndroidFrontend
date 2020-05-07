package edu.upc.eetac.dsa.lastsurvivorfrontend.models;

import java.util.LinkedList;
import java.util.List;

public class Item {

    private String id;
    private String parentId ="";
    private String name;
    private String type;
    private String rarity;
    private String description;
    List<Material> listMaterials;
    private int offense;
    private int defense;
    //Empty Constructor
    public Item() {
    }
    public Item(String parentId, String name, String type, String rarity, String description, List<Material> listMaterials, int offense, int defense) {
        this.parentId = parentId;
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.description = description;
        this.listMaterials = listMaterials;
        this.offense = offense;
        this.defense = defense;
    }
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

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Material> getListMaterials() {
        return listMaterials;
    }

    public void setListMaterials(List<Material> listMaterials) {this.listMaterials = listMaterials;
    }
    public void addListMaterial(Material material){
        if(this.listMaterials==null){
            this.listMaterials = new LinkedList<Material>();
            this.listMaterials.add(material);
        }else{
            this.listMaterials.add(material);
        }
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
}
