package com.thesis.carbon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.thesis.carbon.Constants.arrayList1;
import static com.thesis.carbon.Constants.arrayList2;
import static com.thesis.carbon.Constants.arrayList3;
import static com.thesis.carbon.Constants.arrayList4;
import static com.thesis.carbon.Constants.arrayList5;
import static com.thesis.carbon.Constants.arrayList6;
import static com.thesis.carbon.Constants.arrayList7;
import static com.thesis.carbon.Constants.arrayList8;
import static com.thesis.carbon.Constants.arrayList9;
import static com.thesis.carbon.Constants.arrayList10;
import static com.thesis.carbon.Constants.arrayList11;
import static com.thesis.carbon.Constants.arrayList12;
import static com.thesis.carbon.Constants.arrayList13;
import static com.thesis.carbon.Constants.arrayList14;
import static com.thesis.carbon.Constants.arrayList15;
import static com.thesis.carbon.Constants.arrayList16;
import static com.thesis.carbon.Constants.arrayList17;
import static com.thesis.carbon.Constants.arrayList;
import static com.thesis.carbon.Constants.increv;
import static com.thesis.carbon.Constants.timestr;

public class HomeActivity extends Activity {

    ViewPager viewPager;

    Integer[] imageId = {R.drawable.co2, R.drawable.co2};
    //String[] imagesName = {"205","22.8"};
    ArrayList<Integer> imagesName = new ArrayList<>();
    String[] unitArray = {"gCO2/kWh","MW"};

    private FloatingActionButton fab;

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 5000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 10000;
    private static int NUM_PAGES = 0;
    private RecyclerView recycler_view;
    private List<ElectricityModel> electricityModelList = new ArrayList<>();
    private ArrayList<String> nameArray = new ArrayList<>();
    private ArrayList<Integer> valArray = new ArrayList<>();
    private ArrayList<Integer> imageArray = new ArrayList<>();
    private ArrayList<Integer> efValues = new ArrayList<>();
    private ElectricityAdapter electricityAdapter;
    DatabaseReference rootRef;
    DatabaseReference resorceref;
    DatabaseReference co2ref;

    private String co2EmissionTime;
    private String formattedDate;
    private String str_currentDate;
    private String str_currentDate2;
    private float co2EmissionValue;
    private int electricityValue;
    private long current_timestamp;
    private long yesterday_timestamp;
    private long fetch_timestamp;

    private int barcount=0;

    private FloatingActionButton analysis_fab;

    private TextView text_sync_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (InternetConnection.checkConnection(this)) {
            // Its Available...
            Toast.makeText(getApplicationContext(),"Internet Connected", Toast.LENGTH_SHORT).show();
        } else {
            // Not Available...
            Toast.makeText(getApplicationContext(),"Internet Not Connected", Toast.LENGTH_SHORT).show();

        }

