package edu.upc.eetac.dsa.lastsurvivorfrontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.PlayerService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        if(!ExistPlayer()){
            LaunchLoginActivity();
        }else{
            //Show Username in the SplashScreen
            textView2.setText("Welcome Back "+player.getUsername());
            //Connect with retrofit
            try{
                startRetrofit();
                //Succesfully created Retrofit
            }catch(Exception e){
                //Not possible to connect to server
                e.printStackTrace();
                NotifyUser("Can't Connect to Server!");
            }
        }
    }
    private boolean ExistPlayer(){

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
        /*
        intent.putExtra("DATA_1", "TestString");
        intent.putExtra("DATA_2", true);
        intent.putExtra("DATA_3", 6969);
        intent.putExtra("DATA_4",0.6969);
        */
        startActivityForResult(intent,1);
    }
    private void LaunchLoginActivity() {
        Intent intent = new Intent(MainActivity.this ,LoginActivity.class);
        startActivityForResult(intent,1);
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
    //User Notifier Handler using Toast
    private void NotifyUser(String showMessage){
        Toast toast = Toast.makeText(MainActivity.this,showMessage,Toast.LENGTH_SHORT);
        toast.show();
    }
}
