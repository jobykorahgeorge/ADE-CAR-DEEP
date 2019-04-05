package com.adobe.vehicletelemetry;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * Created by TONY (tonysbento@gmail.com) on 03/07/17.
 */

public class Preferences {

    private static final String APP_PREFS = "INTELLI_SCAN_APP_PREFS";
    public static final String ACC_VALUE = "ACC_VALUE";
    public static final String ACC_DATA = "ACC_DATA";
    public static final String ACC_SECONDS = "ACC_SECONDS";


    private Context mContext;
    private SharedPreferences sharedPrefs;

    public Preferences(Context context) {
        mContext = context;
    }

    public void setData(String key, String value) {

        sharedPrefs = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        sharedPrefs.edit()
                .putString(key, value)
                .apply();
    }

    public String getData(String key) {

        sharedPrefs = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        String value = sharedPrefs.getString(key, null);

        return value;
    }

   /* public void setProductUrlSuffix(String color, String urlSuffix) {

        sharedPrefs = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        sharedPrefs.edit()
                .putString(KEY_PROD_URL + color, urlSuffix)
                .apply();
    }

    public String getProductUrlSuffix(String color) {

        sharedPrefs = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        String suffix = sharedPrefs.getString(KEY_PROD_URL + color, null);

        if(suffix == null || suffix.trim().isEmpty()){
            suffix = Constants.PRODUCT_URL_DEFAULT_MAP.get(color);
        }

        return suffix;
    }*/

    private String encrypt(String input) {
        if (input != null) {
            return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
        }

        return null;
    }

    private String decrypt(String input) {
        if (input != null) {
            return new String(Base64.decode(input, Base64.DEFAULT));
        }

        return null;
    }

}