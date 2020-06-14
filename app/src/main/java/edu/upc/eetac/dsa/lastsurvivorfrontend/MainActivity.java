package edu.upc.eetac.dsa.lastsurvivorfrontend;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

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


public class MainActivity extends AppCompatActivity {
    //TOKEN
    private boolean temp_token = false;
    // RETROFIT OBJECT
    private static Retrofit retrofit;
    private static String retrofitIpAddress;
    //Player Service Object
    PlayerService playerService;
    //Map Service
    private boolean playerLogged = false;
    MapService mapService;
    //Player Objects
    Player player = new Player();
    //Maps List
    List<Map> mapList = new LinkedList<>();
    //TextView of Splash
    private ProgressBar pb_circular;
    private static int GameRequestCode = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb_circular = findViewById(R.id.progressBar_cyclic);
        //After launch check if a user exists in preferences and also set the player
        if(!ExistPlayerAndSetData()){
            LaunchLoginActivity();
        }else{
            //Connect with retrofit & Login Automatically and Retrieve the Player Data
            try{
                ResourceFileReader rs =  new ResourceFileReader();
                retrofitIpAddress = ResourceFileReader.ReadResourceFileFromStringNameKey("retrofit.IpAddress",this);
                startRetrofit();
                playerService = retrofit.create(PlayerService.class);
                //Login with Player Object player which was written using (ExistPlayerAndSetData)
                LoginUser();

            }catch(Exception e){
                //Not possible to connect to server
                e.printStackTrace();
                NotifyUser("Unable to Connect to Server, Restart the Application!");
                playerLogged = false;
            }
        }
        pb_circular.setVisibility(View.GONE);
    }
    private boolean ExistPlayerAndSetData(){
        //Access the shared preference UserInfo and obtain the parameters, default =string empty
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        player.setUsername(settings.getString("Username", ""));
        player.setPassword(settings.getString("Password", ""));
        player.setId(settings.getString("Id", ""));
        return !player.getId().equals("");
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //If the focus has been changed,means we are on the activity than hide the bar again behind the activity
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /* Shows the system bars by removing all the flags
        except for the ones that make the content appear under the system bars.*/
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void onStartGameClicked(View view){
        if(playerLogged){
            pb_circular.setVisibility(View.VISIBLE);
            if(player.getId()!=null) {
                //Launch Unity Game and after starting the game also get the data back from the unity to update the server
                //NotifyUser("Game Started: " + player.getUsername());
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("Player", player);
                //ArrayList<Map> maps = new ArrayList<>(mapList);
                //intent.putParcelableArrayListExtra("mapList", maps);
                startActivityForResult(intent, GameRequestCode);
            }
        }else{
            NotifyUser("Unable to Connect to Server, Restart the Application!");
            LaunchLoginActivity();
        }
    }
    public void onButtonEnemiesClick(View view){
        if(playerLogged) {
            pb_circular.setVisibility(View.VISIBLE);
            Intent intent = new Intent(MainActivity.this, EnemyActivity.class);
            startActivity(intent);
            pb_circular.setVisibility(View.GONE);
        }else{
            NotifyUser("Unable to Connect to Server, Restart the Application!");
            LaunchLoginActivity();
        }
    }
    public void onForumClicked(View view){
        if(playerLogged) {
            //Here the press of the Button Forum
            pb_circular.setVisibility(View.VISIBLE);
            Intent intent = new Intent(MainActivity.this, ForumListActivity.class);
            intent.putExtra("Avatar", player.getAvatar());
            startActivityForResult(intent, 4);
            pb_circular.setVisibility(View.GONE);
        }else{
            NotifyUser("Unable to Connect to Server, Restart the Application!");
            LaunchLoginActivity();
        }
    }
    public void onInventoryClicked(View view){
        if(playerLogged) {
            pb_circular.setVisibility(View.VISIBLE);
            Intent intent = new Intent(MainActivity.this, StoreActivity.class);
            intent.putExtra("parentId", player.getId());
            startActivity(intent);
            pb_circular.setVisibility(View.GONE);
        }else{
            NotifyUser("Unable to Connect to Server, Restart the Application!");
            LaunchLoginActivity();
        }
    }
    public void onButtonRedirectWebClick(View view){
        if(playerLogged) {
            pb_circular.setVisibility(View.VISIBLE);
            //Launch Unity Game and after starting the game also get the data back from the unity to update the server
            NotifyUser("Web Redirect: " + player.getUsername());
            String url = "http://" + retrofitIpAddress + ":8080/LastSurvivor/Register/main.html";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            pb_circular.setVisibility(View.GONE);
        }else{
            NotifyUser("Unable to Connect to Server, Restart the Application!");
            LaunchLoginActivity();
        }
    }
    public void onButtonModifyProfileClick(View view){
        if(playerLogged) {
            pb_circular.setVisibility(View.VISIBLE);
            //Launch Unity Game and after starting the game also get the data back from the unity to update the server
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("Player", player);
            startActivityForResult(intent, 3);
            pb_circular.setVisibility(View.GONE);
        }else{
            NotifyUser("Unable to Connect to Server, Restart the Application!");
            LaunchLoginActivity();
        }
    }
    public void onSignOutClicked(View view){
        if(playerLogged) {
            pb_circular.setVisibility(View.VISIBLE);
            //Launch Unity Game and after starting the game also get the data back from the unity to update the server
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Username", "");
            editor.putString("Password", "");
            editor.putString("Id", "");
            editor.commit();
            //After logout from phone, set the screen to LoginActivity
            LaunchLoginActivity();
            pb_circular.setVisibility(View.GONE);
        }else{
            NotifyUser("Unable to Connect to Server, Restart the Application!");
            LaunchLoginActivity();
        }
    }
    public void onCurrentRankingClicked(View view){
        if(playerLogged ){
        pb_circular.setVisibility(View.VISIBLE);
        Intent intent = new Intent(MainActivity.this ,RankingActivity.class);
        startActivityForResult(intent,2);
        pb_circular.setVisibility(View.GONE);
        }else{
            NotifyUser("Unable to Connect to Server, Restart the Application!");
            LaunchLoginActivity();
        }
    }
    private void LaunchLoginActivity() {
        pb_circular.setVisibility(View.VISIBLE);
        Intent intent = new Intent(MainActivity.this ,LoginActivity.class);
        startActivityForResult(intent,1);
        pb_circular.setVisibility(View.GONE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If login activity closed means the user has logged in, and the data is stored in the database
        pb_circular.setVisibility(View.VISIBLE);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                if (data != null) {
                    player = data.getParcelableExtra("Player");
                    playerLogged = true;
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                playerLogged = false;
                finish();
            }

        }
        //Profile Modified request code 3
        if (requestCode == 3) {
            if(resultCode == Activity.RESULT_OK){
                if (data != null) {
                    player = data.getParcelableExtra("Player");
                    playerLogged = true;
                }
                //Retrieved updated data from Profile Modified
                if (player != null) {
                    Log.w("Main Modify",player.getAvatar());
                    playerLogged = true;
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
               //Do nothing as nothing updated
                playerLogged = true;
            }
        }
        //Game Activity Request Code 4
        if(requestCode == GameRequestCode){
            if(resultCode == Activity.RESULT_OK){
                if (data != null) {
                    player = data.getParcelableExtra("Player");
                    playerLogged = true;
                }
                //Retrieved updated data from Game Activity
                updatePlayer(); //Update Player in Server
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Do nothing as nothing changed in game!
                playerLogged = true;
            }
        }
        pb_circular.setVisibility(View.GONE);
    }
    private void updatePlayer(){
        pb_circular.setVisibility(View.VISIBLE);
        try {
            Call<Player> playerTmp = playerService.updatePlayer(player);
            /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
            playerTmp.enqueue(new Callback<Player>() {
                @Override
                public void onResponse(Call<Player> call, Response<Player> response) {
                    //Update Successful
                    pb_circular.setVisibility(View.GONE);
                    if (response.code() == 201) {
                        //Successful we can get the ID, and call again to ask for PLayer
                        if(response.isSuccessful()){
                            player =  response.body();
                            if (player != null) {
                                Log.w("Update Player" ,"Update Player Response successful"+ player.toString());
                                playerLogged = true;
                            }else{
                                playerLogged = false;LaunchLoginActivity();
                            }
                        }else{ Log.e("MainActivity","Couldn't fill player from body"); playerLogged = false;}
                    } else if(response.code() == 404){ // Not Found User
                        NotifyUser("Player Not Found");playerLogged = false;
                    }else if(response.code() == 400){ //Incorrect Password
                        NotifyUser("Bad Request");playerLogged = false;
                    }else{
                        NotifyUser("Something went horribly wrong!");playerLogged = false;
                    }
                }
                @Override
                public void onFailure(Call<Player> call, Throwable t) {
                    NotifyUser("Failure to Update Profile");playerLogged = false;LaunchLoginActivity();
                }
            });
        }catch (Exception e){
            e.printStackTrace();playerLogged = false;LaunchLoginActivity();
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

    private void LoginUser(){
        String username,password;
        username =  player.getUsername();
        password =  player.getPassword();
        pb_circular.setVisibility(View.VISIBLE);
        if(username == null|| password== null){
            NotifyUser("Please fill the fields!");
        }else if(username.isEmpty()|| password.isEmpty()){
            NotifyUser("Please Type something other than space");
        }else if(username.contains(" ")|| password.contains(" ")){
            NotifyUser("Please Type that doesn't contain spaces");
        }else{
            //Now we can send the data to Server and ask for login
            String TAG = "onSignIn";

            try {
                player = new Player(username,password,0,0,0,0);
                player.setUsername(username);player.setPassword(password);
                Call<Player> playerID = playerService.signIn(player);
                Log.d(TAG, "Player Logging In from Splash: "+playerID.toString());
                /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
                playerID.enqueue(new Callback<Player>() {
                    @Override
                    public void onResponse(Call<Player> call, Response<Player> response) {
                        Log.d(TAG, "Player Logging In from splash response Server: "+call.toString());
                        pb_circular.setVisibility(View.GONE);
                        //SingIn Successful
                        if (response.code() == 201) {
                            //Successful we can get the ID, and call again to ask for PLayer
                            if(response.isSuccessful()){
                                player =  response.body();

                                if (player != null) {
                                    Log.d(TAG,"The Player ID is: "+player.getId());
                                    playerLogged = true;
                                }else{
                                    playerLogged = false;LaunchLoginActivity();
                                }
                            }else{ Log.d(TAG,"Something went horribly wrong!");LaunchLoginActivity();}
                        } else if(response.code() == 404){ // Not Found User
                            NotifyUser("Unsuccessful!");playerLogged = false;LaunchLoginActivity();
                        }else if(response.code() == 401){ //Incorrect Password
                            NotifyUser("Incorrect Password");playerLogged = false;LaunchLoginActivity();
                        }else{
                            NotifyUser("Something went horribly wrong!");playerLogged = false;LaunchLoginActivity();
                        }
                    }
                    @Override
                    public void onFailure(Call<Player> call, Throwable t) {
                        pb_circular.setVisibility(View.GONE);
                        NotifyUser("Failure to logIn");playerLogged = false;LaunchLoginActivity();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //User Notifier Handler using Toast
    private void NotifyUser(String showMessage){
        Toast toast = Toast.makeText(MainActivity.this,showMessage,Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    public void onBackPressed() {
        // do something on back.
        exitDialog();
    }
    private void exitDialog(){
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        LinearLayout cancel= dialog.findViewById(R.id.cancelbtn);
        LinearLayout exit=dialog.findViewById(R.id.button_back);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });
        dialog.show();

    }
}
