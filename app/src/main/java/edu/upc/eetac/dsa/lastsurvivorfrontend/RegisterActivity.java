package edu.upc.eetac.dsa.lastsurvivorfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button registerBtn = findViewById(R.id.RegisterBtn);
        Button goBack = findViewById(R.id.goBackBtn);
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

    public void onGoBackClicked(View view) {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
    }
    public void onRegisterClick(View view){
        EditText username = findViewById(R.id.input_username2);
        EditText password = findViewById(R.id.input_password2);
        if (username.getText().toString().equals(null)||password.getText().toString().equals(null)||password.getText().toString().equals("Password")||username.getText().toString().equals("Username")){
            Toast.makeText(getApplicationContext(), "Error.Campos vacíos.", Toast.LENGTH_LONG).show();
        }

        else{
            startRetrofit();
            PlayerService service = retrofit.create(PlayerService.class);
            Player player = new Player();
            player.setUsername(username.getText().toString());
            player.setPassword(password.getText().toString());
            service.signUp(username.getText().toString(),password.getText().toString()).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.code() == 201) {
                        Toast.makeText(getApplicationContext(), "Signed Up successfully", Toast.LENGTH_LONG).show();

                    }
                    else if (response.code() == 404){
                        Toast.makeText(getApplicationContext(),"Something happened...",Toast.LENGTH_LONG).show();
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
