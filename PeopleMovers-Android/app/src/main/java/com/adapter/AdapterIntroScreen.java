package com.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.PeopleMovers;
import com.model.ModelIntro;
import com.peoplemovers.app.R;

import java.util.ArrayList;

public class AdapterIntroScreen extends PagerAdapter {

    ArrayList<ModelIntro> listOfIntro;
    private LayoutInflater inflater;
    private Context context;

    public AdapterIntroScreen(Context context, ArrayList<ModelIntro> listOfIntro) {
        this.context = context;
        this.listOfIntro = listOfIntro;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listOfIntro.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View layout = inflater.inflate(R.layout.pager, view, false);
        ImageView imgIntro = (ImageView) layout
                .findViewById(R.id.image);
        imgIntro.setImageResource(listOfIntro.get(position).getImage());
        TextView txtDesc = (TextView) layout.findViewById(R.id.txt_pager);
        PeopleMovers.getInstance().setAveRoman(txtDesc);
        txtDesc.setText(Html.fromHtml(listOfIntro.get(position).getDesc(),0));
        view.addView(layout, 0);
        return layout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}