package com.vic.vicdriver.Utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.vic.vicdriver.Controllers.Interfaces.SocketCallback;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Locale;

@SuppressLint("Registered")
public class ApplicationClass extends Application {

    //  private String socketUrl = "http://elxdrive.com:4000";  // Office server
    public String socketUrl = "http://vicwsp.com:3000/"; //Live Server

    public static final String channelId = "com.elementarylogics.minbio_driver";
    private SocketCallback socketCallback;
    private String TAG = "socketCallBack";
    private Locale locale;
    private Context context;
    public static ApplicationClass applicationClassInstance;
    public Socket mSocket;
    private String sendLocationEmit = "send_location";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        applicationClassInstance = this;

        createNotificationChannel();
    }

    public void initializeSocket(int userId) {
        try {

            mSocket = IO.socket(socketUrl);
//            IO.Options opts = new IO.Options();
//            Data data = Common.fromJson(getApplicationContext());
//            opts.query = "type=" + "driver &id=" + data.getId();
//
//            mSocket = IO.socket(/*socketUrl*/ "http://10.20.30.192:3000/", opts);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.connect();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "LocationService", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void sendConnection(int userId) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", userId);
            jsonObject.put("user_type", "drivers");
            mSocket.emit("connect_user", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initializeCallback(SocketCallback socketCallback) {
        this.socketCallback = socketCallback;
        listeningForOns();
    }

    public void emit(double lat, double lng, int userId, int driverId, String result, boolean onPath) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);
            jsonObject.put("driver_id", driverId);
            jsonObject.put("latitude", lat);
            jsonObject.put("longitude", lng);
            jsonObject.put("path", result);
            jsonObject.put("onPath", onPath);
            mSocket.emit(sendLocationEmit, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void listeningForOns() {
        mSocket.on("update_location", args -> {
            try {
                JSONObject jsonObject = (JSONObject) args[0];

                socketCallback.onSuccess(jsonObject, "update_location");

            } catch (Exception e) {
                socketCallback.onFailure(e.getLocalizedMessage(), "update_location");
            }
        });
    }


    public void changeLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();
        String lang = SharedPreference.getSimpleString(context, Constants.language);

        if (!(config.locale.getLanguage().equals(lang))) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            context.getResources().updateConfiguration(newConfig, context.getResources().getDisplayMetrics());
        }
    }


}
