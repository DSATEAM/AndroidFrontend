package edu.upc.eetac.dsa.lastsurvivorfrontend;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Item;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.StoreService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class StoreActivity extends AppCompatActivity{
        // RETROFIT OBJECT
        private static Retrofit retrofit;
        private static String retrofitIpAddress;

        //TRACKS SERVICE OBJECT
        StoreService storeService;
        List<Item> itemsList ;
        private RecyclerView recyclerView;
        //As we added new methods inside our custom Adapter, we need to create our own type of adapter
        private StoreAdapter mAdapter;
        private static int playerStatsDetail = 3;
        private boolean aBooleanServedAlready =false;
        private RecyclerView.LayoutManager layoutManager;
        private ProgressBar pb_circular;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inventory);

            //Implementing RecyclerView
            recyclerView = findViewById(R.id.my_recycler_view);
            pb_circular = findViewById(R.id.progressBar_cyclic);
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
            ResourceFileReader rs =  new ResourceFileReader();
            retrofitIpAddress = ResourceFileReader.ReadResourceFileFromStringNameKey("retrofit.IpAddress",this);
            startRetrofit();
            storeService = retrofit.create(StoreService.class);
            getItems();
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
    //User Notifier Handler using Toast
    private void NotifyUser(String MSG){
        Toast toast = Toast.makeText(edu.upc.eetac.dsa.lastsurvivorfrontend.StoreActivity.this,MSG,Toast.LENGTH_SHORT);
        toast.show();
    }
    public void onBackButtonClick(View view) {
        exitDialog();
    }
    //Gets the List of Tracks from LocalHost


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
    private void getItems() {
        pb_circular.setVisibility(View.VISIBLE);
        try {

            Call<List<Item>> itemStats = storeService.itemList();
            /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
            itemStats.enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    pb_circular.setVisibility(View.GONE);
                    //Retrieve the result containing in the body
                    if (!response.body().isEmpty()) {
                        // non empty response, Mapping Json via Gson...
                        Log.d("StoreActivity", "Server Response Ok");
                        itemsList = response.body();
                        //If clicked once then new player list else update the recyclerview
                        if (mAdapter == null) {
                            buildRecyclerView();
                        } else {
                            mAdapter = null;
                            buildRecyclerView();
                        }

                    } else {
                        // empty response...
                        Log.d("StoreActivity", "Request Failed!");
                    }
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {
                    NotifyUser("Error,could not retrieve data!");
                }
            });
        } catch (Exception e) {
            Log.d("RankingActivity", "Exception: " + e.toString());
        }
    }

    private void buildRecyclerView() {
        mAdapter = new StoreAdapter(itemsList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //TODO NEED TO IMPLEMENT PLAYER STATS DETAIL ACTIVITY
                LaunchItemsDetailPopup(position);
            }
        });
    }

    private void LaunchItemsDetailPopup(int position) {
        //Create Multiline String of Choosen Player Stats
        pb_circular.setVisibility(View.VISIBLE);
        String statsString = itemsList.get(position).getName() + "\n" + itemsList.get(position).getRarity() +
                "\n" + itemsList.get(position).getDescription() + "\n" + itemsList.get(position).getCredit();
        //Create a dialog as unchanging
        final Dialog dialog = new Dialog(StoreActivity.this);
        dialog.setContentView(R.layout.dialog_itemstats);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        //Get the Views of the created dialog
        LinearLayout button_buy = dialog.findViewById(R.id.button_buyItem);
        LinearLayout button_back = dialog.findViewById(R.id.button_back);
        TextView headText = dialog.findViewById(R.id.text_Username);
        EditText multilineText = dialog.findViewById(R.id.text_multiline);
        String TAG = "onAddItem";
        button_buy.setOnClickListener(v -> {
            try {
                String user = getIntent().getStringExtra("parentId");
                itemsList.get(position).setParentId(user);
                Call<Item> itemID = storeService.addItem(itemsList.get(position));
                Gson gson = new Gson();
                String jsonInString = gson.toJson(itemsList.get(position));
                Log.d(TAG, "ItemGson: "+jsonInString);
                Log.d(TAG, "onAddItemClicked: "+ itemID.toString());
                /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
                itemID.enqueue(new Callback<Item>() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if (response.code() == 201) {
                            NotifyUser("Successful");
                            //Item itemRcv = response.body();
                        } else if (response.code() == 400) { // Bad Request
                            NotifyUser("Unsuccessful!");
                        } else if (response.code() == 402) { //Not enough credits
                            NotifyUser("Not enough credits");
                        } else {
                            NotifyUser("You already have this Item");
                        }
                    }
                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {
                        NotifyUser("Error");
                    }
                });
            }
            catch (Exception e) {
                Log.d("StoreActivity", "Exception: " + e.toString());
            }
        });
                button_back.setOnClickListener(
                        v1 -> dialog.dismiss());
                //Set the username and Multiline String of Choosen Player Stats
                headText.setText(itemsList.get(position).getName());
                multilineText.setText(statsString);
                pb_circular.setVisibility(View.INVISIBLE);
                dialog.show();
        }



    @Override
    public void onBackPressed() {
        // do something on back.
        exitDialog();
    }
    private void exitDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        LinearLayout cancel = dialog.findViewById(R.id.cancelbtn);
        LinearLayout exit = dialog.findViewById(R.id.button_back);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        dialog.show();

    }
}
