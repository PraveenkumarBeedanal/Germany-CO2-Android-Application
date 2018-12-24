package com.thesis.carbon;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ElectricityAdapter extends RecyclerView.Adapter<ElectricityAdapter.MyViewHolder> {

    private List<ElectricityModel> electricityModelList;
    private ArrayList<Integer> ImageArray;
    private ArrayList<Integer> efValue;
    private Context context;
    private ArrayList<Integer> imageArray;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_resource, text_val;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            text_resource = (TextView) view.findViewById(R.id.text_resource);
            text_val = (TextView) view.findViewById(R.id.text_val);
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }


    public ElectricityAdapter(Context context,List<ElectricityModel> electricityModelList, ArrayList<Integer> imageArray, ArrayList<Integer> efvalue) {
        this.context = context;
        this.electricityModelList = electricityModelList;
        this.imageArray = imageArray;
        this.efValue = efvalue;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ElectricityModel electricityModel = electricityModelList.get(position);
        holder.text_resource.setText(electricityModel.getResource_name());
        holder.text_val.setText(electricityModel.getResource_val()+""+" MW"+" (EF - "+efValue.get(position)+")");

       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CarbonEmissionActivity.class);
                intent.putExtra("type", electricityModel.getResource_name());
                context.startActivity(intent);
            }
        });*/

        holder.imageView.setImageResource(imageArray.get(position));


    }

    @Override
    public int getItemCount() {
        return electricityModelList.size();
        //return 10;
    }
}
