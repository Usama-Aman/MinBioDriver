package com.vic.vicdriver.Views.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.ApplicationClass;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.vic.vicdriver.Utils.Common.showToast;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class LiveTrackingWithMapBox extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener,
        View.OnClickListener {

    /*     Mapbox Variables     */
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapboxNavigation navigation;
    private NavigationMapRoute navigationMapRoute;
    private LocationComponent locationComponent;
    private DirectionsRoute currentRoute;
    private SymbolManager symbolManager;
    private List<Symbol> symbols = new ArrayList<>();
    private LocationChangeListeningActivityLocationCallback callback = new LocationChangeListeningActivityLocationCallback(this);
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 10;
    private Point originPoint, destinationPoint;
    private GeoJsonSource originGeoJsonSource, destinationGeoJsonSource;

    //IDs for marker
    private static final String ORIGIN_SOURCE_ID = "origin-source-id";
    private static final String DESTINATION_SOURCE_ID = "destination-source-id";

    private static final String ORIGIN_ICON_ID = "origin-icon-id";
    private static final String DESTINATION_ICON_ID = "destination-icon-id";

    private static final String ORIGIN_LAYER_ID = "origin-layer-id";
    private static final String DESTINATION_LAYER_ID = "destination-layer-id";

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";

    /*      Local Variables  */
    private int driverId = 0, userId = 0;
    private Button startNavigation;
    private LatLng origin, destination;
    private String userLat, userLng;
    private String orderNumber = "";
    private Location userLocation;
    private LocationEngine locationEngine;
    private boolean isRouteLoadedOnce = false;
    private String orderStatus = "";
    private RelativeLayout imgBack;
    private double distanceInKM;
    private String TAG = "LiveTrackingWithMapBox";
    private String styleURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigation = new MapboxNavigation(this, getResources().getString(R.string.mapbox_access_token));
        Mapbox.getInstance(this, getResources().getString(R.string.mapbox_access_token));
        styleURL = getResources().getString(R.string.style_URL);
        setContentView(R.layout.live_tracking_with_mapbox);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {


        startNavigation = findViewById(R.id.startNavigation);

        mapView.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        orderNumber = bundle.getString("order_number");
        userLat = bundle.getString("lat");
        userLng = bundle.getString("lng");
        driverId = bundle.getInt("driverId");
        userId = bundle.getInt("userId");
        orderStatus = bundle.getString("status");

        userLocation = new Location("");
        userLocation.setLatitude(Double.parseDouble(userLat));
        userLocation.setLongitude(Double.parseDouble(userLng));

        if (!userLng.isEmpty() && !userLat.isEmpty()) {
            destination = new LatLng(Double.parseDouble(userLat), Double.parseDouble(userLng));
        } else
            finish();

        try {
            Common.showDialog(LiveTrackingWithMapBox.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(view -> finish());
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            if (style.isFullyLoaded()) {

                LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, mapboxMap, style);
                try {
                    localizationPlugin.matchMapLanguageWithDeviceDefault();
                } catch (RuntimeException exception) {
                    Log.d(TAG, exception.toString());
                }

                enableLocationComponent(style);
                startNavigation.setOnClickListener(LiveTrackingWithMapBox.this);
            }
        });
    }

    private void addMarkers(Style style) {

        originGeoJsonSource = new GeoJsonSource(ORIGIN_SOURCE_ID,
                Feature.fromGeometry(originPoint));

        destinationGeoJsonSource = new GeoJsonSource(DESTINATION_SOURCE_ID,
                Feature.fromGeometry(destinationPoint));

        style.addImage((ORIGIN_ICON_ID), BitmapFactory.decodeResource(
                getResources(), R.drawable.map_marker_dark));

        style.addImage((DESTINATION_ICON_ID), BitmapFactory.decodeResource(
                getResources(), R.drawable.map_marker_light));

        style.addSource(originGeoJsonSource);
        style.addSource(destinationGeoJsonSource);

        style.addLayer(new SymbolLayer(ORIGIN_LAYER_ID, ORIGIN_SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage(ORIGIN_ICON_ID),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                ));

        style.addLayer(new SymbolLayer(DESTINATION_LAYER_ID, DESTINATION_SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage(DESTINATION_ICON_ID),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                ));

        style.addSource(new GeoJsonSource(ROUTE_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{})));

        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(3f),
                lineColor(Color.parseColor("#0a3a52"))
        );

        style.addLayer(routeLayer);

    }


    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.NORMAL);
            locationComponent.zoomWhileTracking(10d);

            UiSettings uiSettings = mapboxMap.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);

            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        locationComponent.setLocationEngine(locationEngine);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);

    }


    private void getRoute(LatLng l1, LatLng l2) {

        Point origin = Point.fromLngLat(l1.getLongitude(), l1.getLatitude());
        Point destination = Point.fromLngLat(l2.getLongitude(), l2.getLatitude());

        Locale locale = new Locale(SharedPreference.getSimpleString(LiveTrackingWithMapBox.this, Constants.language));

        NavigationRoute.builder(this)
                .accessToken(getResources().getString(R.string.mapbox_access_token))
                .origin(origin)
                .language(locale)
                .destination(destination)
                .addApproaches("unrestricted", "curb")
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                        try {
                            Common.dissmissDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (response.isSuccessful()) {
                            currentRoute = response.body().routes().get(0);

                            if (mapboxMap != null) {
                                mapboxMap.getStyle(style -> {

                                    GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                                    if (source != null) {
                                        Timber.d("onResponse: source != null");
                                        source.setGeoJson(FeatureCollection.fromFeature(
                                                Feature.fromGeometry(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6))));
                                    }
                                });
                            }

                            originGeoJsonSource.setGeoJson(Point.fromLngLat(origin.longitude(), origin.latitude()));

//                            navigation.addOffRouteListener(LiveTrackingWithMapBox.this);


                        } else {
                            Log.d("Error", "onResponse: " + response.errorBody());
                        }

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        try {
                            Common.dissmissDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("Error", "onResponse: " + t.getMessage());
                    }
                });

    }

    private void turnGPSOn() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(style -> enableLocationComponent(style));
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startNavigation) {
            boolean simulateRoute = false;   //          To stimulate a route
            turnGPSOn();
            if (orderStatus.equals("Accepted")) {
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .waynameChipEnabled(true)
                        .shouldSimulateRoute(simulateRoute)
                        .build();
                NavigationLauncher.startNavigation(LiveTrackingWithMapBox.this, options);

            } else {
                showToast(LiveTrackingWithMapBox.this, getResources().getString(R.string.please_accept_delivery_first), false);
            }

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();

        if (isRouteLoadedOnce) {

            Log.d(TAG, "onResume: " + isRouteLoadedOnce + "   get Route is called");
            getRoute(origin, destination);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    private static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<LiveTrackingWithMapBox> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(LiveTrackingWithMapBox activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            LiveTrackingWithMapBox activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                    activity.origin = new LatLng(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());

                    activity.distanceInKM = location.distanceTo(activity.userLocation) / 1000;
                    if (activity.distanceInKM <= Double.parseDouble(SharedPreference.getSimpleString(activity, Constants.delivery_arrival_notify_circle))) {
                        if (!SharedPreference.getBoolean(activity, Constants.WithInRange)) {
                            activity.callWithInRangeApi();
                            SharedPreference.saveBoolean(activity, Constants.WithInRange, true);
                        }
                    }

                    if (activity.driverId > 0 && activity.userId > 0) {
                        ((ApplicationClass) activity.getApplication()).emit(activity.origin.getLatitude(), activity.origin.getLongitude(),
                                activity.userId, activity.driverId, "", false);
                    }

                    if (!activity.isRouteLoadedOnce) {

                        activity.originPoint = Point.fromLngLat(activity.origin.getLongitude(), activity.origin.getLatitude());
                        activity.destinationPoint = Point.fromLngLat(activity.destination.getLongitude(), activity.destination.getLatitude());

                        activity.addMarkers(activity.mapboxMap.getStyle());

                        try {
                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    .include(activity.origin)
                                    .include(activity.destination)
                                    .build();

                            int width = activity.getResources().getDisplayMetrics().widthPixels;
                            int padding = (int) (width * 0.25);

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
                            activity.mapboxMap.moveCamera(cameraUpdate);
                            activity.mapboxMap.animateCamera(cameraUpdate);

                            activity.getRoute(activity.origin, activity.destination);
                            activity.isRouteLoadedOnce = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
        }
    }

    private void callWithInRangeApi() {

        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(this, Constants.accessToken);
        token = "Bearer " + token;

        Call<ResponseBody> call = api.withInRange(token, orderNumber);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                    } else {

                        jsonObject = new JSONObject(response.errorBody().string());
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