package edu.upc.eetac.dsa.lastsurvivorfrontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.content.Intent;
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
    // RETROFIT OBJECT
    private static Retrofit retrofit;

    //Player Service Object
    PlayerService playerService;
    //TextViews
    public TextView usernameTextview ;
    public TextView passwordTextview ;
    //Player Objects
    Player player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start Login
        setContentView(R.layout.activity_main);
        //Starting Retrofit
        startRetrofit();
        playerService = retrofit.create(PlayerService.class);
        //Get the TextViews
        usernameTextview = this.findViewById(R.id.username);
        passwordTextview  = this.findViewById(R.id.password);
        //TODO Get the Current Data Saved as Token in Local Device

        //TODO If the above not true, than wait for user input

        //TODO After user input, send the data to server and ask for authentication

        //TODO Else if user chose register than open register and transfer the connection

    }

    private void LaunchRegisterActivity() {
        Intent intent = new Intent(MainActivity.this ,RegisterActivity.class);
        /*
        intent.putExtra("DATA_1", "TestString");
        intent.putExtra("DATA_2", true);
        intent.putExtra("DATA_3", 6969);
        intent.putExtra("DATA_4",0.6969);
        */
        startActivityForResult(intent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode == RESULT_OK){
                //Means the activity REGISTER was closed successfully and thus the Player was
                // registered correctly!
                String tmp_ID = data.getStringExtra("RETRIEVE_Player_ID");
                String tmp_pass = data.getStringExtra("RETRIEVE_PLAYER_PASSWORD");
                String tmp_username = data.getStringExtra("RETRIEVE_PLAYER_USERNAME");
                //TODO GET THE PLAYER STATS FROM THE SERVER

                //TODO GET THE ITEMS FROM THE SERVER FOR THE PLAYER

                //TODO GET THE MATERIALS FROM THE SERVER FOR THE PLAYER
            }
            //Means user didn't register and so don't do anything and wait...
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
        Toast toast = Toast.makeText(MainActivity.this,showMessage,Toast.LENGTH_SHORT);
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
                player = new Player(username,password,0,0,0,0,0);
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
