package com.thesis.carbon;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends PagerAdapter {

    private Activity activity;
    private Integer[] imagesArray;
    private ArrayList<Integer> namesArray;
    private String[] unitArray;

    public CustomAdapter(Activity activity,Integer[] imagesArray,ArrayList<Integer> namesArray,String[] unitArray){

        this.activity = activity;
        this.imagesArray = imagesArray;
        this.namesArray = namesArray;
        this.unitArray = unitArray;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = ((Activity)activity).getLayoutInflater();

        View viewItem = inflater.inflate(R.layout.item_view, container, false);
        ImageView imageView = (ImageView) viewItem.findViewById(R.id.imageView);
        imageView.setImageResource(imagesArray[position]);
        TextView textValue = (TextView) viewItem.findViewById(R.id.text_value);
        TextView textUnit = (TextView) viewItem.findViewById(R.id.text_unit);
        textValue.setText(String.valueOf(namesArray.get(position)+" "));
        textUnit.setText(unitArray[position]);
        ((ViewPager)container).addView(viewItem);

        return viewItem;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imagesArray.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // TODO Auto-generated method stub
        return view == ((View)object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        ((ViewPager) container).removeView((View) object);
    }
}

