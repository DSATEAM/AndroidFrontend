package edu.upc.eetac.dsa.lastsurvivorfrontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity {
    // RETROFIT OBJECT
    private static Retrofit retrofit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start Login
        setContentView(R.layout.activity_main);
        //Starting Retrofit
        startRetrofit();
        //TODO Get the Current Data Saved as Token in Local Device

        //TODO If the above not true, than wait for user input

        //TODO After user input, send the data to server and ask for authentication

        //TODO Else if user chose register than open register and transfer the connection

    }

    private void LaunchRegisterActivity() {
        Intent intent = new Intent(MainActivity.this ,RegisterActivity.class);
        intent.putExtra("DATA_1", "TestString");
        intent.putExtra("DATA_2", true);
        intent.putExtra("DATA_3", 6969);
        intent.putExtra("DATA_4",0.6969);
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
                .baseUrl("http://10.0.2.2:8080/LastSurvivorBackend/")
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
    //User Notifier Handler using Toast
    private void NotifyUser(String MSG){
        Toast toast = Toast.makeText(MainActivity.this,MSG,Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onSignUpClicked(View view) {
        LaunchRegisterActivity();
    }
}