        analysis_fab = (FloatingActionButton)findViewById(R.id.analysis_fab);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR,-6);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyMMddhhmm");
        SimpleDateFormat simpleDateFormat11 = new SimpleDateFormat("yyyyMMdd");
        str_currentDate = simpleDateFormat.format(c.getTime());
        formattedDate = simpleDateFormat11.format(c.getTime());

        nameArray.clear();
        valArray.clear();
        Constants.timestr.clear();

        imageArray.add(R.drawable.ic_new_biomass);
        imageArray.add(R.drawable.ic_new_fossil_coal);
        imageArray.add(R.drawable.img_gas);
        imageArray.add(R.drawable.img_gas);
        imageArray.add(R.drawable.ic_new_geoth);
        imageArray.add(R.drawable.ic_new_hydro);
        imageArray.add(R.drawable.ic_new_nuclear);
        imageArray.add(R.drawable.ic_new_solar);
        imageArray.add(R.drawable.ic_new_wind);
        imageArray.add(R.drawable.ic_new_other);


        efValues.add(18);
        efValues.add(1001);
        efValues.add(469);
        efValues.add(840);
        efValues.add(45);
        efValues.add(5);
        efValues.add(16);
        efValues.add(46);
        efValues.add(12);
        efValues.add(700);

        //  4627*0.25*469

        rootRef = FirebaseDatabase.getInstance().getReference();
        resorceref = rootRef.child("resources");
        co2ref = rootRef.child("emission_factor");

        firebasedata345();
        fetchCo2data();
        firebasedata();
        //firebasedata3();

        recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        text_sync_time = (TextView)findViewById(R.id.text_sync_time);



        analysis_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MainActivityex.class);
                intent.putExtra("size", barcount);
                intent.putExtra("count", (barcount/4));
                startActivity(intent);
                increv = 0;
            }
        });

    }

    private void fetchCo2data() {

        Query lastQuery = co2ref.orderByKey().limitToLast(1);

        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        String periodStart11 = postSnapshot.child("periodStart").getValue(String.class);
                        String timep1 = periodStart11.substring(0,8);

                        co2EmissionTime = postSnapshot.child("periodStart").getValue(String.class);
                        co2EmissionValue = postSnapshot.child("co2").getValue(Float.class);
                        electricityValue = postSnapshot.child("Electricity").getValue(Integer.class);

                        int co2int = Math.round(co2EmissionValue);
                        imagesName.add(co2int);
                        imagesName.add(electricityValue);
                       /* if(timep1.equals(formattedDate)){

                        }*/

                    }

                    if(imagesName.size()!=0){
                        viewPager = (ViewPager) findViewById(R.id.viewPager);
                        final PagerAdapter adapter = new CustomAdapter(HomeActivity.this,imageId,imagesName,unitArray);
                        viewPager.setAdapter(adapter);

                        NUM_PAGES = imageId.length;

                        final Handler handler = new Handler();
                        final Runnable Update = new Runnable() {
                            public void run() {
                                if (currentPage == NUM_PAGES) {
                                    currentPage = 0;
                                }
                                viewPager.setCurrentItem(currentPage++, true);
                            }
                        };

                        timer = new Timer(); // This will create a new Thread
                        timer .schedule(new TimerTask() { // task to be scheduled

                            @Override
                            public void run() {
                                handler.post(Update);
                            }
                        }, DELAY_MS, PERIOD_MS);
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "No data available",Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    public void fetchist(){

        for (int i=0;i<nameArray.size();i++){
            ElectricityModel  electricityModel = new ElectricityModel();
            electricityModel.setResource_name(nameArray.get(i));
            electricityModel.setResource_val(valArray.get(i));
            electricityModelList.add(electricityModel);
        }

        recycler_view.setAdapter(electricityAdapter);
        electricityAdapter.notifyDataSetChanged();
    }

    public void firebasedata(){
        Query lastQuery = resorceref.orderByKey().limitToLast(1);

        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String periodStart11 = postSnapshot.child("periodStart").getValue(String.class);
                        String timep1 = periodStart11.substring(0,8);

                        int biomass = postSnapshot.child("Biomass").getValue(Integer.class);
                        String biomass1 = String.valueOf(postSnapshot.child("Biomass").getKey());
                        nameArray.add(biomass1);
                        valArray.add(biomass);
                        int brown_coal = postSnapshot.child("Fossil Brown coal").getValue(Integer.class);
                        String brown_coal1 = String.valueOf(postSnapshot.child("Fossil Brown coal").getKey());
                        int hard_coal = postSnapshot.child("Fossil Hard coal").getValue(Integer.class);
                        String hard_coal1 = String.valueOf(postSnapshot.child("Fossil Hard coal").getKey());
                        int add_fossil = brown_coal+hard_coal;
                        nameArray.add("Fossil coal");
                        valArray.add(add_fossil);
                        int coal_gas = postSnapshot.child("Fossil Coal Gas").getValue(Integer.class);
                        String coal_gas1 = String.valueOf(postSnapshot.child("Fossil Coal Gas").getKey());
                        /*nameArray.add(coal_gas1);
                        valArray.add(coal_gas);*/
                        int fossil_gas = postSnapshot.child("Fossil Gas").getValue(Integer.class);
                        String fossil_gas1 = String.valueOf(postSnapshot.child("Fossil Gas").getKey());
                        int sum_fossil_gas = coal_gas+fossil_gas;
                        nameArray.add("Fossil Gas");
                        valArray.add(sum_fossil_gas);
                        int fossil_oil = postSnapshot.child("Fossil oil").getValue(Integer.class);
                        String fossil_oil1 = String.valueOf(postSnapshot.child("Fossil oil").getKey());
                        nameArray.add(fossil_oil1);
                        valArray.add(fossil_oil);
                        int geothermal = postSnapshot.child("Geothermal").getValue(Integer.class);
                        String geothermal1 = String.valueOf(postSnapshot.child("Geothermal").getKey());
                        nameArray.add(geothermal1);
                        valArray.add(geothermal);
                        int hydro_poundage = postSnapshot.child("Hydro Poundage").getValue(Integer.class);
                        String hydro_poundage1 = String.valueOf(postSnapshot.child("Hydro Poundage").getKey());
                        /*nameArray.add(hydro_poundage1);
                        valArray.add(hydro_poundage);*/
                        int hydro_reserviour = postSnapshot.child("Hydro Reserviour").getValue(Integer.class);
                        String hydro_reserviour1 = String.valueOf(postSnapshot.child("Hydro Reserviour").getKey());
                        /*nameArray.add(hydro_reserviour1);
                        valArray.add(hydro_reserviour);*/
                        int hydro_storage = postSnapshot.child("Hydro Storage").getValue(Integer.class);
                        String hydro_storage1 = String.valueOf(postSnapshot.child("Hydro Storage").getKey());
                        int sum_hydro_storage = hydro_poundage+hydro_reserviour+hydro_storage;
                        nameArray.add(hydro_storage1);
                        valArray.add(sum_hydro_storage);
                        /*int marine = postSnapshot.child("Marine").getValue(Integer.class);
                        String marine1 = String.valueOf(postSnapshot.child("Marine").getKey());
                        nameArray.add(marine1);
                        valArray.add(marine);*/
                        int nuclear = postSnapshot.child("Nuclear").getValue(Integer.class);
                        String nuclear1 = String.valueOf(postSnapshot.child("Nuclear").getKey());
                        nameArray.add(nuclear1);
                        valArray.add(nuclear);
                        int other = postSnapshot.child("Other").getValue(Integer.class);
                        String other1 = String.valueOf(postSnapshot.child("Other").getKey());
                        /*nameArray.add(other1);
                        valArray.add(other);*/
                        int other_renewable = postSnapshot.child("Other Renewable").getValue(Integer.class);
                        String other_renewable1 = String.valueOf(postSnapshot.child("Other Renewable").getKey());
                       /* nameArray.add(other_renewable1);
                        valArray.add(other_renewable);*/
                        int solar = postSnapshot.child("Solar").getValue(Integer.class);
                        String solar1 = String.valueOf(postSnapshot.child("Solar").getKey());
                        nameArray.add(solar1);
                        valArray.add(solar);
                        int wind_onshore = postSnapshot.child("Wind Onshore").getValue(Integer.class);
                        String wind_onshore1 = String.valueOf(postSnapshot.child("Wind Onshore").getKey());
                       /* nameArray.add(wind_onshore1);
                        valArray.add(wind_onshore);*/
                        int wind_offshore = postSnapshot.child("Wind offshore").getValue(Integer.class);
                        String wind_offshore1 = String.valueOf(postSnapshot.child("Wind offshore").getKey());
                        int sum_wind = wind_onshore+wind_offshore;
                        nameArray.add("Wind");
                        valArray.add(sum_wind);
                        int waste = postSnapshot.child("waste").getValue(Integer.class);
                        String waste1 = String.valueOf(postSnapshot.child("waste").getKey());
                        int sum_other = other+other_renewable+waste;
                        nameArray.add("Other");
                        valArray.add(sum_other);

                    }

                    electricityAdapter = new ElectricityAdapter(HomeActivity.this,electricityModelList, imageArray, efValues);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recycler_view.setLayoutManager(mLayoutManager);
                    recycler_view.setItemAnimator(new DefaultItemAnimator());
                    fetchist();
                }else {
                    Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        //
    }

    public void firebasedata345(){
        Query lastQuery = resorceref.orderByKey().limitToLast(1);

        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String periodStart11 = postSnapshot.child("periodStart").getValue(String.class);

                        String t1 = periodStart11.substring(0,4);
                        String t2 = periodStart11.substring(4,6);
                        String t3 = periodStart11.substring(6,8);
                        String t4 = periodStart11.substring(8,10);
                        String t5 = periodStart11.substring(10,12);

                        String timep1 = t1+"-"+t2+"-"+t3+"-"+t4+":"+t5;

                        try{
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                            Date df = sdf.parse(periodStart11);
                            current_timestamp = df.getTime();

                            Calendar c = Calendar.getInstance();
                            c.setTime(df);
                            c.add(Calendar.HOUR, -24);

                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyMMddHHmm");
                            SimpleDateFormat simpleDateFormat15 = new SimpleDateFormat("yyy-MM-dd-HH:mm");
                            str_currentDate2 = simpleDateFormat1.format(c.getTime());
                            String  str_currentDate23 = simpleDateFormat15.format(c.getTime());
                            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddhhmm");
                            Date d1f = sdf1.parse(str_currentDate2);
                            yesterday_timestamp = d1f.getTime();

                            try{
                                SimpleDateFormat sdfp = new SimpleDateFormat("yyyyMMddHHmm");
                                Date dfp = sdfp.parse(periodStart11);

                                Calendar cp = Calendar.getInstance();
                                cp.setTime(dfp);
                                cp.add(Calendar.HOUR, 1);

                                SimpleDateFormat simpleDateFormat1p = new SimpleDateFormat("yyyMMddHHmm");
                                String extrahr = simpleDateFormat1p.format(cp.getTime());
                                SimpleDateFormat sdf1p = new SimpleDateFormat("yyyyMMddhhmm");
                                Date d1fp = sdf1p.parse(extrahr);
                                long sony = d1fp.getTime();


                                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                cal.setTimeInMillis(sony);
                                String date = DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();

                                text_sync_time.setText(date);

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    firebasedata3();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    public void firebasedata3() {

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
                            barcount++;
                            String periodStart1 = postSnapshot.child("periodStart").getValue(String.class);
                            String timep = periodStart1.substring(8,10);
                            String timep11 = periodStart1.substring(10,12);
                            String cancot = timep+":"+timep11;
                            try{
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                                Date df = sdf.parse(periodStart11);

                                Calendar c = Calendar.getInstance();
                                c.setTime(df);
                                c.add(Calendar.HOUR, 1);

                                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyMMddHHmm");
                                String extrahr = simpleDateFormat1.format(c.getTime());
                                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddhhmm");
                                Date d1f = sdf1.parse(extrahr);
                                long sony = d1f.getTime();


                                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                cal.setTimeInMillis(sony);
                                String date = DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();

                                String currentString = date;
                                String[] separated = currentString.split(" ");
                                String par1 = separated[0]; // this will contain "Fruit"
                                String pr2 = separated[1]; // this will contain " they taste good"
                                Constants.timestr.add(pr2);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
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

