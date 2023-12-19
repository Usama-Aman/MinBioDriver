package com.vic.vicdriver.Network;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.ApplicationClass;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;
import com.vic.vicdriver.Views.Activities.LiveTrackingMaps;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.vic.vicdriver.Utils.ApplicationClass.channelId;

public class GPSService extends Service {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private int driverId = 0;
    private int userId = 0;
    private String userLat, userLng;
    private Intent resultPendingIntent;
    private Bundle bundle;
    private Location userLocation;
    private String orderNumber = "";

    @Override
    public void onCreate() {
        super.onCreate();

        locationListener = new MyLocationListener();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    stopSelf();
                }
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 5, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 5, locationListener);

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        driverId = intent.getIntExtra("driverId", 0);
//        userId = intent.getIntExtra("userId", 0);
//        userLat = intent.getStringExtra("lat");
//        userLng = intent.getStringExtra("lng");
//        orderNumber = intent.getStringExtra("order_number");

        driverId = Integer.parseInt(SharedPreference.getSimpleString(GPSService.this, Constants.DriverIdForNavigation));
        userId = Integer.parseInt(SharedPreference.getSimpleString(GPSService.this, Constants.UserIdForNavigation));
        userLat = (SharedPreference.getSimpleString(GPSService.this, Constants.UserLat));
        userLng = (SharedPreference.getSimpleString(GPSService.this, Constants.UserLng));
        orderNumber = (SharedPreference.getSimpleString(GPSService.this, Constants.OrderNumber));


        userLocation = new Location("");
        userLocation.setLatitude(Double.parseDouble(userLat));
        userLocation.setLongitude(Double.parseDouble(userLng));

        resultPendingIntent = new Intent(GPSService.this, LiveTrackingMaps.class);
        bundle = new Bundle();
        bundle.putString("lat", String.valueOf(userLat));
        bundle.putString("lng", String.valueOf(userLng));
        bundle.putInt("driverId", driverId);
        bundle.putInt("userId", userId);

        resultPendingIntent.putExtras(bundle);
//        PendingIntent pendingIntent = PendingIntent.getActivity(GPSService.this, 0, resultPendingIntent, 0);

        Notification notification = new NotificationCompat.Builder(GPSService.this, channelId)
                .setContentTitle(getResources().getString(R.string.minbio_running))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.noti)
                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);


        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);

    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

            bundle.putString("lat", String.valueOf(userLat));
            bundle.putString("lng", String.valueOf(userLng));
            bundle.putInt("driverId", driverId);
            bundle.putInt("userId", userId);


            double distanceInKM = location.distanceTo(userLocation) / 1000;
            Log.d("Distance", "onLocationChanged: " + String.valueOf(distanceInKM));

            if (distanceInKM <= Double.parseDouble(SharedPreference.getSimpleString(GPSService.this, Constants.delivery_arrival_notify_circle))) {
                if (!SharedPreference.getBoolean(GPSService.this, Constants.WithInRange)) {
                    callWithInRangeApi();
                    SharedPreference.saveBoolean(GPSService.this, Constants.WithInRange, true);
                }
            }

            resultPendingIntent.putExtras(bundle);
            if (driverId > 0 && userId > 0)
                ((ApplicationClass) getApplication()).emit(location.getLatitude(), location.getLongitude(), userId, driverId, "", false);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }


    private void callWithInRangeApi() {

        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(GPSService.this, Constants.accessToken);
        token = "Bearer " + token;

        Call<ResponseBody> call = api.withInRange(token, orderNumber);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        Log.d(TAG, "onResponse: " + jsonObject.toString());
                    } else {

                        jsonObject = new JSONObject(response.errorBody().string());
                        Log.d(TAG, "onResponse: " + jsonObject.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }
}
