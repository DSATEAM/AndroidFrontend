package edu.upc.eetac.dsa.lastsurvivorfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.SignUpService;
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
        Button registerBtn = (Button) findViewById(R.id.RegisterBtn);
        Button goBack = (Button) findViewById(R.id.goBackBtn);
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
        EditText username = (EditText)findViewById(R.id.input_username2);
        EditText password = (EditText)findViewById(R.id.input_password2);
        if (username.getText().toString().equals(null)||password.getText().toString().equals(null)||password.getText().toString().equals("Password")||username.getText().toString().equals("Username")){
            Toast.makeText(getApplicationContext(), "Error.Campos vacíos.", Toast.LENGTH_LONG).show();
        }
        else{
            startRetrofit();
            SignUpService service = retrofit.create(SignUpService.class);
            Player player = new Player(username.getText().toString(),password.getText().toString());
            service.addPlayer(player).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if(response.isSuccessful())
                        Toast.makeText(getApplicationContext(),"Signed Up successfully",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                        Toast.makeText(getApplicationContext(),"Something happened...",Toast.LENGTH_LONG).show();
                }
            });

        }



    }
}
