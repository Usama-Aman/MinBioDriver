package com.vic.vicdriver.Views.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Response.GenResponse;
import com.vic.vicdriver.Models.Response.Login.Data;
import com.vic.vicdriver.Models.Response.orders_pack.Datum;
import com.vic.vicdriver.Models.Response.orders_pack.Item;
import com.vic.vicdriver.Models.Response.orders_pack.OrderDetailResponse;
import com.vic.vicdriver.Models.Response.orders_pack.PickupAddress;
import com.vic.vicdriver.Network.GPSService;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;
import com.kyanogen.signatureview.SignatureView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.vic.vicdriver.Utils.Common.showToast;

public class SummaryActivity extends AppCompatActivity {

    private RelativeLayout imgBack;
    private LinearLayout imgLocation;
    private TableLayout tabItems;
    private ConstraintLayout btnAccept, btnCompleted;
    private Button btnReject;
    private LinearLayout linAcceptReject, linCompleted;
    private Data data;
    private Intent serviceIntent;
    private String currency = "€";
    private TextView tvProductTxt, tvQuantityTxt, tvTotalTxt, tvCategoryName;
    private TextView vendorCompantName, buyerCompanyName, vendorAddress, buyerAddress, tvDriverAmount;
    private Datum orderModel;
    private ImageView btnPickUp, ivDeliveryDetailMap, buyerMap, chatWithBuyer, chatWithSeller;
    private int pickCount = 0;
    private boolean isEnableDirection = false;
    private TableLayout vendorTableLayout;

    private String driverLat, driverLng;
    LocationManager locManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        data = Common.fromJson(getApplicationContext());

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission(100);
        } else {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L,
                    500.0f, locationListener);

            Location location = locManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            driverLat = String.valueOf(location.getLatitude());
            driverLng = String.valueOf(location.getLongitude());

            displayUI();
        }
    }


    private void displayUI() {
        imgLocation = findViewById(R.id.imgLocation);
        imgBack = findViewById(R.id.imgBack);
        tabItems = findViewById(R.id.tabItems);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);
        btnCompleted = findViewById(R.id.btnCompleted);
        vendorTableLayout = findViewById(R.id.vendorTableLayout);
        linAcceptReject = findViewById(R.id.linAcceptReject);
        linCompleted = findViewById(R.id.linCompleted);
        buyerAddress = findViewById(R.id.buyerAddress);
        buyerCompanyName = findViewById(R.id.buyerCompanyName);
        buyerMap = findViewById(R.id.buyerMap);
        chatWithBuyer = findViewById(R.id.chatWithBuyer);
        tvDriverAmount = findViewById(R.id.tvDriverAmount);

        imgBack.setOnClickListener(v -> finish());
