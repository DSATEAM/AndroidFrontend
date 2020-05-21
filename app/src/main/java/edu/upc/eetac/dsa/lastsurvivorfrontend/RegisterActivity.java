package edu.upc.eetac.dsa.lastsurvivorfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {
    private static Retrofit retrofit;
    Player player;

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
        setContentView(R.layout.activity_register);
        Button registerBtn = findViewById(R.id.RegisterBtn);
        Button goBack = findViewById(R.id.goBackBtn);

        startRetrofit();
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

    public void onGoBackClicked(View view) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }
    public void onRegisterClick(View view){
        EditText username = findViewById(R.id.input_username2);
        EditText password = findViewById(R.id.input_password2);
        if (username.getText().toString().equals(null)||password.getText().toString().equals(null)||password.getText().toString().equals("Password")||username.getText().toString().equals("Username")){
            Toast.makeText(getApplicationContext(), "Error.Campos vacíos.", Toast.LENGTH_LONG).show();
        }

        else{
            PlayerService service = retrofit.create(PlayerService.class);
            player = new Player();
            player.setUsername(username.getText().toString());
            player.setPassword(password.getText().toString());
            service.signUp(player).enqueue(new Callback<Player>() {
                @Override
                public void onResponse(Call<Player> call, Response<Player> response) {
                    if (response.code() == 201) {
                        Toast.makeText(getApplicationContext(), "Signed Up successfully", Toast.LENGTH_LONG).show();
                        player = response.body();
                        //Close Register and return result to login which will also close the activity for splash
                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("Username",player.getUsername());
                        editor.putString("Password",player.getPassword());
                        editor.putString("Id",player.getId());
                        editor.commit();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("Player",player);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                    else if (response.code() == 404){
                        Toast.makeText(getApplicationContext(),"Couldn't Register...",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                        Toast.makeText(getApplicationContext(),"Failed...",Toast.LENGTH_LONG).show();
                }
            });

        }



    }
}
