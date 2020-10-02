package com.peoplemovers.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.util.AdvancedWebView;

public class TempActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        AdvancedWebView mWebView=(AdvancedWebView)findViewById(R.id.web_temp);
        mWebView.setGeolocationEnabled(false);
        mWebView.setMixedContentAllowed(true);
        mWebView.setCookiesEnabled(true);
        mWebView.setThirdPartyCookiesEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default
        mWebView.loadUrl("https://stage.peoplemovers.com/extension?url=https://news.google.com/articles/CAIiEKiEmAyJ9bh7w78RbgJrcbIqGQgEKhAIACoHCAowiYr-CjD1iIoDMMT73AU?hl=en-IN&gl=IN&ceid=IN%3Aen");
    }
}
