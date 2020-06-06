package edu.upc.eetac.dsa.lastsurvivorfrontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Forum;
import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Message;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.ForumService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForumActivity extends AppCompatActivity {

    // RETROFIT OBJECT
    private static Retrofit retrofit;
    private static String retrofitIpAddress;

    //TRACKS SERVICE OBJECT
    ForumService forumService;
    //List<Repo> Repo_List;
    List<Message> messageList ;
    Forum forum;

    private RecyclerView recyclerView;
    //As we added new methods inside our custom Adapter, we need to create our own type of adapter
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar pb_circular;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        //Implementing RecyclerView
        recyclerView = findViewById(R.id.my_recycler_view);
        pb_circular = findViewById(R.id.progressBar_cyclic);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        forum = getIntent().getParcelableExtra("Forum");
        //HTTP &
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //Attaching Interceptor to a client
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();

        // Running Retrofit to get result from Local tracks service Interface
        //Remember when using Local host on windows the IP is 10.0.2.2 for Android
        //Also added NullOnEmptyConverterFactory when the response from server is empty
        ResourceFileReader rs =  new ResourceFileReader();
        retrofitIpAddress = ResourceFileReader.ReadResourceFileFromStringNameKey("retrofit.IpAddress",this);
        startRetrofit();
        forumService = retrofit.create(ForumService.class);
        getMessages(forum);
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
    private void NotifyUser(String MSG){
        Toast toast = Toast.makeText(ForumActivity.this,MSG,Toast.LENGTH_SHORT);
        toast.show();
    }
    public void onBackButtonClick(View view){
        exitDialog();
    }
    public void onCommentButtonClick(View view){}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If login activity closed means the user has logged in, and the data is stored in the database
        pb_circular.setVisibility(View.VISIBLE);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //Graph Activity Closed Successfully!

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //User demanded to go back to main menu
                finish();
            }

        }
    }

    private void getMessages(Forum forum){
        //Retrofit Implementation on Button Press
        //Adding Interceptor
        pb_circular.setVisibility(View.VISIBLE);
        try {

            Call<List<Message>> messagesCall = forumService.getMessages(forum);
            /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
            messagesCall.enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {

                    pb_circular.setVisibility(View.GONE);
                    //Retrieve the result containing in the body
                    if (!response.body().isEmpty()) {
                        // non empty response, Mapping Json via Gson...
                        Log.d("ForumActivity","Server Response Ok");
                        messageList = response.body();
                        //If clicked once then new player list else update the recyclerview
                        if(mAdapter == null){
                            buildRecyclerView();
                        }else{
                            mAdapter = null;
                            buildRecyclerView();
                        }

                    } else {
                        // empty response...
                        Log.d("RankingActivity","Request Failed!");
                    }
                }

                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {
                    NotifyUser("Error,could not retrieve data!");
                }

            });
        }
        catch(Exception e){
            Log.d("RankingActivity","Exception: " + e.toString());
        }
    }
    //Builds the RecyclerView
    private void buildRecyclerView(){
        mAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(mAdapter);
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

}
