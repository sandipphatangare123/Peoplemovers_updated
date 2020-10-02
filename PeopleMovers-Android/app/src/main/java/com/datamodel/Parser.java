package com.datamodel;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.application.PeopleMovers;
import com.peoplemovers.app.R;
import com.util.TaskNotifier;
import com.util.Util;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class Parser {
    String TAG = "Parser";
    static ProgressDialog pDialog;
    static Context context;
    Map<String, String> params;
    String body = null;
    SharedPreferences sharedPreferences;
    public static final String COOKIE_KEY = "Cookie";


    // RequestQueue queue;

//10-02 00:27:12.141 26945-26945/com.peoplemovers.app E/Peoplemovers: __atuvc=2%7C39; __atuvs=59ce99dd80ccebe5001; PeopleMoversHomeFeed_1.25283=%7B%22view%22%3A2%7D; __cfduid=d7537e47eeafdc5fc9f4f9bf36cdc73c81506712020; __distillery=0aad758_40f6d43b-1e81-4e0e-aca1-9a5352b19dcc-2adad02a9-6bf9aefa25be-13e2; peopleMoversEntityTypeProduction=1; peopleMoversEntityTypeIdProduction=25283; peoplemoversci_session=a%3A8%3A%7Bs%3A10%3A%22session_id%22%3Bs%3A32%3A%22c024a0bbf8b1cb7f0ae477428e155f73%22%3Bs%3A10%3A%22ip_address%22%3Bs%3A30%3A%22116.74.177.100%2C+141.101.107.63%22%3Bs%3A10%3A%22user_agent%22%3Bs%3A120%3A%22Mozilla%2F5.0+%28Linux%3B+Android+5.1%3B+MotoE2+Build%2FLPCS23.13-56-5%3B+wv%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Version%2F4.0+Chr%22%3Bs%3A13%3A%22last_activity%22%3Bi%3A1506883981%3Bs%3A9%3A%22user_data%22%3Bs%3A0%3A%22%22%3Bs%3A3%3A%22uid%22%3Bs%3A5%3A%2225283%22%3Bs%3A10%3A%22entityType%22%3Bi%3A1%3Bs%3A12%3A%22entityTypeId%22%3Bs%3A5%3A%2225283%22%3B%7D2163efc74fc4a423a32e694c1e7a0e443eb08a2b; _ga=GA1.2.1621322536.1506712035; _gid=GA1.2.782164580.1506882517; _gat=1; __atuvc=14%7C39%2C7%7C40; __atuvs=59ce99dd80ccebe5007
//10-02 00:29:07.211 29821-29821/com.peoplemovers.app E/Peoplemovers: __cfduid=d45497258868a4b41570a0d0099a045a81506884338; peoplemoversci_session=a%3A5%3A%7Bs%3A10%3A%22session_id%22%3Bs%3A32%3A%22469c64f8b9a1e519bf8252184d3303a5%22%3Bs%3A10%3A%22ip_address%22%3Bs%3A30%3A%22116.74.177.100%2C+162.158.79.239%22%3Bs%3A10%3A%22user_agent%22%3Bs%3A120%3A%22Mozilla%2F5.0+%28Linux%3B+Android+5.1%3B+MotoE2+Build%2FLPCS23.13-56-5%3B+wv%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Version%2F4.0+Chr%22%3Bs%3A13%3A%22last_activity%22%3Bi%3A1506884339%3Bs%3A9%3A%22user_data%22%3Bs%3A0%3A%22%22%3B%7D9c897e0cf11d1f457bed83c44e6ce8176d5b3350
//10-02 00:30:34.961 29821-29821/com.peoplemovers.app E/Peoplemovers: __cfduid=d45497258868a4b41570a0d0099a045a81506884338; __distillery=0aad758_44035326-a3a1-4c86-9df0-796fcda9117c-063913dbb-fa3089cd508e-5721; peoplemoversci_session=a%3A8%3A%7Bs%3A10%3A%22session_id%22%3Bs%3A32%3A%22469c64f8b9a1e519bf8252184d3303a5%22%3Bs%3A10%3A%22ip_address%22%3Bs%3A30%3A%22116.74.177.100%2C+162.158.79.239%22%3Bs%3A10%3A%22user_agent%22%3Bs%3A120%3A%22Mozilla%2F5.0+%28Linux%3B+Android+5.1%3B+MotoE2+Build%2FLPCS23.13-56-5%3B+wv%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Version%2F4.0+Chr%22%3Bs%3A13%3A%22last_activity%22%3Bi%3A1506884339%3Bs%3A9%3A%22user_data%22%3Bs%3A0%3A%22%22%3Bs%3A3%3A%22uid%22%3Bs%3A5%3A%2225283%22%3Bs%3A10%3A%22entityType%22%3Bi%3A1%3Bs%3A12%3A%22entityTypeId%22%3Bs%3A5%3A%2225283%22%3B%7D5cfbb0650d1f5915c013cd3ea86046b1f0292a6e; peopleMoversEntityTypeProduction=1; peopleMoversEntityTypeIdProduction=25283; _ga=GA1.2.1097880686.1506884348; _gid=GA1.2.1505740503.1506884348; _gat=1; __atuvc=2%7C40; __atuvs=59d13af7c1983ba6001

    public static void showDialogue(Context context) {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.txt_loading) + "");
        pDialog.show();
    }

    public static void dissmissDialogue() {
        pDialog.dismiss();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void getJsonUsingStringRequest(Context context1, final TaskNotifier notifier, Map<String, String> params1, final String cookies) {
        context = context1;
        params = params1;
        String url = "https://stage.peoplemovers.com/mobile/setmobiledata".trim();
        //showDialogue(context);
        //queue = Volley.newRequestQueue(context);
       /* if(action.equalsIgnoreCase("AddClap")){
         url = "http:/".trim();
        } else {
            url = Util.Server_URL;

        }*/
        trustAllCertificates();


        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // jsonResponse=response;
                        // Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        notifier.onSuccess(response);
                        Log.e(TAG, response + "");
                        //              dissmissDialogue();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            //get status code here
                            //    String statusCode = String.valueOf(error.networkResponse.statusCode);
                            //get response body and parse with appropriate encoding
                            if (error.networkResponse.data != null) {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                    Log.e(TAG, body + "");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //do stuff with the body...
                            notifier.onError(body + "");
                            // Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                            //                dissmissDialogue();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            //              dissmissDialogue();
                        }
                    }
                })

        {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                //notifier.onError("No network , Server error");
                return super.parseNetworkError(volleyError);

            }

            /* (non-Javadoc)
     * @see com.android.volley.toolbox.StringRequest#parseNetworkResponse(com.android.volley.NetworkResponse)
     */
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                //  InspireIndia.get().checkSessionCookie(response.headers);
                //notifier.onError("No network , Server error");

                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json".trim());
                    headers.put("Cookie", cookies.trim());

                    Log.e("header---->", headers + "");
                }

                //  InspireIndia.get().addSessionCookie(headers);

                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Log.e("params---->", params + "");

                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PeopleMovers.getInstance().getRequestQueue().add(strRequest);

    }

    public void getJsonUsingJSONRequest(Context context1, final TaskNotifier notifier, Map<String, String> params1, final String cookies) {
        context = context1;
        params = params1;
        String url = "https://www.peoplemovers.com/mobile/setmobiledata".trim();
        sharedPreferences = context1.getSharedPreferences(context1.getString(R.string.app_name), Context.MODE_PRIVATE);

        //showDialogue(context);
        //queue = Volley.newRequestQueue(context);
       /* if(action.equalsIgnoreCase("AddClap")){
         url = "http:/".trim();
        } else {
            url = Util.Server_URL;

        }*/
        trustAllCertificates();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("deviceID", sharedPreferences.getString("deviceID", ""));
            Log.e(TAG, jsonBody + "");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("TAG", response.toString());
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", error.getMessage(), error);
                }


            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("Cookie", sharedPreferences.getString("cookies", ""));
                        params.put("Content-Type", "application/json");
                        params.put("Cookie", jsonBody + "");
                        Log.e(TAG, params + "");
                    } catch (Exception e) {
                    }
                    return params;
                }

            };


            PeopleMovers.getInstance().addToRequestQueue(jsonObjectRequest);

        } catch (Exception e) {
        }
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }

    public void getJsonUsingStringData(Context context1,
                                       final TaskNotifier notifier, String url) {
        context = context1;
        // showDialogue(context);
        trustAllCertificates();
        // OnItemScrollListener.firstVisibleItem=OnItemScrollListener.visibleItemCount;
        Log.e(Util.TAG, url + "");
        StringRequest strRequest = new StringRequest(Request.Method.GET, url.trim(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, response + "");
                        notifier.onSuccess(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { //get status code here
                        try {
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            //get response body and parse with appropriate encoding
                            if (error.networkResponse.data != null) {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                    Log.e(TAG, body + "");

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            //do stuff with the body...
                            notifier.onError(body + "");
                        } catch (Exception e) {
                            notifier.onError("No network , Server error");
                            //               dissmissDialogue();
                        }
                    }
                }) {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                //notifier.onError("No network , Server error");
                return super.parseNetworkError(volleyError);
            }


            /* (non-Javadoc)
     * @see com.android.volley.toolbox.StringRequest#parseNetworkResponse(com.android.volley.NetworkResponse)
     */
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                //  InspireIndia.get().checkSessionCookie(response.headers);
                notifier.onError("No network , Server error");

                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json/x-www-form-urlencoded; charset=utf-8");
                return headers;

                //  InspireIndia.get().addSessionCookie(headers);

            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PeopleMovers.getInstance().getRequestQueue().add(strRequest);
    }


    public void putJsonUsingStringRequest(Context context1,
                                          final TaskNotifier notifier, Map<String, String> params1, String action) {
        context = context1;
        params = params1;

        showDialogue(context);
        //queue = Volley.newRequestQueue(context);
        String url = Util.Server_URL + action;
        trustAllCertificates();


        StringRequest strRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // jsonResponse=response;
                        // Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, response + "");

                        notifier.onSuccess(response);
                        dissmissDialogue();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            //get status code here
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            //get response body and parse with appropriate encoding
                            if (error.networkResponse.data != null) {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                    Log.e(TAG, body + "");

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            dissmissDialogue();
                            //do stuff with the body...
                            notifier.onError("Network error");
                            // Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            notifier.onError(e + "");


                        }
                    }


                }) {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                //   notifier.onError("Network error");

                return super.parseNetworkError(volleyError);
            }

            /* (non-Javadoc)
     * @see com.android.volley.toolbox.StringRequest#parseNetworkResponse(com.android.volley.NetworkResponse)
     */
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                //  InspireIndia.get().checkSessionCookie(response.headers);
                //    notifier.onError("Network error");
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                //  InspireIndia.get().addSessionCookie(headers);

                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PeopleMovers.getInstance().getRequestQueue().add(strRequest);

    }

    public void getJsonUsingStringRequestUsingParams(Context context1,
                                                     final TaskNotifier notifier, Map<String, String> params1, final String action, String deviceId) {
        context = context1;
        params = params1;
        sharedPreferences = context1.getSharedPreferences(context1.getString(R.string.app_name), Context.MODE_PRIVATE);
        showDialogue(context);
        //queue = Volley.newRequestQueue(context);
//        String url = Util.Server_URL + "?deviceID=" + deviceId + "";
        String url = Util.Server_URL + "?deviceID=" + deviceId + "";

        Log.e("URL->", url);
        trustAllCertificates();


        StringRequest strRequest = new StringRequest(Request.Method.GET, url.trim(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // jsonResponse=response;
                        // Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, response + "");

                        notifier.onSuccess(response);
                        dissmissDialogue();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            //get status code here
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            //get response body and parse with appropriate encoding
                            if (error.networkResponse.data != null) {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                    Log.e(TAG, body + "");

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            //do stuff with the body...
                            notifier.onError(body + "");
                            // Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                            dissmissDialogue();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }


                }) {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }

            /* (non-Javadoc)
     * @see com.android.volley.toolbox.StringRequest#parseNetworkResponse(com.android.volley.NetworkResponse)
     */
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
            //    PeopleMovers.get().checkSessionCookie(response.headers);
               /* Map<String, String> responseHeaders = response.headers;
                String rawCookies = responseHeaders.get("set-Cookie");
                Log.e("cookies--->",rawCookies);*/
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }
      //   headers= PeopleMovers.get().addSessionCookie(headers);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put(COOKIE_KEY, sharedPreferences.getString(COOKIE_KEY,""));

                Log.e("header---->", headers + "");

                return headers;
            }

        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PeopleMovers.getInstance().getRequestQueue().add(strRequest);

    }
}