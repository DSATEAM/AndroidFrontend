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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
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

public class GraphsActivity extends AppCompatActivity {

    private ProgressBar pb_circular;
    List<Player> playerList = new LinkedList<>();
    Player player = new Player();


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        //playerList = getIntent().getParcelableArrayListExtra("playerList");//recuperamos los jugadores
        pb_circular = findViewById(R.id.progressBar_cyclic);
        //player = getIntent().getParcelableExtra("Player");
        //playerList = getIntent().getParcelableArrayListExtra("playerList");
        // Bundle bundle = getIntent().getExtras();
        // playerList = bundle.getParcelable("data");
        int size= playerList.size();
        ListView lv = (ListView) findViewById(R.id.listView1);
        Log.d("Cantidad de jugadores: ", String.valueOf(playerList.size()));


        final String[] Nombres = new String[] {"Midoriya", "kru", "juan", "paco"};
        ArrayList<BarData> list = new ArrayList<>();

        for (int i = 0; i<6; i++) {//aqui deberia de haber la cantidad de graficos que queremos poner,teniendo un intento no fallido extraer playerlist.size();
            list.add(generateData(i + 1));

        }
        ChartDataAdapter chartDataAdapter = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(chartDataAdapter);
        pb_circular.setVisibility(View.GONE);

    }

    class ChartDataAdapter extends ArrayAdapter<BarData> {
        public ChartDataAdapter(Context context, List<BarData> objects) {
            super(context, 0, objects);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            Log.d("Cantidad de jugadores: ", String.valueOf(playerList.size()));


            BarData data = getItem(position);
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().from(getContext()).inflate(R.layout.activity_graph_aux, null);
                holder.chart = (BarChart) convertView.findViewById(R.id.chart);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

            }

            data.setValueTextColor(Color.BLACK);
            //PARAMETROS DE PINTAR LOS GRAFICOS
            holder.chart.getDescription().setEnabled(false);
            holder.chart.setScaleEnabled(false);//desactivar el zoom de barras
            holder.chart.setDrawGridBackground(false);
            holder.chart.setBackgroundColor(Color.WHITE);
            //holder.chart.setDrawBarShadow(true);
            ViewHolder finalHolder = holder;
            holder.chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    finalHolder.chart.getXAxis().getValueFormatter().getFormattedValue(e.getX(), finalHolder.chart.getXAxis());
                }

                @Override
                public void onNothingSelected() {

                }
            });

            final String[] labels = new String[] {"Experience", "Kills", "GamesPlayed", "Credits"};
            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(labels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1);
            xAxis.setTextColor(Color.WHITE);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            // xAxis.setAxisMaximum(1);
            // xAxis.setCenterAxisLabels(true);

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis = holder.chart.getAxisLeft();
            leftAxis.setLabelCount(5, false);
            leftAxis.setTextColor(Color.WHITE);
            leftAxis.setSpaceTop(15f);
            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setLabelCount(5, false);
            rightAxis.setSpaceTop(15f);

            holder.chart.setData(data);
            holder.chart.setFitBars(true);
            holder.chart.animateY(750);
            //holder.chart.animateXY(500, 500);//este valdria para animar en ambos sentidos
            pb_circular.setVisibility(View.GONE);

            return convertView;


            //return super.getView(position,convertView,parent);

        }
        class ViewHolder {
            BarChart chart;
        }
    }
    private BarData generateData(int count) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 5; i++) {//las barras serian os campos recibidos de cada objeto player
            entries.add(new BarEntry(i, (float) (Math.random() * 90) + 30)); //valor de las barras exp,kills, etc

        }
        // string del user 0 de la lista
        BarDataSet dataSet = new BarDataSet(entries, "Jugador: " + count);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setBarShadowColor(Color.rgb(203, 203, 203));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);
        return data;
    }


    public class MyXAxisValueFormatter extends IndexAxisValueFormatter {
        private String[] mValues;
        public MyXAxisValueFormatter(String[] values){
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis){
            return mValues[(int)value];
        }

    }
}
        /*for (Player player : playerList)
            Log.d("value is" , playerList.toString());*/
//ArrayList
        /*mpLinechart=(LineChart) findViewById(R.id.line_chart);
        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(),"Data Set 1");//Datos1
        LineDataSet lineDataSet2 = new LineDataSet(dataValues1(),"Data Set 2");//Datos2
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        TextView text = findViewById(R.id.textView9);
        Intent intent = getIntent();

        //ArrayList<String> mylist= getIntent().getStringArrayListExtra("mylist");

        //text.setText((CharSequence) mylist);


        //caracteristicas del grafico
        mpLinechart.setNoDataText("NO DATA");
        mpLinechart.setNoDataTextColor(Color.RED);
        mpLinechart.setBackgroundColor(Color.WHITE);
        mpLinechart.setDrawGridBackground(true);
        mpLinechart.setDrawBorders(true);
        mpLinechart.setBorderColor(Color.RED);

        LineData data = new LineData(dataSets);
        mpLinechart.setData(data);// pone los datos en el grafico, si no hay salta el no data
        mpLinechart.invalidate();
        pb_circular.setVisibility(View.GONE);*/

//datos para añadir, aqui se deberia de poner datos recibidos de BBDD
   /* private ArrayList<Entry> dataValues1()
    {
        ArrayList<Entry>dataVals= new ArrayList<Entry>();
        dataVals.add((Entry) new Entry(0,80));
        dataVals.add((Entry) new Entry(1,0));
        dataVals.add((Entry) new Entry(2,35));
        dataVals.add((Entry) new Entry(3,5));
        dataVals.add((Entry) new Entry(4,70));
        return dataVals;
    }
    private ArrayList<Entry> dataValues2()
    {
        ArrayList<Entry>dataVals= new ArrayList<Entry>();
        dataVals.add((Entry) new Entry(0,20));
        dataVals.add((Entry) new Entry(1,30));
        dataVals.add((Entry) new Entry(2,20));
        dataVals.add((Entry) new Entry(3,60));
        dataVals.add((Entry) new Entry(4,20));
        return dataVals;
    }*/
   /* private String getPlayerStats(){
        int baseExp = 50;
        int level = player.getExperience() /baseExp;
        int exp = player.getExperience()%baseExp;
        return "P,"+level+","+exp+","+player.getKills()+","+player.getCredits();
    }*/
