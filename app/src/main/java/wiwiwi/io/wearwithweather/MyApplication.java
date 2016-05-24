package wiwiwi.io.wearwithweather;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MyApplication extends Application {
    public static final String PREF_FILE_NAME = "WWW_SHARED_PREFERENCES";
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getInstance(){
        return  sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static void saveToPreferences(Context context, String preferenceName, int preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(preferenceName, preferenceValue);
        editor.apply();
    }

    public static void saveToPreferences(Context context, String preferenceName, boolean preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getString(preferenceName, defaultValue);
    }
    public static int readFromPreferences(Context context, String preferenceName, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getInt(preferenceName, defaultValue);
    }
    public static boolean readFromPreferences(Context context, String preferenceName, boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(preferenceName, defaultValue);
    }

    public static void createBundleSendingEmail(String email ,Intent intent){
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        intent.putExtras(bundle);
    }



    /*service tags */
    private static final String Login_Service = "http://wiwiwi.somee.com/wiConsole.asmx/wiLogin";
    private static final String Register_Service = "http://wiwiwi.somee.com/wiConsole.asmx/wiRegister";
    private static final String User_Details_Service = "http://wiwiwi.somee.com/wiConsole.asmx/wiGetUserDetails";
    private static final String Get_Clothes_Service = "http://wiwiwi.somee.com/wiConsole.asmx/wiGetClothes";


    public static String getLogin_Service_Tag() {
        return Login_Service;
    }

    public static String getGet_Clothes_Service1() {
        return Get_Clothes_Service;
    }


    public static String getRegister_Service_Tag() {
        return Register_Service;
    }

    public static String getUser_Details_Service_Tag() {
        return User_Details_Service;
    }
}
