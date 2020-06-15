package edu.upc.eetac.dsa.lastsurvivorfrontend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.unity3d.player.UnityPlayerActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Item;
import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Map;
import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.MapService;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.PlayerService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameActivity extends AppCompatActivity {

    Player player = new Player();
    List<Map> mapList = new LinkedList<>();
    private static Retrofit retrofit;
    private static String retrofitIpAddress;
    //Player Service Object
    PlayerService playerService;
    //Map Service
    MapService mapService;
    private ProgressBar pb_circular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        pb_circular = findViewById(R.id.progressBar_cyclic);
        pb_circular.setVisibility(View.VISIBLE);
        //
        ResourceFileReader rs =  new ResourceFileReader();
        retrofitIpAddress = ResourceFileReader.ReadResourceFileFromStringNameKey("retrofit.IpAddress",this);
        startRetrofit();
        mapService = retrofit.create(MapService.class);
        playerService = retrofit.create(PlayerService.class);
        if(getIntent().hasExtra("Player")){
            player = getIntent().getParcelableExtra("Player");
            getMaps();
            //Now ArrayList Functional Hahaha!, but changed to retrofit callback
            //ArrayList<Map> list = getIntent().getParcelableArrayListExtra("mapList");
            //mapList = list;
        }
    }
    private static void startRetrofit(){
        //HTTP &
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //Attaching Interceptor to a client
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();

        // Running Retrofit to get result from Local tracks service Interface
        //Remember when using Local host on windows the IP is 10.0.2.2 for Android
        //Also added NullOnEmptyConverterFactory when the response from server is empty
        retrofit = new Retrofit.Builder()
                .baseUrl("http://"+retrofitIpAddress+":8080/Backend/")
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
    private void getMaps(){
        try {

            Call<List<Map>> mapsCaller = mapService.getMaps();
            /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
            mapsCaller.enqueue(new Callback<List<Map>>() {
                @Override
                public void onResponse(Call<List<Map>> call, Response<List<Map>> response) {

                    //pb_circular.setVisibility(View.GONE);
                    //Retrieve the result containing in the body
                    if (!response.body().isEmpty()) {
                        // non empty response, Mapping Json via Gson...
                        Log.d("RankingActivity","Server Response Ok");
                        mapList = response.body();

                        launchingFirstTime();
                    } else {
                        // empty response...
                        Log.d("Map List","Maps Request Failed!");
                    }
                }

                @Override
                public void onFailure(Call<List<Map>> call, Throwable t) {
                    NotifyUser("Error,could not retrieve data!");
                }
            });
        }
        catch(Exception e){
            Log.d("MainActivity","Exception: " + e.toString());
        }
    }
    //User Notifier Handler using Toast
    private void NotifyUser(String showMessage){
        Toast toast = Toast.makeText(GameActivity.this,showMessage,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void launchingFirstTime(){
        //String mapData = From Map List get the map and concatenate!;
        String playerData = getPlayerData();
        String mapData = getMapData();
        String objectData = getObjectData();
        //Unity with MapData, playerData,ObjectData

        Intent intent = new Intent(this, UnityPlayerActivity.class);
        intent.putExtra("playerData", playerData);
        intent.putExtra("objectData", objectData);
        intent.putExtra("mapData", mapData);
        pb_circular.setVisibility(View.GONE);
        startActivityForResult(intent,0);
    }
    private String getMapData(){
        StringBuilder type1Map = new StringBuilder();
        if(mapList!=null){
            for(int i=0;i<mapList.size();i++){
                type1Map.append(mapList.get(i).getType1Map()).append("/");
            }
            if(type1Map.length()!=0){
                if(type1Map.lastIndexOf("/")==(type1Map.length()-1)){
                    type1Map = new StringBuilder(type1Map.substring(0, type1Map.length() - 2));
                }
            }
        }
        return type1Map.toString();
    }
    private String getObjectData(){
        StringBuilder type2Objects = new StringBuilder();
        if(mapList!=null) {
            for (int i = 0; i < mapList.size(); i++) {
                type2Objects.append(mapList.get(i).getType2Objects()).append("/");
            }
            if (type2Objects.length() != 0) {
                if (type2Objects.lastIndexOf("/") == (type2Objects.length() - 1)) {
                    type2Objects = new StringBuilder(type2Objects.substring(0, type2Objects.length() - 2));
                }
            }
        }
        return type2Objects.toString();
    }
    private String getPlayerStats(){
        //PlayerStats String: P,Level,Exp,Kills,Coins;Sword;Axe;Katana;Baton;Big Hammer
        int baseExp = 50;
        int level = player.getExperience() /baseExp;
        int exp = player.getExperience()%baseExp;
        return "P,"+level+","+exp+","+player.getKills()+","+player.getCredits();
    }
    private String getPlayerData(){
        //PlayerStats String: P,Level,Exp,Kills,Coins;Sword;Axe;Katana;Baton;Big Hammer
        //where weapons has Damage,Defense,hitRange,attackCooldown
        String playerStatsStr = getPlayerStats();
        String itemStr = "";
        StringBuilder items;
        Item itemTmp;
        items = new StringBuilder(playerStatsStr);
        if(player.getListItems() !=null){
            for(int i=0;i<player.getListItems().size(); i++){
                itemTmp = player.getListItems().get(i);
                //where weapons has Damage,Defense,hitRange,attackCooldown
                itemStr = getItemChar(itemTmp.getName()) + ","+itemTmp.getOffense()+ ","+itemTmp.getDefense()+ ",";
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
            if(resultCode == Activity.RESULT_OK) {
                pb_circular.setVisibility(View.VISIBLE);
                //Get the Unity Activity Data!
                ArrayList<String> playerStatsArr = new ArrayList<>();
                if(data != null && data.hasExtra("playerStatsArr")) {
                    playerStatsArr = data.getStringArrayListExtra("playerStatsArr");
                    String playerStats = playerStatsArr.get(playerStatsArr.size()-1);
                        Log.w("Close Game", "Received Stats from Unity: " + playerStats);
                        //Convert the Incoming string to Proper Player Stats!
                        String[] strArr = playerStats.split(",");
                        //P,level,Experience,Kills,Credits
                        int level = Integer.parseInt(strArr[1]);
                        int exp = Integer.parseInt(strArr[2]);
                        int kills = Integer.parseInt(strArr[3]);
                        int coins = Integer.parseInt(strArr[4]);
                        //Updating Player Object with New Stats
                        player.setGamesPlayed(player.getGamesPlayed() + 1);
                        player.setExperience(level * 50 + exp);
                        player.setKills(kills);
                        player.setCredits(coins);
                        updatePlayer();

                }
                //Close the GameActivity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Player",player);
                setResult(Activity.RESULT_OK,returnIntent);
                pb_circular.setVisibility(View.GONE);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        }
    }
    private void updatePlayer(){
        try {
            if(player!=null) {
                Call<Player> playerTmp = playerService.updatePlayer(player);
                /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
                playerTmp.enqueue(new Callback<Player>() {
                    @Override
                    public void onResponse(Call<Player> call, Response<Player> response) {
                        //Update Successful
                        if (response.code() == 201) {
                            //Successful we can get the ID, and call again to ask for PLayer
                            if (response.isSuccessful()) {
                                player = response.body();
                                if (player != null) {
                                    Log.w("GameActivity", "Update Player Response successful" + player.toString());
                                }
                            } else {
                                Log.e("GameActivity", "Couldn't fill player from body");
                            }
                        } else if (response.code() == 404) { // Not Found User
                            NotifyUser("Player Not Found");
                        } else if (response.code() == 400) { //Incorrect Password
                            NotifyUser("Bad Request");
                        } else {
                            NotifyUser("Something went horribly wrong!");
                        }
                    }
                    @Override
                    public void onFailure(Call<Player> call, Throwable t) {
                        NotifyUser("Failure to Update Profile");
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
