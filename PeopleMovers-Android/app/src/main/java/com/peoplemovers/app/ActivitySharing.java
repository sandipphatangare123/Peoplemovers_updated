package com.peoplemovers.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.PeopleMovers;
import com.datamodel.Parser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pushnotification.MyFirebaseInstanceIDService;
import com.util.AdvancedWebView;
import com.util.TaskNotifier;
import com.util.Util;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivitySharing extends Activity implements AdvancedWebView.Listener, TaskNotifier {

    private static  String TEST_PAGE_URL = "https://stage.peoplemovers.com/extension?url=".trim();
    private AdvancedWebView mWebView;
    ProgressBar progressBar;
    TextView txtLoading;
    ImageView imgBackground;
    private boolean flagVisibility = true, isCounterRunning = false,isRunning=false;
    CountDownTimer countDownTimer;
    SharedPreferences sharedPreferences;
    private String refreshedToken;
   // SwipeRefreshLayout mySwipeRefreshLayout;
    SharedPreferences.Editor editor;
    Button btnClose;
    private String newsUrl="";
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);
        setUI();
        Intent intent1 = new Intent(this, MyFirebaseInstanceIDService.class);
        startService(intent1);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.e(Util.TAG, "In sharing activty---->");

      //  Log.e("Tocken---->", refreshedToken + "");
        if (!Util.isNetworkAvailable(this)) {
            Toast.makeText(ActivitySharing.this, "Please check internet connectivity", Toast.LENGTH_SHORT).show();
            loadOfflineView();
        } else {
            loadView();
        }

        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void handleMessage(Intent intent) {

        //onPageStarted("https://www.peoplemovers.com/extension?url=https://unsplash.com/",null);
        newsUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
     //   Log.e(Util.TAG,sharedText+"");

        if (newsUrl != null) /*{
            // Update UI to reflect text being shared
            if(sharedText.contains("https://youtu")){
                String []part=sharedText.split(".: ");
                if(part.length>1) {
                    mWebView.loadUrl("javascript:(function() { showsection($event, 'composeAttachOptsSection')})()");
                    mWebView.loadUrl("javascript:(function() { document.getElementById('cbTitle').value = '" + part[0] + "'; ;})()");
                    mWebView.loadUrl("javascript:(function() { document.getElementById('linksuggestion').value = '" + part[1] + "'; ;})()");
                    mWebView.loadUrl("javascript:(function() { document.getElementById('cbMessage').value = '" + sharedText + "'; ;})()");

                }
            } else  if(sharedText.contains("www")) {
                mWebView.loadUrl("javascript:(function() { document.getElementById('cbMessage').value = '" + sharedText + "'; ;})()");
            }else {
                mWebView.loadUrl("javascript:(function() { document.getElementById('cbMessage').value = '" + sharedText + "'; ;})()");
                mWebView.loadUrl("javascript:(function() { document.getElementById('cbTitle').value = '" + sharedText + "'; ;})()");
            }
        }*/{
            TEST_PAGE_URL=TEST_PAGE_URL+newsUrl;
            Log.e(Util.TAG,TEST_PAGE_URL);
            mWebView.loadUrl(TEST_PAGE_URL+"");
        }
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        // onPageStarted("https://www.peoplemovers.com/app/#/work\n",null);
    }
    @Override
    public void onError(String message) {

    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @Override
    public void onSuccess(String message) {
        editor.putString("flagUpdate", "true");
        editor.commit();
    }

    private void setUI() {
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove("cookies");
        editor.remove("userID");
        editor.putString("flagUpdate", "false");
        editor.commit();
        flagVisibility = true;
        imgBackground = (ImageView) findViewById(R.id.img_background);
        btnClose = (Button) findViewById(R.id.btn_close);
        btnClose.setVisibility(View.GONE);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ActivitySharing.this,ActivityHome.class);
                intent.putExtra("data","true");
                startActivity(intent);
                ActivitySharing.this.finish();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_home);
        txtLoading = (TextView) findViewById(R.id.txt_sync_appdata);
        PeopleMovers.getInstance().setAveHeavy(txtLoading);
        mWebView = (AdvancedWebView) findViewById(R.id.webview);
       /* mySwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout.setRefreshing(false);
                        mWebView.reload();
                    }
                }
        );*/
    }


    @Override
    protected void onStart() {
        super.onStart();
       /* mySwipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener =
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (mWebView.getScrollY() == 0)
                            mySwipeRefreshLayout.setEnabled(true);
                        else
                            mySwipeRefreshLayout.setEnabled(false);

                    }
                });*/
    }

    @Override
    protected void onStop() {
        //mySwipeRefreshLayout.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        super.onStop();
    }

    private void loadView() {
        mWebView.setListener(this, this);
        mWebView.setGeolocationEnabled(false);
        mWebView.setMixedContentAllowed(true);
        mWebView.setCookiesEnabled(true);
        mWebView.setThirdPartyCookiesEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // Toast.makeText(ActivityHome.this, title, Toast.LENGTH_SHORT).show();
            }

        });
        mWebView.addHttpHeader("X-Requested-With", "");
        handleMessage(intent);
      //  mWebView.loadUrl(TEST_PAGE_URL);
      /*  countDownTimer = new CountDownTimer(3000, 500) {

            public void onTick(long millisUntilFinished) {
                Log.e("seconds rem", "" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
                if (flagVisibility) {
                    oldURL = newURL;
                    flagVisibility = false;
                } else {
                    if (!oldURL.trim().equalsIgnoreCase(newURL.trim())) {
                        Log.e("old url-->", oldURL + "");
                        Log.e("new url-->", newURL + "");
                        oldURL = newURL;
                        //  countDownTimer.cancel();
                    } else {
                        // return;
                    }
                }
            }

            public void onFinish() {
                Log.e("seconds rem", "finished");
                progressBar.setVisibility(View.GONE);
                imgBackground.setVisibility(View.GONE);
            }
        };*/
    }

    private void loadOfflineView() {
        mWebView.setListener(this, this);
        mWebView.setGeolocationEnabled(false);
        mWebView.setMixedContentAllowed(true);
        mWebView.setCookiesEnabled(true);
        mWebView.setThirdPartyCookiesEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
       /* mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {

            }

            public void onPageFinished(WebView view, String url) {
                Log.e("url-->", url);
                // if (mWebView.getProgress() == 100) {


               if (mWebView.getProgress() == 100) {
                    progressBar.setVisibility(View.GONE);
                    imgBackground.setVisibility(View.GONE);
                    //Toast.makeText(ActivityHome.this, "Finished loading", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        mWebView.addHttpHeader("X-Requested-With", "");
        mWebView.loadUrl(TEST_PAGE_URL);
    }

    public String getCookie(String siteName, String CookieName) {
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if (cookies != null) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains(CookieName)) {
                    String[] temp1 = ar1.split("=");
                    CookieValue = temp1[1];
                }
            }
        }
        return CookieValue;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ActivitySharing.this,ActivityHome.class);
        startActivity(intent);
        ActivitySharing.this.finish();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        progressBar.setVisibility(View.VISIBLE);
        txtLoading.setVisibility(View.GONE);
    }

    @Override
    public void onPageFinished(String url) {
        progressBar.setVisibility(View.GONE);
        btnClose.setVisibility(View.VISIBLE);
        imgBackground.setVisibility(View.GONE);
        txtLoading.setVisibility(View.GONE);
        /*try {
            mWebView.scrollTo(0, 0);
            progressBar.setVisibility(View.GONE);
            btnClose.setVisibility(View.VISIBLE);
            imgBackground.setVisibility(View.GONE);
            mWebView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
            mWebView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
            mWebView.getSettings().setAllowFileAccess(true);
            mWebView.getSettings().setAppCacheEnabled(true);
            mWebView.getSettings().setJavaScriptEnabled(true);
            Log.e(Util.TAG, "URL-->" + url);
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            handleMessage(intent);

        } catch (Exception exception) {
            exception.printStackTrace();
        }*/
    }

    private String getUserId(String url) {
        String userID = "";
        try {
            String input = java.net.URLDecoder.decode(getCookie(url, "user_data"), "UTF-8");
            Log.e("before replacement ->", input.toString() + " ");
            String pattern1 = ";i:";
            String pattern2 = "((;?[s]{1,}:[0-9]{1,}).)";

            Pattern patternt = Pattern.compile(pattern1);
            Matcher matchert = patternt.matcher(input);

            List<Integer> indexList = new ArrayList<Integer>();while (matchert.find()) {
                indexList.add(matchert.start());
                input = input.replaceFirst(matchert.group(), ":");
                System.out.println(" starting at index " + matchert.start());
            }

            Pattern pattern = Pattern.compile(pattern2);
            Matcher matcher = pattern.matcher(input);
            int index = 0;
            int prevEnd = 0;
            while (matcher.find()) {
                int nextStart = matcher.start();
                for (Integer startIndex : indexList) {
                    if (prevEnd < startIndex && startIndex <= nextStart) {
                        index++;
                    }
                }
                if (index == 0) {
                    input = input.replaceFirst(matcher.group(), "");
                } else if (index % 2 == 0) {
                    input = input.replaceFirst(matcher.group(), ",");
                } else {
                    input = input.replaceFirst(matcher.group(), ":");
                }

                System.out.println("find() found the pattern " + matcher.group()
                        + " starting at index " + matcher.start()
                        + " and ending at index " + matcher.end());
                System.out.println("After replacement == " + input);
                prevEnd = matcher.end();
                index++;
            }


            input = input.replace(";}", "}");
            Log.e("Cookies->", input.toString() + " ");
            input = input.substring(input.indexOf("{"), input.indexOf("}") + 1);
            Log.e("After replacement ->", input.toString() + " ");

            System.out.println("After replacement == " + input);
            JSONObject jsonObject = new JSONObject(input);

            if (!jsonObject.isNull("uid"))
                userID = jsonObject.getString("uid");
            else
                userID = "";
            Log.e("userID->", userID.toString() + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userID;
    }

    public String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        //   Toast.makeText(ActivitySharing.this, "onPageError(errorCode = " + errorCode + ",  description = " + description + ",  failingUrl = " + failingUrl + ")", Toast.LENGTH_SHORT).show();
    }

    public static String getCookieFromAppCookieManager(String url) throws MalformedURLException {

        CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager == null)
            return null;
        String rawCookieHeader = null;
        URL parsedURL = new URL(url);
        rawCookieHeader = cookieManager.getCookie(parsedURL.getHost());
        if (rawCookieHeader == null)
            return null;
        return rawCookieHeader;
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        //  Toast.makeText(ActivitySharing.this, "onDownloadRequested(url = " + url + ",  suggestedFilename = " + suggestedFilename + ",  mimeType = " + mimeType + ",  contentLength = " + contentLength + ",  contentDisposition = " + contentDisposition + ",  userAgent = " + userAgent + ")", Toast.LENGTH_LONG).show();

		/*if (AdvancedWebView.handleDownload(this, url, suggestedFilename)) {
            // download successfully handled
		}
		else {
			// download couldn't be handled because user has disabled download manager app on the device
		}*/
    }

    @Override
    public void onExternalPageRequest(String url) {
        //   Toast.makeText(ActivitySharing.this, "onExternalPageRequest(url = " + url + ")", Toast.LENGTH_SHORT).show();
    }

    BasicClientCookie parseRawCookie(String rawCookie) throws Exception {
        String[] rawCookieParams = rawCookie.split(";");

        for (int i = 0; i < rawCookieParams.length; i++)
            Log.e("Cookie value", rawCookieParams[i] + "");

        String[] rawCookieNameAndValue = rawCookieParams[0].split("=");
        for (int i = 0; i < rawCookieNameAndValue.length; i++)
            Log.e("Cookie value", rawCookieNameAndValue[i] + "");

        if (rawCookieNameAndValue.length != 2) {
            throw new Exception("Invalid cookie: missing name and value.");
        }

        String cookieName = rawCookieNameAndValue[0].trim();
        String cookieValue = rawCookieNameAndValue[1].trim();
        Log.e("Cookie value", cookieName + " " + cookieValue);
        BasicClientCookie cookie = new BasicClientCookie(cookieName, cookieValue);
        for (int i = 1; i < rawCookieParams.length; i++) {
            String rawCookieParamNameAndValue[] = rawCookieParams[i].trim().split("=");
            String paramName = rawCookieParamNameAndValue[0].trim();
        }

        return cookie;
    }

    public class OkHttpHandler extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRunning=true;
        }

        @Override
        protected String doInBackground(String... params) {
            if (refreshedToken == null) {
                Intent intent1 = new Intent(ActivitySharing.this, MyFirebaseInstanceIDService.class);
                startService(intent1);
                refreshedToken = FirebaseInstanceId.getInstance().getToken();
            }
            String cookie = sharedPreferences.getString("cookies", "");
            String userId = sharedPreferences.getString("userID", "");
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Util.Server_URL).newBuilder();
            urlBuilder.addQueryParameter("deviceID", refreshedToken + "");
            urlBuilder.addQueryParameter("userID", userId + "");
            urlBuilder.addQueryParameter("key", "0dcfda7d-1cbf-4067-befc-599aa77f6ab4-6433c894-a377-4e45-86f0-11539d148c15");
//https://www.peoplemovers.com/mobile/setmobiledata?agent=curl&deviceID=testDeviceToken&userID=25283&app_auth_key=0dcfda7d-1cbf-4067-befc-599aa77f6ab4-6433c894-a377-4e45-86f0-11539d148c15

            String url = urlBuilder.build().toString();
            Log.e(Util.TAG, url);
            Log.e(Util.TAG, cookie);
            Log.e(Util.TAG, refreshedToken + "");
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    //  .addHeader("Content-Type", "application/json")
                    //.addHeader("Accept", "application/json")
//                    .addHeader("Cookie", "Cookie="+cookie)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("Resonse-->", s+"");
            if(s!=null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!jsonObject.isNull("status")) {
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            editor.putString("flagUpdate", "true");
                            editor.commit();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isRunning=false;
            }
        }
    }
}
