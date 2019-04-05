package com.adobe.vehicletelemetry.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bento on 01/04/17.
 */

public class VolleyHandler {

    public static final int GET = Request.Method.GET;
    public static final int PUT = Request.Method.PUT;
    public static final int POST = Request.Method.POST;

    private static final String TAG = VolleyHandler.class.getSimpleName();
    private static final int SOCKET_TIMEOUT_MS = 5000;
    private static VolleyHandler mInstance;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static NetworkListener mNetworkListener;

    public static synchronized VolleyHandler getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new VolleyHandler();
        }

        if (activity instanceof NetworkListener) {
            mNetworkListener = (NetworkListener) activity;
        }/* else {
            throw new RuntimeException(activity.toString()
                    + " must implement NetworkListener");
        }*/

        return mInstance;
    }

    public static synchronized VolleyHandler getInstance(Fragment fragment) {
        if (mInstance == null) {
            mInstance = new VolleyHandler();
        }

        if (fragment instanceof NetworkListener) {
            mNetworkListener = (NetworkListener) fragment;
        }/* else {
            throw new RuntimeException(activity.toString()
                    + " must implement NetworkListener");
        }*/

        return mInstance;
    }

    public void makeRequestArray(final String requestId, Context context, int method, String url, final JSONArray jaParams) {

        Log.d(TAG, "URL:" + url);

        if (!isOnline(requestId, context)) {
            return;
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                method,
                url,
                jaParams,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray jaResponse) {
                        Log.d(TAG, "Response: " + jaResponse);

                        if (mNetworkListener != null) {
                            mNetworkListener.onResponse(requestId, jaResponse);
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject joError = null;

                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                                joError = new JSONObject(res);
                                Log.d(TAG, "Error: " + joError);

                                if (mNetworkListener != null) {
                                    mNetworkListener.onError(requestId, joError);
                                }
                            } catch (UnsupportedEncodingException e1) {
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                });

        // Adding request to request queue
        addToRequestQueue(context, jsonArrayRequest, requestId);

        if (mNetworkListener != null) {
            mNetworkListener.onRequest(requestId);
        }
    }

    public void makeRequest(final String requestId, Context context, int method, String url, final JSONObject joParams) {

        Log.d(TAG, "URL:" + url);

        if (!isOnline(requestId, context)) {
            return;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                method,
                url,
                joParams,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject joResponse) {
                        Log.d(TAG, "Response: " + joResponse);

                        if (mNetworkListener != null) {
                            mNetworkListener.onResponse(requestId, joResponse);
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject joError = null;

                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                                joError = new JSONObject(res);
                                Log.d(TAG, "Error: " + joError);

                                if (mNetworkListener != null) {
                                    mNetworkListener.onError(requestId, joError);
                                }
                            } catch (UnsupportedEncodingException e1) {
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeader();
            }
        };

        // Adding request to request queue
        addToRequestQueue(context, jsonObjReq, requestId);

        if (mNetworkListener != null) {
            mNetworkListener.onRequest(requestId);
        }
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void loadImage(final String requestId, Context context, String imageUrl, final ImageView imageView) {

        if (!isOnline(requestId, context)) {
            return;
        }

        ImageLoader imageLoader = getImageLoader(context);
        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into image view
                    imageView.setImageBitmap(response.getBitmap());
                }
            }
        });
    }


    private <T> void addToRequestQueue(Context context, Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue(context).add(req);
    }

    private ImageLoader getImageLoader(Context context) {
        getRequestQueue(context);
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new BitmapCache());
        }
        return this.mImageLoader;
    }

    private RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }


    private HashMap<String, String> getHeader() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    private boolean isOnline(String requestId, Context context) {

        boolean isConnected = false;

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();

            if (!isConnected && mNetworkListener != null) {
                Log.d(TAG, "NO INTERNET CONNECTION!");
                mNetworkListener.onNoInternetConnection(requestId);
            }

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        return isConnected;
    }


    public interface NetworkListener {
        void onRequest(String requestId);

        void onResponse(String requestId, JSONObject joResponse);

        void onResponse(String requestId, JSONArray jaResponse);

        void onError(String requestId, JSONObject joError);

        void onNoInternetConnection(String requestId);
    }
}
