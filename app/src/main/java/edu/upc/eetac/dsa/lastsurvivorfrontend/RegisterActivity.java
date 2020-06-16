package edu.upc.eetac.dsa.lastsurvivorfrontend;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private Context mContext;
    private static String retrofitIpAddress;
    private ProgressBar pb_circular;
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
        pb_circular = findViewById(R.id.progressBar_cyclic);
        mContext = this.getApplicationContext();
        retrofitIpAddress = ResourceFileReader.ReadResourceFileFromStringNameKey("retrofit.IpAddress",this);
        startRetrofit();
        pb_circular.setVisibility(View.GONE);
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


   public void onGoBackClicked(View view) {
       exitDialog();
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
               Intent returnIntent = new Intent();
               setResult(Activity.RESULT_CANCELED,returnIntent);
               finish();
           }
       });
       dialog.show();

   }
    public void onRegisterClick(View view){
        EditText username = findViewById(R.id.input_username2);
        EditText password = findViewById(R.id.input_password2);
        EditText repassword = findViewById(R.id.input_repassword2);
        pb_circular.setVisibility(View.VISIBLE);
        //username.getText().toString();
        //password.getText().toString();
        if (password.getText().toString().equals("Password") || username.getText().toString().equals("Username")||
            password.getText().toString().equals("") || username.getText().toString().equals("")||
                password.getText().toString().isEmpty() || username.getText().toString().isEmpty()||
                password.getText().toString().contains(" ") || username.getText().toString().contains(" ")){
            Toast.makeText(getApplicationContext(), "Fill Fields Correctly!.", Toast.LENGTH_LONG).show();
            pb_circular.setVisibility(View.GONE);
        }else if(!password.getText().toString().equals(repassword.getText().toString())){
            Toast.makeText(getApplicationContext(), "Passwords must be same...", Toast.LENGTH_LONG).show();
            pb_circular.setVisibility(View.GONE);
        }
        else{
            PlayerService service = retrofit.create(PlayerService.class);
            player = new Player();
            player.setUsername(username.getText().toString());
            player.setPassword(password.getText().toString());
           // player.setAvatar("basicAvatar");
            service.signUp(player).enqueue(new Callback<Player>() {
                @Override
                public void onResponse(Call<Player> call, Response<Player> response) {
                    pb_circular.setVisibility(View.GONE);
                    if (response.code() == 201) {
                        Toast.makeText(getApplicationContext(), "Signed Up successfully", Toast.LENGTH_LONG).show();
                        player = response.body();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("Player",player);
                        setResult(Activity.RESULT_OK,returnIntent);
                        //Close Register and return result to login which will also close the activity for splash
                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("Username",player.getUsername());
                        editor.putString("Password",player.getPassword());
                        editor.putString("Id",player.getId());
                        editor.commit();
                        finish();
                    }
                    else if (response.code() == 404){
                        Toast.makeText(getApplicationContext(),"Please Fill Correctly!",Toast.LENGTH_LONG).show();
                    }else if(response.code()==409){
                        Toast.makeText(getApplicationContext(),"Username already Taken!",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                        Toast.makeText(getApplicationContext(),"Unable to Connect...",Toast.LENGTH_LONG).show();
                }
            });

        }
    }
}
