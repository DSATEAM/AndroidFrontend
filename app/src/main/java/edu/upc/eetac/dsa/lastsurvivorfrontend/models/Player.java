package edu.upc.eetac.dsa.lastsurvivorfrontend.models;

public class Player {
     private String username;
     private String password;

     public Player (String user, String pass){
         this.username=user;
         this.password=pass;
     }

     public String getUser(){
         return this.username;
     }
     public String getPassword(){
         return this.password;
     }
     public void setUsername(String user){
         this.username=user;
     }
     public void setPassword(String password){
         this.password=password;
     }


}
