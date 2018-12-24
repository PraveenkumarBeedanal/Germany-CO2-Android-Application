package com.thesis.carbon;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.thesis.carbon.Constants.increv;

public class MainActivityex extends Activity {
    private CombinedChart mChart;

    private String formattedDate;
    DatabaseReference rootRef;

    DatabaseReference co2ref;
    List<Integer> emissionList = new ArrayList<>();
    List<String> timeList = new ArrayList<>();
    private float co2EmissionValue;
    private String co2EmissionTime;

    private Button line_btn;
    private Button chart_btn;

    DatabaseReference rootRef1;
    DatabaseReference resorceref;

    ArrayList<Integer> arrayList = new ArrayList<>();
    ArrayList<Integer> arrayList1 = new ArrayList<>();
    ArrayList<Integer> arrayList2 = new ArrayList<>();
    ArrayList<Integer> arrayList3 = new ArrayList<>();
    ArrayList<Integer> arrayList4 = new ArrayList<>();
    ArrayList<Integer> arrayList5 = new ArrayList<>();
    ArrayList<Integer> arrayList6 = new ArrayList<>();
    ArrayList<Integer> arrayList7 = new ArrayList<>();
    ArrayList<Integer> arrayList8 = new ArrayList<>();
    ArrayList<Integer> arrayList9 = new ArrayList<>();

    ArrayList<Integer> colorist = new ArrayList<>();
    ArrayList<String> xAxisLabel = new ArrayList<>();

    private int valc;
    private int size;

    String[] nameStr = new String[]{"Biomass","Fossil coal","Fossil Gas","Fossil oil","Geothermal","Hydro Storage","Nuclear","Solar","Wind","Other"};
    private String str_currentDate;
    private String str_currentDate2;

    CombinedData data;

    private long current_timestamp;
    private long yesterday_timestamp;
    private long fetch_timestamp;

    List<ResourcesModel> resourcesModelList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mChart = (CombinedChart) findViewById(R.id.chart1);
        chart_btn = (Button)findViewById(R.id.chart_btn);
        line_btn = (Button)findViewById(R.id.line_btn);

        valc = getIntent().getExtras().getInt("count");
        size = getIntent().getExtras().getInt("size");

        rootRef1 = FirebaseDatabase.getInstance().getReference();
        resorceref = rootRef1.child("resources");

         data = new CombinedData();

        nameStr = new String[0];
        Arrays.fill(nameStr, null);
        arrayList.clear();
        arrayList1.clear();
        arrayList2.clear();
        arrayList3.clear();
        arrayList4.clear();
        arrayList5.clear();
        arrayList6.clear();
        arrayList7.clear();
        arrayList8.clear();
        arrayList9.clear();
        colorist.clear();

        emissionList.clear();
        timeList.clear();

        rootRef = FirebaseDatabase.getInstance().getReference();
        co2ref = rootRef.child("emission_factor");

        firebasedata345();

