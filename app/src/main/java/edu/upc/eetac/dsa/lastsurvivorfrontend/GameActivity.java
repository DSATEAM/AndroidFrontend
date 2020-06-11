package edu.upc.eetac.dsa.lastsurvivorfrontend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Item;
import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Map;
import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.MapService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameActivity extends AppCompatActivity {
    boolean isLaunchingFirstTime = true;
    Player player = new Player();
    List<Map> mapList = new LinkedList<>();
    private static Retrofit retrofit;
    private static String retrofitIpAddress;
    //Map Service
    MapService mapService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //
        ResourceFileReader rs =  new ResourceFileReader();
        retrofitIpAddress = ResourceFileReader.ReadResourceFileFromStringNameKey("retrofit.IpAddress",this);
        startRetrofit();
        mapService = retrofit.create(MapService.class);
        if(getIntent().hasExtra("Player")){
            player = getIntent().getParcelableExtra("Player");
            getMaps();
            //Now ArrayList Functional Hahaha!, but changed to retrofit callback
            //ArrayList<Map> list = getIntent().getParcelableArrayListExtra("mapList");
            //mapList = list;
        }else{
            //Means we opened GameActivity and haven't got any Player Data!
            Intent returnIntent = new Intent();
            //returnIntent.getExtra("Player",player);
            setResult(Activity.RESULT_CANCELED,returnIntent);
            finish();
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
        if(isLaunchingFirstTime){
            isLaunchingFirstTime =false;
            //String mapData = From Map List get the map and concatenate!;
            String playerData = getPlayerData();
            String mapData = getMapData();
            String objectData = getObjectData();
            //Unity with MapData, playerData,ObjectData
            /*
            Intent intent = new Intent(this, UnityPlayerActivity.class);
            intent.putExtra("playerData", playerData");
            intent.putExtra("objectData", objectData);
            intent.putExtra("mapData", mapData);
            startActivityForResult(intent,0);
            */
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
        //Objects on Map with location and other values
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
