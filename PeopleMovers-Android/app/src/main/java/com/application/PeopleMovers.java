package com.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.peoplemovers.app.R;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Sandip Phatangare.
 */
public class PeopleMovers extends Application {
    public static final String TAG = PeopleMovers.class
            .getSimpleName();

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "sessionid";

    private Typeface typeFace;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static PeopleMovers mInstance;
    private SharedPreferences _preferences;

    public static PeopleMovers get() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        _preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

    }

    public static synchronized PeopleMovers getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public void setAveRoman(TextView textView) {
        AssetManager am = getApplicationContext().getAssets();
        Typeface typeFaceRegular = Typeface.createFromAsset(am, "fonts/avenir_roman.otf");
        textView.setTypeface(typeFaceRegular);
    }

    public void setAveBlack(TextView textView) {
        AssetManager am = getApplicationContext().getAssets();
        Typeface typeFaceRegular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "avenir_black.ttf"));
        textView.setTypeface(typeFaceRegular);
    }

    public void setAveHeavy(TextView textView) {
        AssetManager am = getApplicationContext().getAssets();
        Typeface typeFaceRegular = Typeface.createFromAsset(getAssets(), "fonts/avenir_heavy.ttf");
        textView.setTypeface(typeFaceRegular);
    }

    public void setLatoBoldItalic(TextView textView) {
        AssetManager am = getApplicationContext().getAssets();
        Typeface typeFaceRegular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "Lato-BoldItalic.ttf"));
        textView.setTypeface(typeFaceRegular);
    }

    public void setLatoSemiBoldItalic(TextView textView) {
        AssetManager am = getApplicationContext().getAssets();
        Typeface typeFaceRegular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "Lato-SemiboldItalic.ttf"));
        textView.setTypeface(typeFaceRegular);
    }

    public void setLatoItalic(TextView textView) {
        AssetManager am = getApplicationContext().getAssets();
        Typeface typeFaceRegular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "Lato-Italic.ttf"));
        textView.setTypeface(typeFaceRegular);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    /**
     * Checks the response headers for session cookie and saves it
     * if it finds it.
     * @param headers Response Headers.
     */
    public final void checkSessionCookie(Map<String, String> headers) {
        if (headers.containsKey(SET_COOKIE_KEY)
                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
            String cookie = headers.get(SET_COOKIE_KEY);
            if (cookie.length() > 0) {
               String[] splitCookie = cookie.split(";");
                String[] splitSessionId = splitCookie[0].split("=");
                cookie = splitSessionId[1];
                SharedPreferences.Editor prefEditor = _preferences.edit();
                prefEditor.putString(SESSION_COOKIE, cookie);
                prefEditor.commit();
            }
        }
    }

    /**
     * Adds session cookie to headers if exists.
     * @param headers
     */
    public final Map<String, String>  addSessionCookie(Map<String, String> headers) {
        String sessionId = _preferences.getString(SESSION_COOKIE, "");
        if (sessionId.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(SESSION_COOKIE);
            builder.append("=");
            builder.append(sessionId);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }
        return headers;
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}
