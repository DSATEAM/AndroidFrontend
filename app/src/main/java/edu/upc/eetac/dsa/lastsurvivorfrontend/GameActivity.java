package edu.upc.eetac.dsa.lastsurvivorfrontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Item;
import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Map;
import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;

public class GameActivity extends AppCompatActivity {
    boolean isLaunchingFirstTime = true;
    Player player = new Player();
    List<Map> mapList = new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if(getIntent().hasExtra("Player")){
            player = getIntent().getParcelableExtra("Player");
            mapList = getIntent().getParcelableArrayListExtra("mapList");
            launchingFirstTime();
        }else{
            //Means we opened GameActivity and haven't got any Player Data!
            Intent returnIntent = new Intent();
            //returnIntent.getExtra("Player",player);
            setResult(Activity.RESULT_CANCELED,returnIntent);
            finish();
        }
    }
    private void launchingFirstTime(){
        if(isLaunchingFirstTime){
            /*
            //String mapData = getMaps();
            String mapData = "EEEE;" +
                    "EFFE;" +
                    "EFFE;" +
                    "EEEE";
            String playerData = "P,3,50,50,50;S,4,1.5,1.5";
            String objectData = "P,1,1;C,3,3,1,1,2";
            //Unity with MapData, playerData,ObjectData
            Intent intent = new Intent(this, UnityPlayerActivity.class);
            //PlayerStats String: P,Level,Exp,Kills,Coins;Sword;Axe;Katana;Baton;Big Hammer
            //where weapons has Damage,Defense,hitRange,attackCooldown

            String playerData = getPlayerData();
            String mapData = getMapData();
            String objectData = getObjectData();
            intent.putExtra("playerData", playerData");
            intent.putExtra("objectData", objectData);
            intent.putExtra("mapData", mapData);
            startActivityForResult(intent,0);
            isLaunchingFirstTime =false;*/
        }
    }
    private String getMapData(){
        String type1Map = null;
        for(int i=0;i<mapList.size();i++){
            type1Map = mapList.get(0).getType1Map();
            if(mapList.size()>1){
                type1Map = type1Map + "/";
            }
        }
        if(type1Map!=null){
            if(type1Map.charAt(type1Map.length()-1) == '/'){
                type1Map = type1Map.substring(0,type1Map.length()-1);
            }
        }
        return type1Map;
    }
    private String getObjectData(){
        String type2Objects = null;
        for(int i=0;i<mapList.size();i++){
            type2Objects = mapList.get(0).getType2Objects();
            if(mapList.size()>1){
                type2Objects = type2Objects + "/";
            }
        }
        if(type2Objects!=null){
            if(type2Objects.charAt(type2Objects.length()-1) == '/'){
                type2Objects = type2Objects.substring(0,type2Objects.length()-1);
            }
        }
        return type2Objects;
    }
    private String getPlayerStats(){
        int baseExp = 50;
        int level = player.getExperience() /baseExp;
        int exp = player.getExperience()%baseExp;
        return "P,"+level+","+exp+","+player.getKills()+","+player.getCredits();
    }
    private String getPlayerData(){
        String playerStatsStr = getPlayerStats();
        String itemStr = "";
        StringBuilder items;
        Item itemTmp;
        items = new StringBuilder(playerStatsStr);
        if(player.getListItems() !=null){
            for(int i=0;i<player.getListItems().size(); i++){
                itemTmp = player.getListItems().get(i);
                //where weapons has Damage,Defense,hitRange,attackCooldown
                itemStr = getItemChar(itemTmp.getName()) + ","+itemTmp.getOffense()+ ","+itemTmp.getDefense();
                itemStr = itemStr + itemTmp.getHitRange()+","+itemTmp.getAttackCooldown();
                items.append(";").append(itemStr);
            }
        }
        return items.toString();
    }
    private Character getItemChar(String ItemName){
        Character charItem ='B';
        switch(ItemName) {
            case "Knight Sword":
                // code block
                charItem = 'S';
                break;
            case "Axe":
                charItem = 'A';
                break;
            case "Katana":
                charItem = 'K';
                break;
            case "Big Hammer":
                charItem = 'H';
                break;
        }
        return charItem;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If login activity closed means the user has logged in, and the data is stored in the database
        if (requestCode == 0) {
            if(resultCode == Activity.RESULT_OK){
                //Get the Unity Activity Data!
                String playerStats = data.getStringExtra("playerStats");
                Log.w("Close Game","Received Stats from Unity: "+playerStats);
                //TODO Convert the Incoming string to Proper Player Stats!
                String[] strArr = playerStats.split(",");
                //P,level,Experience,Kills,Credits
                int level = Integer.parseInt(strArr[1]);
                int exp = Integer.parseInt(strArr[2]);
                int kills=Integer.parseInt(strArr[3]);
                int coins=Integer.parseInt(strArr[4]);
                //Updating Player Object with New Stats
                player.setGamesPlayed(player.getGamesPlayed()+1);
                player.setExperience(level*50+exp);
                player.setKills(kills);
                player.setCredits(coins);
                //Close the GameActivity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Player",player);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }
}