//        btnAccept.setOnClickListener(v -> checkLocationPermission(MY_PERMISSIONS_REQUEST_LOCATION));
        btnAccept.setOnClickListener(v -> acceptOrder("Accepted", null));
        btnReject.setOnClickListener(v -> finish());
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.saveBoolean(SummaryActivity.this, Constants.isAnotherOrder, false);
                showSignatureDialog(0, null, 0, "fromComplete");
            }
        });

        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bundle bundle = new Bundle();
                bundle.putString("lat", orderModel.getLatitude());
                bundle.putString("lng", orderModel.getLongitude());
                bundle.putString("order_number", orderModel.getOrderNumber());
                bundle.putInt("userId", orderModel.getUserId());
                bundle.putInt("driverId", data.getId());
                bundle.putString("status", orderModel.getDeliveryStatus());
                bundle.putBoolean("isEnableDirection", isEnableDirection);
                Intent intent = new Intent(SummaryActivity.this, LiveTrackingWithMapBox.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        try {
            String symbol = SharedPreference.getSimpleString(getApplicationContext(),
                    currency);
            if (symbol != "" && !symbol.isEmpty()) {
                currency = symbol;
            }

            Bundle bundle = getIntent().getExtras();
            orderModel = bundle.getParcelable("orderModel");
            orderModel.setItems(bundle.getParcelableArrayList("items"));
            orderModel.setPickupAddresses(bundle.getParcelableArrayList("pickAddresses"));

            if (orderModel.getDeliveryStatus().equalsIgnoreCase("accepted")) {
                callDetailApi();

            } else {
                Common.dissmissDialog();
                populateData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(this, getResources().getString(R.string.something_is_not_right), false);
        }
    }


    private void callDetailApi() {
        Common.showDialog(SummaryActivity.this);

        String token = "Bearer " + SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken);
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<OrderDetailResponse> call = api.getOrderDetails(token, driverLat, driverLng);
        call.enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(Call<OrderDetailResponse> call, Response<OrderDetailResponse> response) {
                Common.dissmissDialog();
                try {

                    if (response.isSuccessful()) {
                        orderModel = response.body().getData();

                        populateData();

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SummaryActivity.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });

    }


    void populateData() {
        try {
            if (orderModel != null && orderModel.getOrderNumber() != null) {

                /*Rendering Vendor List UI*/
                if (orderModel.getPickupAddresses().size() > 0 && orderModel.getPickupAddresses() != null) {
                    for (int i = 0; i < orderModel.getPickupAddresses().size(); i++) {
                        PickupAddress pickUpAddresses = orderModel.getPickupAddresses().get(i);
                        View view = getLayoutInflater().inflate(R.layout.vendors_table_layout, null);

                        vendorAddress = view.findViewById(R.id.vendorAddress);
                        vendorCompantName = view.findViewById(R.id.vendorCompanyName);
                        ivDeliveryDetailMap = view.findViewById(R.id.deliveryMap);
                        chatWithSeller = view.findViewById(R.id.chatWithSeller);
                        btnPickUp = view.findViewById(R.id.btnPickVendorProducts);


                        tvDriverAmount.setText("You will earn " + currency + orderModel.getDriverAmount() + " for this delivery");

                        ivDeliveryDetailMap.setOnClickListener(v -> {
                            Intent intent = new Intent(SummaryActivity.this, DeliveryDetailMaps.class);
                            intent.putExtra("address", pickUpAddresses.getAddress());
                            intent.putExtra("lat", Double.parseDouble(pickUpAddresses.getLatitude()));
                            intent.putExtra("lng", Double.parseDouble(pickUpAddresses.getLongitude()));
                            startActivity(intent);

                        });

                        chatWithSeller.setOnClickListener(v -> {
                            Intent intent = new Intent(SummaryActivity.this, Driver2Seller.class);
                            intent.putExtra("orderId", pickUpAddresses.getId());
                            intent.putExtra("userName", pickUpAddresses.getCompany());
                            startActivity(intent);
                        });

                        if (orderModel.getDeliveryStatus().equalsIgnoreCase("Accepted")) {
                            if (pickUpAddresses.isIs_reached()) {
                                if (pickUpAddresses.getDeliveryStatus().equals("Pending")) {
                                    btnPickUp.setVisibility(View.VISIBLE);
                                    btnPickUp.setEnabled(true);
                                } else {
                                    pickCount++;
                                    btnPickUp.setVisibility(View.GONE);
                                    btnPickUp.setEnabled(false);
                                }
                            } else {
                                btnPickUp.setVisibility(View.GONE);
                                btnPickUp.setEnabled(false);
                            }
                        } else {
                            btnPickUp.setVisibility(View.GONE);
                            btnPickUp.setEnabled(false);
                        }


                        int finalI = i;
                        btnPickUp.setOnClickListener(v -> {
                            showSignatureDialog(pickUpAddresses.getId(), view, finalI, "fromPickUp");
                        });


                        if (orderModel.getDeliveryStatus().equalsIgnoreCase("Accepted"))
                            chatWithSeller.setVisibility(View.VISIBLE);
                        else
                            chatWithSeller.setVisibility(View.GONE);


                        vendorAddress.setText(pickUpAddresses.getAddress());
                        vendorCompantName.setText(pickUpAddresses.getCompany());

                        vendorTableLayout.addView(view);
                    }
                }

                /*Rendering the Products UI*/
                if (orderModel.getItems() != null && orderModel.getItems().size() > 0) {

                    for (int i = 0; i < orderModel.getItems().size(); i++) {
                        Item itemModel = orderModel.getItems().get(i);
                        View view = getLayoutInflater().inflate(R.layout.summary_items_row, null);

                        tvProductTxt = view.findViewById(R.id.tvProductTxt);
                        tvQuantityTxt = view.findViewById(R.id.tvQuantityTxt);
                        tvTotalTxt = view.findViewById(R.id.tvTotalTxt);
                        tvCategoryName = view.findViewById(R.id.tvCategoryName);

                        tvProductTxt.setText(itemModel.getProductVariety());
                        tvQuantityTxt.setText(itemModel.getQuantity().toString() + itemModel.getUnit());
                        tvTotalTxt.setText(itemModel.getTotal() + currency);
                        tvCategoryName.setText(itemModel.getProductCategory());
                        tabItems.addView(view);

                    }
                }

                /*Rendering Buyer Information*/
                buyerCompanyName.setText(orderModel.getBuyerCompany());
                buyerAddress.setText(orderModel.getAddress());

                buyerMap.setOnClickListener(v -> {
                    Intent intent = new Intent(SummaryActivity.this, DeliveryDetailMaps.class);
                    intent.putExtra("address", orderModel.getAddress());
                    intent.putExtra("lat", Double.parseDouble(orderModel.getLatitude()));
                    intent.putExtra("lng", Double.parseDouble(orderModel.getLongitude()));
                    startActivity(intent);
                });


                if (orderModel.getDeliveryStatus().equalsIgnoreCase("accepted")) {
                    linCompleted.setVisibility(View.VISIBLE);
                    linAcceptReject.setVisibility(View.GONE);
                } else {
                    linAcceptReject.setVisibility(View.VISIBLE);
                    linCompleted.setVisibility(View.GONE);
                }

                if (pickCount == orderModel.getPickupAddresses().size()) {
                    isEnableDirection = true;
                    btnCompleted.setEnabled(true);
                    imgLocation.setVisibility(View.VISIBLE);
                    btnCompleted.setBackground(getResources().getDrawable(R.drawable.round_button_green));
                } else {
                    isEnableDirection = false;
                    imgLocation.setVisibility(View.GONE);
                    btnCompleted.setBackground(getResources().getDrawable(R.drawable.round_button_gray));
                    btnCompleted.setEnabled(false);
                }


                if (orderModel.getDeliveryStatus().equalsIgnoreCase("Accepted")) {
                    chatWithBuyer.setEnabled(true);
                    chatWithBuyer.setVisibility(View.VISIBLE);
                } else {
                    chatWithBuyer.setEnabled(false);
                    chatWithBuyer.setVisibility(View.GONE);
                }

                chatWithBuyer.setOnClickListener(v -> {
                    Intent intent = new Intent(SummaryActivity.this, Driver2Buyer.class);
                    intent.putExtra("orderId", orderModel.getDeliveryAddressId());
                    intent.putExtra("userName", orderModel.getBuyerCompany());
                    startActivity(intent);
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSignatureDialog(Integer id, View v, int position, String fromWhere) {

        Dialog dialog = new Dialog(SummaryActivity.this);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.layout_signature);

        ConstraintLayout done, clear;
        SignatureView signatureView = dialog.findViewById(R.id.signature_view);
        done = dialog.findViewById(R.id.btnDoneSignature);
        clear = dialog.findViewById(R.id.btnClearSignature);
        ConstraintLayout crossHideDialog = dialog.findViewById(R.id.crossHideDialog);
        crossHideDialog.setOnClickListener(view -> {
            dialog.dismiss();
        });

        done.setOnClickListener(view -> {
            if (signatureView.isBitmapEmpty()) {
                return;
            } else {
                dialog.dismiss();
                Bitmap bitmap = signatureView.getSignatureBitmap();

                if (fromWhere.equals("fromPickUp"))
                    callPickUpApi(id, v, position, saveBitmap(bitmap));
                else if (fromWhere.equals("fromComplete"))
                    acceptOrder("Completed", saveBitmap(bitmap));
            }
        });


        clear.setOnClickListener(view -> {
            signatureView.clearCanvas();
        });


        dialog.show();


    }

    private File saveBitmap(Bitmap b) {
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, "Temp" + ".png");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            b.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }


        return imageFile;
    }

    private void callPickUpApi(Integer id, View view, int position, File signatureFile) {

        Common.showDialog(SummaryActivity.this);

        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken);
        token = "Bearer " + token;

        RequestBody rbOrderId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));

        RequestBody rbSignature = RequestBody.create(MediaType.parse("image/*"), signatureFile);
        MultipartBody.Part signatureMultipart = MultipartBody.Part.createFormData("signature", signatureFile.getName(), rbSignature);

        Call<ResponseBody> call = api.pickUpItem(token, rbOrderId, signatureMultipart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Common.dissmissDialog();
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        showToast(SummaryActivity.this, jsonObject.getString("message"), true);
                        ImageView button = view.findViewById(R.id.btnPickVendorProducts);
                        button.setVisibility(View.GONE);
                        button.setEnabled(false);

                        orderModel.getPickupAddresses().get(position).setDeliveryStatus("Inprogress");

                        pickCount++;
                        Intent ongoingOrdersBroadCast = new Intent("OngoingOrdersBroadCast");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(ongoingOrdersBroadCast);


                        if (pickCount == orderModel.getPickupAddresses().size()) {
                            btnCompleted.setEnabled(true);
                            isEnableDirection = true;
                            imgLocation.setVisibility(View.VISIBLE);
                            btnCompleted.setBackground(getResources().getDrawable(R.drawable.round_button_green));
                        } else {
                            isEnableDirection = false;
                            imgLocation.setVisibility(View.GONE);
                            btnCompleted.setBackground(getResources().getDrawable(R.drawable.round_button_gray));
                            btnCompleted.setEnabled(false);
                        }

                        Common.orderModeltoJson(SummaryActivity.this, orderModel);
                        Log.d(TAG, "onResponse: ");


                    } else {

                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SummaryActivity.this, jsonObject.getString("message"), false);
                        ImageView button = view.findViewById(R.id.btnPickVendorProducts);
                        button.setVisibility(View.VISIBLE);
                        button.setEnabled(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());

                Button button = view.findViewById(R.id.btnPickVendorProducts);
                button.setVisibility(View.VISIBLE);
                button.setEnabled(true);
            }
        });
    }

    void acceptOrder(String status, File signatureFile) {
        Common.showDialog(SummaryActivity.this);

        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken);
        token = "Bearer " + token;

        if (orderModel != null && orderModel.getOrderNumber() != null) {

            RequestBody rbStatus = RequestBody.create(MediaType.parse("text/plain"), status);
            RequestBody rbAddressId = RequestBody.create(MediaType.parse("text/plain"), orderModel.getDeliveryAddressId().toString());

            Call<GenResponse> call = null;
            if (status.equals("Accepted"))
                call = api.updateAcceptOrder(token, rbStatus, rbAddressId);
            else if (status.equals("Completed")) {

                RequestBody rbSignature = RequestBody.create(MediaType.parse("image/*"), signatureFile);
                MultipartBody.Part signatureMultipart = MultipartBody.Part.createFormData("signature", signatureFile.getName(), rbSignature);


                call = api.updateOrderStatus(token, rbStatus, rbAddressId, signatureMultipart);
            }

            call.enqueue(new Callback<GenResponse>() {
                @Override
                public void onResponse(Call<GenResponse> call, Response<GenResponse> response) {

                    Common.dissmissDialog();
                    try {
                        if (response.isSuccessful()) {

                            if (response.body().getStatus()) {

                                showToast(SummaryActivity.this, response.body().getMessage() + "", true);

                                new Handler().postDelayed(() -> {

                                    try {

                                        Intent availableOrdersBroadCast = new Intent("AvailableOrdersBroadCast");
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(availableOrdersBroadCast);
                                        Intent deliveredOrdersBroadCast = new Intent("DeliveredOrdersBroadCast");
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(deliveredOrdersBroadCast);
                                        Intent ongoingOrdersBroadCast = new Intent("OngoingOrdersBroadCast");
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(ongoingOrdersBroadCast);

                                        if (status.equals("Accepted")) {
                                            SharedPreference.saveBoolean(SummaryActivity.this, "isAccepted", true);
                                            SharedPreference.saveBoolean(SummaryActivity.this, Constants.WithInRange, false);

                                        } else {
                                            SharedPreference.saveBoolean(SummaryActivity.this, "isAccepted", false);
                                            SharedPreference.saveBoolean(SummaryActivity.this, Constants.isAnotherOrder, false);

                                            SharedPreference.saveSimpleString(SummaryActivity.this, Constants.OrderNumber, "");
                                            SharedPreference.saveSimpleString(SummaryActivity.this, Constants.UserLat, "");
                                            SharedPreference.saveSimpleString(SummaryActivity.this, Constants.UserLng, "");
                                            SharedPreference.saveSimpleString(SummaryActivity.this, Constants.UserIdForNavigation, "");
                                            SharedPreference.saveSimpleString(SummaryActivity.this, Constants.DriverIdForNavigation, "");


                                            stopService(new Intent(SummaryActivity.this, GPSService.class));
                                        }

                                        finish();

                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }, 1200);
                            } else {
                                showToast(SummaryActivity.this, response.body().getMessage() + "", false);

                            }

                        } else {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            showToast(SummaryActivity.this, jsonObject.getString("message") + "", false);
                        }

                    } catch (
                            Exception e) {
                        Common.dissmissDialog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GenResponse> call, Throwable t) {
                    Common.dissmissDialog();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }

    }

    public void checkLocationPermission(int code) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        code);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        code);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == 100) {
                    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,
                            500.0f, locationListener);

                    Location location = locManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    driverLat = String.valueOf(location.getLatitude());
                    driverLng = String.valueOf(location.getLongitude());

                    displayUI();
                }
            }
        } else {
            Common.dissmissDialog();
//            showToast(SummaryActivity.this, getResources().getString(R.string.permission_denied), false);
            finish();
        }
        return;
    }

    private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            driverLat = String.valueOf(location.getLatitude());
            driverLng = String.valueOf(location.getLongitude());
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

}
