package com.vic.vicdriver.Views.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Network.GPSService;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.ApplicationClass;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicdriver.Utils.Common.showToast;
import static com.vic.vicdriver.Utils.Constants.MY_PERMISSIONS_REQUEST_LOCATION;

public class LiveTrackingMaps extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, TextToSpeech.OnInitListener, SensorEventListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap;
    private static final String TAG = "LiveTrackingMaps";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private View mapView;
    private LatLng origin, destination;
    private Marker driverMarker, userMarker;
    private Location currLocation;
    private Polyline polyline;
    private RelativeLayout imgBack;
    private int driverId = 0, userId = 0;
    private Intent serviceIntent;
    private List<LatLng> pointsArrayList = new ArrayList<>();
    private String userLat, userLng;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location userLocation;
    private double distanceInKM = 1000000;
    private String orderNumber = "";
    private boolean isMarkerRotating = false;
    private Button btnStartNavigation;//, btnDirections;
    private LatLng previousLatLng = new LatLng(0.0, 0.0);
    private TextToSpeech tts;
    private String audioInstructions = "";
    private String maneuver = "";
    LatLng latLng1;
    private float v;
    private float mDeclination;
    private boolean firstTime = false;
    private float[] mRotationMatrix = new float[16];
    private JSONObject routeJson = null;
    boolean onPath = true;
    private String spokenMessage = "";
    private boolean isEnableDirection = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracking);
        tts = new TextToSpeech(LiveTrackingMaps.this, this);

        stopService(new Intent(LiveTrackingMaps.this, GPSService.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        Bundle bundle = getIntent().getExtras();
        orderNumber = bundle.getString("order_number");
        userLat = bundle.getString("lat");
        userLng = bundle.getString("lng");
        driverId = bundle.getInt("driverId");
        userId = bundle.getInt("userId");
        isEnableDirection = bundle.getBoolean("isEnableDirection");

        userLocation = new Location("");
        userLocation.setLatitude(Double.parseDouble(userLat));
        userLocation.setLongitude(Double.parseDouble(userLng));

        if (!userLng.isEmpty() && !userLat.isEmpty()) {
            destination = new LatLng(Double.parseDouble(userLat), Double.parseDouble(userLng));
        } else
            finish();


        locationListener = new MyLocationListener();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        initialize();

    }

    private void initialize() {
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(view -> finish());
        btnStartNavigation = findViewById(R.id.startNavigation);
        btnStartNavigation.setOnClickListener(this);
//        btnDirections = findViewById(R.id.directions);
//        btnDirections.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.liveMap);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mapView = mapFragment.getView();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap == null) {
            return;
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(LiveTrackingMaps.this, R.raw.maps_style));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                origin = latLng;


                calculatingPath(latLng);

            }
        });

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d(TAG, "OnConnected: " + ((ApplicationClass) getApplication()).mSocket.connected());

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);

                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (location != null) {
                        origin = new LatLng(location.getLatitude(), location.getLongitude());
                        showMarkersOnMap();
                    }


                    if (isEnableDirection) {
                        btnStartNavigation.setVisibility(View.VISIBLE);
//                        btnDirections.setVisibility(View.VISIBLE);

                        if (SharedPreference.getSimpleString(LiveTrackingMaps.this, Constants.navigatedOrderNumber).equals(orderNumber)) {
                            if (SharedPreference.getBoolean(LiveTrackingMaps.this, Constants.isNavigationStarted)) {
                                btnStartNavigation.setVisibility(View.GONE);
                                btnStartNavigation.setEnabled(false);
                                startNavigationMethod();
                            }
                        }

                    } else {
                        btnStartNavigation.setVisibility(View.GONE);
//                        btnDirections.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showMarkersOnMap() {
        mMap.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(origin);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        driverMarker = mMap.addMarker(markerOptions);

        markerOptions = new MarkerOptions();
        markerOptions.position(destination);
        markerOptions.draggable(false);
        userMarker = mMap.addMarker(markerOptions);

        viewBoundTheMap();

        String url = getDirectionsUrl(origin, destination);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private void viewBoundTheMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(userMarker.getPosition());
        builder.include(driverMarker.getPosition());

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.40);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.moveCamera(cu);
        mMap.animateCamera(cu);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: " + String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();

        if (SharedPreference.getBoolean(LiveTrackingMaps.this, "isAccepted")) {
            if (!SharedPreference.getSimpleString(this, Constants.DriverIdForNavigation).equals("") && !SharedPreference.getSimpleString(this, Constants.UserIdForNavigation).equals("")
                    && !SharedPreference.getSimpleString(this, Constants.UserLat).equals("") && !SharedPreference.getSimpleString(this, Constants.UserLng).equals("")
                    && !SharedPreference.getSimpleString(this, Constants.OrderNumber).equals("")) {
                serviceIntent = new Intent(LiveTrackingMaps.this, GPSService.class);
                startService(serviceIntent);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected()) {
                locationManager.removeUpdates(locationListener);
                mGoogleApiClient.disconnect();
            }

        if (tts != null) {

            tts.stop();
            tts.shutdown();
            Log.d(TAG, "TTS Destroyed");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SharedPreference.getBoolean(LiveTrackingMaps.this, "isAccepted")) {
            if (!SharedPreference.getSimpleString(this, Constants.DriverIdForNavigation).equals("") && !SharedPreference.getSimpleString(this, Constants.UserIdForNavigation).equals("")
                    && !SharedPreference.getSimpleString(this, Constants.UserLat).equals("") && !SharedPreference.getSimpleString(this, Constants.UserLng).equals("")
                    && !SharedPreference.getSimpleString(this, Constants.OrderNumber).equals("")) {
                serviceIntent = new Intent(LiveTrackingMaps.this, GPSService.class);
                startService(serviceIntent);
            }
        }


        if (tts != null) {

            tts.stop();
            tts.shutdown();
            Log.d(TAG, "TTS Destroyed");
        }
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (location != null) {
                        origin = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                    showMarkersOnMap();
                }
            } else {
                showToast(LiveTrackingMaps.this, getResources().getString(R.string.permission_denied), false);
            }
            return;
        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getResources().getString(R.string.google_maps_key)
                + "&mode=" + "DRIVING";


        return url;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startNavigation) {
//            if (!SharedPreference.getBoolean(this, Constants.isAnotherOrder))
                startNavigationMethod();
//            else
//                showToast(this, "Another order is in progress. ", false);
        } else if (v.getId() == R.id.directions) {

            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + userLat + "," + userLng);
            Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);

        }
    }

    private void startNavigationMethod() {

        SharedPreference.saveSimpleString(LiveTrackingMaps.this, Constants.OrderNumber, orderNumber);
        SharedPreference.saveSimpleString(LiveTrackingMaps.this, Constants.UserLat, userLat);
        SharedPreference.saveSimpleString(LiveTrackingMaps.this, Constants.UserLng, userLng);
        SharedPreference.saveSimpleString(LiveTrackingMaps.this, Constants.UserIdForNavigation, String.valueOf(userId));
        SharedPreference.saveSimpleString(LiveTrackingMaps.this, Constants.DriverIdForNavigation, String.valueOf(driverId));

        SharedPreference.saveBoolean(LiveTrackingMaps.this, Constants.isAnotherOrder, true);
        SharedPreference.saveBoolean(LiveTrackingMaps.this, Constants.isNavigationStarted, true);
        SharedPreference.saveSimpleString(LiveTrackingMaps.this, Constants.navigatedOrderNumber, orderNumber);

        btnStartNavigation.setVisibility(View.GONE);
//        btnDirections.setVisibility(View.VISIBLE);


        Location location = new Location("");
        location.setLatitude(origin.latitude);
        location.setLongitude(origin.longitude);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(19)                   // Sets the zoom
                .bearing(60)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(LiveTrackingMaps.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mMap.setMyLocationEnabled(true);

                            if (locationManager != null) {
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        handler.postDelayed(runnable, 2000);
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        // Executes in UI thread, after the execution of
        // doInBackground()

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "onMarkerDrag: " + "Downloaded");
            try {

                routeJson = new JSONObject(result);

//                gettingAudioFromRoute(routeJson);

                String encodedString = routeJson.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");

                if (driverId > 0 && userId > 0) {
                    ((ApplicationClass) getApplication()).emit(origin.latitude, origin.longitude, userId, driverId, encodedString, false);
                }

                pointsArrayList.clear();
                pointsArrayList = PolyUtil.decode(encodedString);
                makePolyLine(pointsArrayList, encodedString);


            } catch (Exception e) {
                Toast.makeText(LiveTrackingMaps.this, "Exception" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void makePolyLine(List<LatLng> pointsArrayList, String encodedString) {
        try {


            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.addAll(pointsArrayList);
            lineOptions.width(8);
            lineOptions.color(getResources().getColor(R.color.splashColor));

            if (polyline != null)
                polyline.remove();

            // Drawing polyline in the Google Map for the i-th route
            polyline = mMap.addPolyline(lineOptions);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Error Downloading URL", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            double angle = 0.0;
            if (Math.abs(Math.toDegrees(orientation[0]) - angle) > 0.8) {
                float bearing = (float) Math.toDegrees(orientation[0]) + mDeclination;
                updateCamera(bearing);
            }
            angle = Math.toDegrees(orientation[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            origin = new LatLng(location.getLatitude(), location.getLongitude());
            driverMarker.setPosition(origin);

            double distanceInKM = location.distanceTo(userLocation) / 1000;
            if (distanceInKM <= Double.parseDouble(SharedPreference.getSimpleString(LiveTrackingMaps.this, Constants.delivery_arrival_notify_circle))) {
                if (!SharedPreference.getBoolean(LiveTrackingMaps.this, Constants.WithInRange)) {
                    callWithInRangeApi();
                    SharedPreference.saveBoolean(LiveTrackingMaps.this, Constants.WithInRange, true);
                }
            }

            calculatingPath(new LatLng(location.getLatitude(), location.getLongitude()));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(mMap.getCameraPosition().zoom)
                    .bearing(location.getBearing())                // Sets the orientation of the camera to east
                    .tilt(mMap.getCameraPosition().tilt)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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

    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }


    private void callWithInRangeApi() {

        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(LiveTrackingMaps.this, Constants.accessToken);
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


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            } else {
                tts.setLanguage(Locale.US);
            }
        } else {
            Log.e("error", "Initilization Failed!");
        }
    }

    private void calculatingPath(LatLng latLng) {

        int ixLastPoint = 0;

        for (int i = 0; i < pointsArrayList.size(); i++) {

            LatLng point1 = pointsArrayList.get(i);
            LatLng point2 = pointsArrayList.get(i);
            List<LatLng> currentSegment = new ArrayList<>();
            currentSegment.add(point1);
            currentSegment.add(point2);
            if (PolyUtil.isLocationOnPath(latLng, currentSegment, true, 30)) {
                // save index of last point and exit loop
                ixLastPoint = i;
                onPath = true;
                break;
            } else {
                onPath = false;
            }
        }

        System.out.println("onPath" + onPath);

        if (onPath) {
            if (ixLastPoint > 0) {
                pointsArrayList.subList(0, ixLastPoint).clear();
            }
            if (polyline != null)
                polyline.setPoints(pointsArrayList);
        } else {
            if (polyline != null)
                polyline.remove();
            try {
                getRoute(origin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (driverId > 0 && userId > 0) {
            ((ApplicationClass) getApplication()).emit(origin.latitude, origin.longitude, userId, driverId, "", false);
        }


    }

    private void getRoute(LatLng origin) {
        String url = getDirectionsUrl(origin, destination);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

}
