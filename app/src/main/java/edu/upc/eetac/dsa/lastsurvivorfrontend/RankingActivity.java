package edu.upc.eetac.dsa.lastsurvivorfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.PlayerService;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.RankingService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RankingActivity extends AppCompatActivity {
    // RETROFIT OBJECT
    private static Retrofit retrofit;
    //HTTP INTERCEPTOR

    //TRACKS SERVICE OBJECT
    RankingService rankingService;
    //List<Repo> Repo_List;
    List<Player> playerList ;
    private RecyclerView recyclerView;
    //As we added new methods inside our custom Adapter, we need to create our own type of adapter
    private MyAdapter mAdapter;
    private static int playerStatsDetail = 3;
    private boolean aBooleanServedAlready =false;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        //Implementing RecyclerView
        recyclerView = findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //HTTP &
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //Attaching Interceptor to a client
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();

        // Running Retrofit to get result from Local tracks service Interface
        //Remember when using Local host on windows the IP is 10.0.2.2 for Android
        //Also added NullOnEmptyConverterFactory when the response from server is empty
        startRetrofit();
        rankingService = retrofit.create(RankingService.class);

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
        Toast toast = Toast.makeText(RankingActivity.this,MSG,Toast.LENGTH_SHORT);
        toast.show();
    }
    //Gets the List of Tracks from LocalHost
    public void onButtonMyCurrentRankClick(View view) {
        //Retrofit Implementation on Button Press
        //Adding Interceptor
        try {
            Call<List<Player>> playersStats = rankingService.rankingList();
            /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
            playersStats.enqueue(new Callback<List<Player>>() {
                @Override
                public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {

                    //Retrieve the result containing in the body
                    if (!response.body().isEmpty()) {
                        // non empty response, Mapping Json via Gson...
                        NotifyUser("Server Response Ok");
                        playerList = response.body();
                        buildRecyclerView();
                    } else {
                        // empty response...
                        NotifyUser("Request Failed!");
                    }
                }

                @Override
                public void onFailure(Call<List<Player>> call, Throwable t) {
                    NotifyUser("Error,could not retrieve data!");
                }
            });
        }
        catch(Exception e){
            NotifyUser("Exception: " + e.toString());
        }
    }
    //Builds the RecyclerView
    private void buildRecyclerView(){
        mAdapter = new MyAdapter(playerList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //TODO NEED TO IMPLEMENT PLAYER STATS DETAIL ACTIVITY
                NotifyUser("On Item Clicked!");
            }
        });
    }
    //Launch New Activity to Edit-Add Track
    private void LaunchPlayerDetailedActivity(int position){
        /*
        //New View to edit Current Track
        //Launches a new activity to edit the current selected Track Item
        //Creates a Edit Track Activity
        //TO LAUNCH A NEW ACTIVITY WE CREATE A INTENT OF OUR Player Details Activity
        Intent intent = new Intent(RankingActivity.this ,PlayerDetailsActivity.class);
        // Pass the data to our Activity as there exists no object instance of our PlayerDetailedActivity class,
        // The only easy method to Pass data between activity is Intent,as singleton is not recommended
        intent.putExtra("PlayerUsername", playerList.get(position).getUsername());
        intent.putExtra("PlayerExperience", playerList.get(position).getExperience());
        intent.putExtra("PlayerKills", playerList.get(position).getKills());
        intent.putExtra("PlayerGamesPlayed", playerList.get(position).getGamesPlayed());
        intent.putExtra("PlayerCoins", playerList.get(position).getCoins());
            //STARTS THE ACTIVITY FOR RESULT INTENT TO GET THE NEW VALUES
        startActivityForResult(intent,playerStatsDetail);
    */
    }
}
