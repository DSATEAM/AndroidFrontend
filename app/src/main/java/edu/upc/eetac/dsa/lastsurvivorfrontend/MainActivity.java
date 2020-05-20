package edu.upc.eetac.dsa.lastsurvivorfrontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.PlayerService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity {
    //TOKEN
    private boolean temp_token = false;
    // RETROFIT OBJECT
    private static Retrofit retrofit;
    //Player Service Object
    PlayerService playerService;
    //Player Objects
    Player player = new Player();
    //Text Splash
    public TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView2  = this.findViewById(R.id.textView2);
        //After launch check if token
        if(!ExistPlayerAndSetData()){
            LaunchLoginActivity();
        }else{
            //Show Username in the SplashScreen
            textView2.setText("Welcome Back "+player.getUsername());
            //Connect with retrofit
            try{
                startRetrofit();
                playerService = retrofit.create(PlayerService.class);
                LoginUser();
                //Succesfully created Retrofit
            }catch(Exception e){
                //Not possible to connect to server
                e.printStackTrace();
                NotifyUser("Can't Connect to Server!");
            }
        }
    }
    private boolean ExistPlayerAndSetData(){

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        player.setUsername(settings.getString("Username", ""));
        player.setPassword(settings.getString("Password", ""));
        player.setId(settings.getString("Id", ""));
        return !player.getId().equals("");
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
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

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void onStartGameClicked(View view){
        //Launch Unity Game and after starting the game also get the data back from the unity to update the server
        NotifyUser("Game Started: " + player.getUsername());
    }
    public void onSignOutClicked(View view){
        //Launch Unity Game and after starting the game also get the data back from the unity to update the server
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Username","");
        editor.putString("Password","");
        editor.putString("Id","");
        editor.commit();
        LaunchLoginActivity();
    }
    public void onCurrentRankingClicked(View view){
        Intent intent = new Intent(MainActivity.this ,RankingActivity.class);
        startActivityForResult(intent,1);
    }
    private void LaunchLoginActivity() {
        Intent intent = new Intent(MainActivity.this ,LoginActivity.class);
        startActivityForResult(intent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                ExistPlayerAndSetData();
                textView2.setText("Welcome Back "+player.getUsername());
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Do nothing as user not registered properly
                ExistPlayerAndSetData();
                textView2.setText("Welcome Back "+player.getUsername());
            }
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
                .baseUrl("http://10.0.2.2:8080/Backend/")
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
    private void LoginUser(){
        String username,password;
        username =  player.getUsername();
        password =  player.getPassword();
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
                Gson gson = new Gson();
                String jsonInString = gson.toJson(player);
                Log.d(TAG, "PlayerGson: "+jsonInString);
                Log.d(TAG, "Player Logging In from Splash: "+playerID.toString());
                /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
                playerID.enqueue(new Callback<Player>() {
                    @Override
                    public void onResponse(Call<Player> call, Response<Player> response) {
                        Log.d(TAG, "Player Logging In from splash response Server: "+call.toString());
                        //SingIn Successful
                        if (response.code() == 201) {
                            //Successful we can get the ID, and call again to ask for PLayer
                            if(response.isSuccessful()){
                                player =  response.body();
                                Log.d(TAG,"The Player ID is: "+player.getId());
                            }else{ Log.d(TAG,"Something went horribly wrong!");}
                        } else if(response.code() == 404){ // Not Found User
                            NotifyUser("Unsuccessful!");
                        }else if(response.code() == 401){ //Incorrect Password
                            NotifyUser("Incorrect Password");
                        }else{
                            NotifyUser("Something went horribly wrong!");
                        }
                    }
                    @Override
                    public void onFailure(Call<Player> call, Throwable t) {
                        NotifyUser("Error");
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
}
