package edu.upc.eetac.dsa.lastsurvivorfrontend;

import android.annotation.SuppressLint;
import androidx.annotation.Nullable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.gson.Gson;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {
    // RETROFIT OBJECT
    private static Retrofit retrofit;
    //Player Service Object
    PlayerService playerService;
    //TextViews
    public TextView usernameTextview ;
    public TextView passwordTextview ;
    //Player Objects
    Player player = new Player();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_login_activity);
        //Starting Retrofit
        startRetrofit();
        playerService = retrofit.create(PlayerService.class);
        //Get the TextViews
        usernameTextview = this.findViewById(R.id.username);
        passwordTextview  = this.findViewById(R.id.password);
    }
    private void LaunchRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this ,RegisterActivity.class);
        /*
        intent.putExtra("DATA_1", "TestString");
        intent.putExtra("DATA_2", true);
        intent.putExtra("DATA_3", 6969);
        intent.putExtra("DATA_4",0.6969);
        */
        startActivityForResult(intent,1);
        //After Launching check if userExists in SharedPreference if Yes than close this LoginActivity
        if(ExistPlayer()){
            finish();
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
    public void onBackPressed(){
        hideSystemUI();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Exit",false);
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                //Means we registered succesfully
                player = data.getParcelableExtra("Player");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Player",player);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Do nothing as user cancelled operation
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

    //User Notifier Handler using Toast
    private void NotifyUser(String showMessage){
        Toast toast = Toast.makeText(LoginActivity.this,showMessage,Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onSignUpClicked(View view) {
        LaunchRegisterActivity();
    }

    public void onSignInClicked(View view) {
        //Login Player and Retrieve the ID
        String username,password;
        username =  this.usernameTextview.getText().toString();
        password =  this.passwordTextview.getText().toString();
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
                Log.d(TAG, "onSignInClicked: "+playerID.toString());
                /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
                playerID.enqueue(new Callback<Player>() {
                    @Override
                    public void onResponse(Call<Player> call, Response<Player> response) {
                        Log.d(TAG, "onSignInClicked: "+call.toString());
                        //SingIn Successful
                        if (response.code() == 201) {
                            NotifyUser("Successful");
                            //Successful we can get the ID, and call again to ask for PLayer
                            if(response.isSuccessful()){
                                player =  response.body();
                                NotifyUser("The Player ID is: "+player.getId());
                                //We can close the Login Activity and get back to Splash Screen
                                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("Username",player.getUsername());
                                editor.putString("Password",player.getPassword());
                                editor.putString("Id",player.getId());
                                editor.commit();
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }else{ NotifyUser("Something went horribly wrong!");}
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
                Log.d(TAG, "onSignInClicked: "+e.toString());
            }
        }
    }

}
