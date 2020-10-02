package com.peoplemovers.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adapter.AdapterIntroScreen;
import com.application.PeopleMovers;
import com.model.ModelIntro;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;


import java.util.ArrayList;


public class ActiivityIntrouductionScreen extends Activity implements View.OnClickListener {
    //Declration part
    private static ViewPager mPager;
    private static int currentPage = 0;
    private Integer[] intro_screen = {R.drawable.pm_intro_pic_1, R.drawable.pm_intro_pic_2,
            R.drawable.pm_intro_pic_3, R.drawable.pm_intro_pic_4, R.drawable.pm_intro_pic_5, R.drawable.pm_intro_pic_6};
    private String[] intro_text = {"PeopleMovers® is the place to build strong communities\n" +
            "and a better world.",
            "Get connected to leaders\n" +
                    "and organizations based on\n" +
                    "your needs and the issues \n" +
                    "you are focused on. ",
            "Follow what’s happening\n" +
                    "and share your news, needs,\n" +
                    " and events with easy clicks.",
            "Work together in groups \n" +
                    "with easy tools \n" +
                    "for collaboration. ",
            "Earn PeoplePoints® as you engage to redeem for tickets, food, services and other cool stuff!",
            "The more communities share and work together the stronger they become."};
    private TextView btnNext, btnBack,txtPagerLetsMove;
    //SliderLayout sliderLayout;
    private SharedPreferences sharedPreferences;
    public ArrayList<ModelIntro> listOfIntro = new ArrayList<>();
    private int pagerPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivity_introuduction_screen);
        setData();
        init();
    }

    /*Set data for pager*/
    private void setData() {
        for (int i = 0; i < intro_screen.length; i++) {
            ModelIntro modelIntro = new ModelIntro();
            modelIntro.setImage(intro_screen[i]);
            modelIntro.setDesc(intro_text[i]);
            listOfIntro.add(modelIntro);
        }
    }

    /*on click hndle*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (pagerPosition != 0) {
                    pagerPosition-=1;
                    mPager.setCurrentItem(pagerPosition);
                }
                break;
            case R.id.btn_next:
                if (pagerPosition != (listOfIntro.size() - 1)) {
                    pagerPosition+=1;
                    mPager.setCurrentItem(pagerPosition);
                } else if (pagerPosition == (listOfIntro.size() - 1)) {
                    Intent intent = new Intent(ActiivityIntrouductionScreen.this, ActivityHome.class);
                    intent.putExtra("data", "false");
                    startActivity(intent);
                    sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isVisited", true);
                    editor.commit();
                    ActiivityIntrouductionScreen.this.finish();
                }
                break;
        }
    }

    private void init() {
        btnNext = (TextView) findViewById(R.id.btn_next);
        btnBack = (TextView) findViewById(R.id.btn_back);
        txtPagerLetsMove = (TextView) findViewById(R.id.txt_lets_moving);
        txtPagerLetsMove.setVisibility(View.GONE);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        PeopleMovers.getInstance().setAveBlack(btnBack);
        PeopleMovers.getInstance().setAveBlack(btnBack);
        PeopleMovers.getInstance().setAveHeavy(txtPagerLetsMove);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new AdapterIntroScreen(ActiivityIntrouductionScreen.this, listOfIntro));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("posi-->", position + "");
                pagerPosition=position;
                if (position == listOfIntro.size() - 1) {
                    btnNext.setVisibility(View.VISIBLE);
                    btnNext.setText(getResources().getText(R.string.btn_got_it));
                    txtPagerLetsMove.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    txtPagerLetsMove.setVisibility(View.GONE);
                    btnNext.setText(getResources().getText(R.string.btn_next));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        DotsIndicator indicator = (DotsIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
    }
}
