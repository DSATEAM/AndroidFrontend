package edu.upc.eetac.dsa.lastsurvivorfrontend;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.RankingService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GraphsActivity extends AppCompatActivity {

    private ProgressBar pb_circular;
    List<Player> playerList = new LinkedList<>();

    Player player = new Player();
    private static Retrofit retrofit;
    private static String retrofitIpAddress;
    RankingService rankingService;
    public TextView username;

    float x1, x2, y1, y2;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        pb_circular = findViewById(R.id.progressBar_cyclic);

        ListView lv = (ListView) findViewById(R.id.listView1);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //Attaching Interceptor to a client
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();
        ResourceFileReader rs = new ResourceFileReader();
        retrofitIpAddress = ResourceFileReader.ReadResourceFileFromStringNameKey("retrofit.IpAddress", this);
        startRetrofit();
        rankingService = retrofit.create(RankingService.class);
        getRank(lv);

        pb_circular.setVisibility(View.GONE);

    }

    class ChartDataAdapter extends ArrayAdapter<BarData> {
        public ChartDataAdapter(Context context, List<BarData> objects, List<Player> playerList) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            BarData data = getItem(position);

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().from(getContext()).inflate(R.layout.activity_graph_aux, null);
                TextView username = findViewById(R.id.playerNameChart);
                holder.chart = (BarChart) convertView.findViewById(R.id.chart);
                holder.username = (TextView) convertView.findViewById(R.id.playerNameChart);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(15);//el tamaño de los valores de las barras
            //PARAMETROS DE PINTAR LOS GRAFICOS
            holder.chart.getDescription().setEnabled(false);
            holder.chart.setScaleEnabled(false);//desactivar el zoom de barras
            holder.chart.setDrawGridBackground(false);
            holder.chart.setBackgroundColor(Color.WHITE);
            holder.chart.setDrawValueAboveBar(true);
            holder.chart.setFitBars(true);
            holder.chart.getDescription().setEnabled(false);
            holder.chart.getDescription().setTextSize(20);


            holder.chart.getDescription().setText(playerList.get(position).getUsername());
            holder.username.setText(playerList.get(position).getUsername());

            // holder.chart.getDescription().setPosition();

            ViewHolder finalHolder = holder;


            final ArrayList<String> xVals = new ArrayList<>();
            final String[] labels = {"experience", "kills", "games", "monsters per game"};

            Legend l = holder.chart.getLegend();
            l.setFormSize(10f); // set the size of the legend forms/shapes
            l.setForm(Legend.LegendForm.DEFAULT); // set what type of form/shape should be used
            // l.setPosition(LegendPosition.BELOW_CHART_LEFT);
            //l.setTypeface(...);
            l.setTextSize(12f);
            l.setTextColor(Color.BLACK);
            l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
            l.getCalculatedLabelSizes();

            // set custom labels and colors

            data.setValueTextColor(Color.BLACK);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1);

            xAxis.setLabelCount(4);
            xAxis.setDrawLabels(false);
            xAxis.setTextSize(20);
            // xAxis.setAxisMaximum(1);
            xAxis.setCenterAxisLabels(false);

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis = holder.chart.getAxisLeft();
            holder.chart.getAxisLeft().setDrawLabels(false);
            holder.chart.getAxisLeft().setDrawAxisLine(true);
            holder.chart.getAxisLeft().setDrawGridLines(false);
            leftAxis.setLabelCount(5, false);
            leftAxis.setTextColor(Color.BLACK);
            leftAxis.setAxisMinimum(0);//colocar donde empieza el grafico
            leftAxis.setSpaceTop(15f);
            holder.chart.getAxisRight().setDrawAxisLine(true);
            holder.chart.getAxisRight().setDrawLabels(true);
            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setLabelCount(5, true);
            rightAxis.setAxisMinimum(0);
            rightAxis.setSpaceTop(15f);
            rightAxis.setSpaceBottom(0);
            holder.chart.setExtraTopOffset(60);
            holder.chart.setData(data);
            holder.chart.setFitBars(true);
            holder.chart.animateY(500);
            pb_circular.setVisibility(View.GONE);

            return convertView;

        }

        class ViewHolder {
            public TextView username;
            BarChart chart;
        }
    }

    private BarData generateData(int count, List<Player> playerList) {//pasar posicion de un jugador y la lista jugadores
        ArrayList<BarEntry> entries = new ArrayList<>();


        entries.add(new BarEntry(0, (int) playerList.get(count).getExperience()));
        entries.add(new BarEntry(1, (float) playerList.get(count).getKills()));
        entries.add(new BarEntry(2, (float) playerList.get(count).getGamesPlayed()));
        entries.add(new BarEntry(3, (float) (playerList.get(count).getKills()) / (playerList.get(count).getGamesPlayed())));
        // username.setText(playerList.get(count).getUsername());
        BarDataSet dataSet = new BarDataSet(entries, "");


        dataSet.setLabel("Experience · Kills · Games · Kills x Game  ");

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setValueTextSize(40f); si se introduce aqui luego causa errores al volver a entrar al al GraphsActivity desde el rankingactivity creando numeros monstruosamente grandes
        dataSet.setBarShadowColor(Color.rgb(203, 203, 203));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);


        return data;
    }

    private static void startRetrofit() {
        //HTTP &
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //Attaching Interceptor to a client
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + retrofitIpAddress + ":8080/Backend/")
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private void getRank(ListView lv) {

        pb_circular.setVisibility(View.VISIBLE);
        try {

            Call<List<Player>> playersStats = rankingService.rankingList();
            /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
            playersStats.enqueue(new Callback<List<Player>>() {
                @Override
                public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {

                    pb_circular.setVisibility(View.GONE);
                    //Retrieve the result containing in the body
                    if (!response.body().isEmpty()) {
                        // non empty response, Mapping Json via Gson...
                        Log.d("GraphsActivity", "Server Response Ok");
                        playerList = response.body();

                        ArrayList<BarData> list = new ArrayList<>();
                        for (int i = 0; i < playerList.size(); i++) //aqui deberia de haber la cantidad de graficos "items" que queremos poner insertar
                        {
                            if (playerList.get(i).getExperience() > 0) {
                                list.add(generateData(i, playerList));
                            }
                        }
                        ChartDataAdapter chartDataAdapter = new ChartDataAdapter(getApplicationContext(), list, playerList);//pasar como parametro tambien playerlist?
                        lv.setAdapter(chartDataAdapter);

                    } else {
                        // empty response...
                        Log.d("GraphsActivity", "Request Failed!");
                    }
                }

                @Override
                public void onFailure(Call<List<Player>> call, Throwable t) {
                    //  NotifyUser("Error,could not retrieve data!");
                }
            });
        } catch (Exception e) {
            Log.d("GraphsActivity", "Exception: " + e.toString());
        }

    }
}