        line_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(line_btn.getText().toString().equals("Combine Both")){
                    line_btn.setText("Highlight CO2 Emission");
                    data.setData(generateLineData());
                    data.setData(generateBarData());
                    mChart.setData(data);
                    mChart.invalidate();
                }else {
                    line_btn.setText("Combine Both");
                    mChart.getData().removeDataSet(mChart.getData().getBarData().getDataSetByIndex(0));
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();

                    data.setData(generateLineData());
                    mChart.setData(data);
                    mChart.invalidate();
                }
            }
        });

        chart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(chart_btn.getText().toString().equals("Combine Both")){
                    chart_btn.setText("Highlight Resources");

                    data.setData(generateBarData());
                    data.setData(generateLineData());
                    mChart.setData(data);
                    mChart.invalidate();
                }else {
                    chart_btn.setText("Combine Both");
                    mChart.getData().removeDataSet(mChart.getData().getLineData().getDataSetByIndex(0));
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();

                    data.setData(generateBarData());
                    mChart.setData(data);
                    mChart.invalidate();
                }

            }
        });

    }

    public void firebasedata345(){
        Query lastQuery = resorceref.orderByKey().limitToLast(1);

        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String periodStart11 = postSnapshot.child("periodStart").getValue(String.class);
                        String timep1 = periodStart11.substring(0, 8);

                        try{
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                            Date df = sdf.parse(periodStart11);
                            current_timestamp = df.getTime();

                            Calendar c = Calendar.getInstance();
                            c.setTime(df);
                            c.add(Calendar.HOUR, -24);

                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyMMddHHmm");
                            str_currentDate2 = simpleDateFormat1.format(c.getTime());
                            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddhhmm");
                            Date d1f = sdf1.parse(str_currentDate2);
                            yesterday_timestamp = d1f.getTime();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    firebasedata();
                    firebasedatastack();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        //
    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        entries = getLineEntriesData(entries);

        LineDataSet set = new LineDataSet(entries, "CO2 Emission");
        set.setColor(Color.WHITE);
      //  set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setFillColor(Color.rgb(240, 238, 70));
      //  set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        d.addDataSet(set);



        return d;
    }

    private ArrayList<Entry> getLineEntriesData(ArrayList<Entry> entries){

        for(int i=0;i<emissionList.size();i++){

            int i2 = emissionList.get(i);
            entries.add(new BarEntry(i+1, i2));
        }
        return entries;
    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        entries = getBarEnteries(entries);

        BarDataSet set1 = new BarDataSet(entries, "");
        //set1.setColor(Color.rgb(60, 220, 78));
        set1.setColors(getColors());
        set1.setDrawValues(false);
        set1.setStackLabels(new String[]{"Biomass","Fossil coal","Fossil Gas","Fossil oil","Geothermal","Hydro Storage","Nuclear","Solar","Wind","Other"});
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f; // x2 dataset


        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);


        return d;
    }

    private int[] getColors() {

        colorist.add(ContextCompat.getColor(this, R.color.colorBlue));
        colorist.add(ContextCompat.getColor(this, R.color.colorPink));
        colorist.add(ContextCompat.getColor(this, R.color.colorPurple));
        colorist.add(ContextCompat.getColor(this, R.color.colorl));
        colorist.add(ContextCompat.getColor(this, R.color.colors2));
        colorist.add(ContextCompat.getColor(this, R.color.colors3));
        colorist.add(ContextCompat.getColor(this, R.color.colors4));
        colorist.add(ContextCompat.getColor(this, R.color.colors5));
        colorist.add(ContextCompat.getColor(this, R.color.colors9));
        colorist.add(ContextCompat.getColor(this, R.color.colors7));

        // have as many colors as stack-values per entry
        int[] colors = new int[colorist.size()];

        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorist.get(i);
        }

        return colors;
    }

    private ArrayList<BarEntry> getBarEnteries(ArrayList<BarEntry> entries){

        for (int i = 0; i < resourcesModelList.size(); i++) {
            /*float val1 = arrayList.get(i);
            float val2 = arrayList1.get(i);
            float val3 = arrayList2.get(i);
            float val4 = arrayList3.get(i);
            float val5 = arrayList4.get(i);
            float val6 = arrayList5.get(i);
            float val7 = arrayList6.get(i);
            float val8 = arrayList8.get(i);
            float val9 = arrayList9.get(i);
            float val10 = arrayList7.get(i);*/

            float val1 = resourcesModelList.get(i).getBiomass_val();
            float val2 = resourcesModelList.get(i).getFossil_coal_val();
            float val3 = resourcesModelList.get(i).getFossil_gas_val();
            float val4 = resourcesModelList.get(i).getFossil_oil_val();
            float val5 = resourcesModelList.get(i).getGeothermal_val();
            float val6 = resourcesModelList.get(i).getHydrostorage_val();
            float val7 = resourcesModelList.get(i).getNuclear_val();
            float val8 = resourcesModelList.get(i).getSolar_val();
            float val9 = resourcesModelList.get(i).getWind_val();
            float val10 = resourcesModelList.get(i).getOther_val();

            entries.add(new BarEntry(
                    i, new float[]{val1, val2, val3, val4, val5, val6, val7, val8, val9, val10}));
        }
        return  entries;
    }

    public void firebasedata() {
        co2ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    String periodStart11 = postSnapshot.child("periodStart").getValue(String.class);

                    try{
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
                        Date df = sdf.parse(periodStart11);
                        fetch_timestamp = df.getTime();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (fetch_timestamp>=yesterday_timestamp && fetch_timestamp<=current_timestamp){
                        co2EmissionTime = postSnapshot.child("periodStart").getValue(String.class);
                        String timep = co2EmissionTime.substring(8,10);
                        String timep11 = co2EmissionTime.substring(10,12);
                        String cancot = timep+":"+timep11;
                        Constants.timestr.add(cancot);
                        co2EmissionValue = postSnapshot.child("co2").getValue(Float.class);

                        int co2int = Math.round(co2EmissionValue);

                        emissionList.add(co2int);
                        timeList.add(cancot);
                    }
                }

                ArrayList<Entry> entries = new ArrayList<>();

                for(int k = 0;k<emissionList.size();k++){
                    entries.add(new Entry(k, emissionList.get(k)));
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }


    public void firebasedatastack() {
        resorceref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        String periodStart11 = postSnapshot.child("periodStart").getValue(String.class);

                        try{
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
                            Date df = sdf.parse(periodStart11);
                            fetch_timestamp = df.getTime();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if (fetch_timestamp>=yesterday_timestamp && fetch_timestamp<=current_timestamp){

                            ResourcesModel resourcesModel = new ResourcesModel();

                            int biomass = postSnapshot.child("Biomass").getValue(Integer.class);
                            String biomass1 = String.valueOf(postSnapshot.child("Biomass").getKey());
                            arrayList.add(biomass);

                            resourcesModel.setBiomass_val(biomass);

                            int brown_coal = postSnapshot.child("Fossil Brown coal").getValue(Integer.class);
                            String brown_coal1 = String.valueOf(postSnapshot.child("Fossil Brown coal").getKey());
                            int hard_coal = postSnapshot.child("Fossil Hard coal").getValue(Integer.class);
                            String hard_coal1 = String.valueOf(postSnapshot.child("Fossil Hard coal").getKey());
                            int sum_fossil_coal = brown_coal+hard_coal;
                            arrayList1.add(sum_fossil_coal);

                            resourcesModel.setFossil_coal_val(sum_fossil_coal);

                            int coal_gas = postSnapshot.child("Fossil Coal Gas").getValue(Integer.class);
                            String coal_gas1 = String.valueOf(postSnapshot.child("Fossil Coal Gas").getKey());
                            int fossil_gas = postSnapshot.child("Fossil Gas").getValue(Integer.class);
                            int sum_fossil_gas = coal_gas+fossil_gas;
                            String fossil_gas1 = String.valueOf(postSnapshot.child("Fossil Gas").getKey());
                            arrayList2.add(sum_fossil_gas);

                            resourcesModel.setFossil_coal_val(sum_fossil_gas);

                            int fossil_oil = postSnapshot.child("Fossil oil").getValue(Integer.class);
                            String fossil_oil1 = String.valueOf(postSnapshot.child("Fossil oil").getKey());
                            arrayList3.add(fossil_oil);

                            resourcesModel.setFossil_oil_val(fossil_oil);

                            int geothermal = postSnapshot.child("Geothermal").getValue(Integer.class);
                            String geothermal1 = String.valueOf(postSnapshot.child("Geothermal").getKey());
                            arrayList4.add(geothermal);

                            resourcesModel.setGeothermal_val(geothermal);

                            int hydro_poundage = postSnapshot.child("Hydro Poundage").getValue(Integer.class);
                            String hydro_poundage1 = String.valueOf(postSnapshot.child("Hydro Poundage").getKey());
                            int hydro_reserviour = postSnapshot.child("Hydro Reserviour").getValue(Integer.class);
                            String hydro_reserviour1 = String.valueOf(postSnapshot.child("Hydro Reserviour").getKey());
                            int hydro_storage = postSnapshot.child("Hydro Storage").getValue(Integer.class);
                            String hydro_storage1 = String.valueOf(postSnapshot.child("Hydro Storage").getKey());
                            int sum_hydro = hydro_poundage+hydro_reserviour+hydro_storage;
                            arrayList5.add(sum_hydro);

                            resourcesModel.setHydrostorage_val(sum_hydro);

                            int nuclear = postSnapshot.child("Nuclear").getValue(Integer.class);
                            String nuclear1 = String.valueOf(postSnapshot.child("Nuclear").getKey());
                            arrayList6.add(nuclear);

                            resourcesModel.setNuclear_val(nuclear);

                            int solar = postSnapshot.child("Solar").getValue(Integer.class);
                            String solar1 = String.valueOf(postSnapshot.child("Solar").getKey());
                            arrayList8.add(solar);

                            resourcesModel.setSolar_val(solar);

                            int wind_onshore = postSnapshot.child("Wind Onshore").getValue(Integer.class);
                            String wind_onshore1 = String.valueOf(postSnapshot.child("Wind Onshore").getKey());
                            int wind_offshore = postSnapshot.child("Wind offshore").getValue(Integer.class);
                            String wind_offshore1 = String.valueOf(postSnapshot.child("Wind offshore").getKey());
                            int sum_wind = wind_onshore+wind_offshore;
                            arrayList9.add(sum_wind);

                            resourcesModel.setWind_val(sum_wind);

                            int other = postSnapshot.child("Other").getValue(Integer.class);
                            String other1 = String.valueOf(postSnapshot.child("Other").getKey());
                            int other_renewable = postSnapshot.child("Other Renewable").getValue(Integer.class);
                            String other_renewable1 = String.valueOf(postSnapshot.child("Other Renewable").getKey());
                            int waste = postSnapshot.child("waste").getValue(Integer.class);
                            String waste1 = String.valueOf(postSnapshot.child("waste").getKey());
                            int sum_other = other+other_renewable+waste;
                            arrayList7.add(sum_other);

                            resourcesModel.setOther_val(sum_other);

                            resourcesModelList.add(resourcesModel);

                            Log.d("fdgg", biomass+"/"+sum_fossil_coal+"/"+sum_fossil_gas+"/"+fossil_oil+"/"+geothermal+"/"+sum_hydro+"/"+nuclear+"/"+sum_other+"/"+solar+"/"+sum_wind+"");
                        }
                    }


                    mChart.getDescription().setEnabled(false);
                    mChart.setBackgroundColor(Color.TRANSPARENT);
                    mChart.setDrawGridBackground(false);
                    mChart.setDrawBarShadow(false);

                    mChart.setDrawValueAboveBar(false);

                    // draw bars behind lines
                    mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                            CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
                    });

                    Legend l = mChart.getLegend();
                    l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                    l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
                    l.setOrientation(Legend.LegendOrientation.VERTICAL);
                    l.setDrawInside(false);
                    l.setFormSize(8f);
                    l.setFormToTextSpace(4f);
                    l.setXEntrySpace(6f);
                    l.setTextColor(Color.WHITE);

                    YAxis rightAxis = mChart.getAxisRight();
                    rightAxis.setDrawGridLines(false);
                    rightAxis.setAxisMinimum(0f);
                    rightAxis.setTextColor(Color.WHITE);
                    rightAxis.setTextSize(12f);

                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.setDrawGridLines(false);
                    leftAxis.setAxisMinimum(0f);
                    leftAxis.setTextColor(Color.WHITE);
                    leftAxis.setTextSize(12f);// this replaces setStartAtZero(true)

                    XAxis xAxis = mChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setTextColor(Color.WHITE);
                    xAxis.setTextSize(12f);
                    xAxis.setAxisMinimum(0f);

                    xAxis.setGranularity(1f); // only intervals of 1 day

                    xAxis.setLabelCount(15);

                    xAxis.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return Constants.timestr.get((int) value);
                        }
                    });



                    if(arrayList.size()!=0){
                        data.setData( generateLineData());
                        data.setData(generateBarData());
                        mChart.setData(data);
                        mChart.invalidate();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }


}
