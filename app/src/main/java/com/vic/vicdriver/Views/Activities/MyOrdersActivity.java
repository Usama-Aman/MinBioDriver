package com.vic.vicdriver.Views.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dueeeke.tablayout.SegmentTabLayout;
import com.dueeeke.tablayout.listener.OnTabSelectListener;
import com.vic.vicdriver.Controllers.Helpers.MyPagerAdapter;
import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Response.Login.Data;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.ApplicationClass;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;
import com.vic.vicdriver.Views.Activities.Support.SupportChat;
import com.vic.vicdriver.Views.Activities.Support.SupportListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicdriver.Utils.Common.showToast;
import static com.vic.vicdriver.Utils.Constants.companyPhone;
import static com.vic.vicdriver.Utils.Constants.currency;
import static com.vic.vicdriver.Utils.Constants.delivery_arrival_notify_circle;

public class MyOrdersActivity extends AppCompatActivity {

    private SegmentTabLayout orderTabs;
    private ViewPager viewPager;
    private RelativeLayout imgBack, imgSettings, imgTransactionHistory;
    private ConstraintLayout toolbarSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_orders);

        Data data = Common.fromJson(getApplicationContext());
        int userId = data.getId();
        ((ApplicationClass) getApplication()).initializeSocket(userId);

        orderTabs = findViewById(R.id.orderTabs);
        toolbarSupport = findViewById(R.id.toolbarSupport);
        toolbarSupport.setOnClickListener(v -> startActivity(new Intent(MyOrdersActivity.this, SupportListActivity.class)));
        imgSettings = findViewById(R.id.imgSettings);
        imgBack = findViewById(R.id.imgBack);
        imgTransactionHistory = findViewById(R.id.imgTransactionHistory);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyOrdersActivity.this, SettingsActivity.class));
            }
        });
        imgTransactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyOrdersActivity.this, TransactionHistory.class));
            }
        });


        getSettingsApi();
        setupTabs();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("type") && intent.hasExtra("orderId")) {
                boolean isComingFromNotification = intent.getBooleanExtra("isComingFromNotification", false);

                SharedPreference.saveBoolean(this, "FromNoti", isComingFromNotification);

                if (isComingFromNotification) {
                    String type = intent.getStringExtra("type");


                    int orderId = 0;

                    if(intent.hasExtra("orderId")){
                        if (intent.getExtras().get("orderId") instanceof Integer) {
                            orderId = intent.getIntExtra("orderId", 0);
                        } else if (intent.getExtras().get("orderId") instanceof String) {
                            if (Common.isSet(intent.getStringExtra("orderId")))
                                orderId = Integer.parseInt(intent.getStringExtra("orderId"));
                        }
                    }



                    String messageData = intent.getStringExtra("message");

                    checkForNotification(type, orderId, messageData, "body");
                }
            }
        }

    }

    //Initializing the tabs of orders screen
    private void setupTabs() {
        String[] titles = {getResources().getString(R.string.Available_Tab), getResources().getString(R.string.Ongoing_Tab),
                getResources().getString(R.string.Delivered_Tab)};

        orderTabs.setTextUnselectColor(MyOrdersActivity.this.getResources().getColor(R.color.splashColor));
        orderTabs.setIndicatorColor(MyOrdersActivity.this.getResources().getColor(R.color.splashColor));
        orderTabs.setDividerColor(MyOrdersActivity.this.getResources().getColor(R.color.catalogueBackground));
        orderTabs.setIndicatorAnimEnable(true);
        orderTabs.setIndicatorBounceEnable(true);
        orderTabs.setTextAllCaps(true);
        orderTabs.setTabSpaceEqual(false);

        viewPager = findViewById(R.id.ordersViewpager);
        PagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), titles);
        viewPager.setAdapter(pagerAdapter);
        orderTabs.setTabData(titles);
        orderTabs.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                orderTabs.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }

    //Setting api to get the global values in the apps
    private void getSettingsApi() {
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> getSettings = api.getSettings("Bearer " +
                SharedPreference.getSimpleString(MyOrdersActivity.this, Constants.accessToken));

        getSettings.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());

                        JSONArray data = new JSONArray();
                        data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = new JSONObject();
                            object = data.getJSONObject(i);
                            if (object.getString("key").equals("currency")) {
                                SharedPreference.saveSimpleString(MyOrdersActivity.this,
                                        currency, object.getString("value"));
                            } else if (object.getString("key").equals("delivery_arrival_notify_circle")) {
                                SharedPreference.saveSimpleString(MyOrdersActivity.this,
                                        delivery_arrival_notify_circle, object.getString("value"));
                            } else if (object.getString("key").equals("company_phone")) {
                                SharedPreference.saveSimpleString(MyOrdersActivity.this,
                                        companyPhone, object.getString("value"));
                            }
                        }


                    } else {

                        if (response.code() == 401) {
                            SharedPreference.saveSimpleString(MyOrdersActivity.this, Constants.isLoggedIn, "false");
                            Intent i = new Intent(MyOrdersActivity.this, SignIn.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }

                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(MyOrdersActivity.this, jsonObject.getString("message"), false);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Settings Api", "onFailure: " + t.getMessage());
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0);
            orderTabs.setCurrentTab(0);
        } else
            showExitDialoge();
    }

    void showExitDialoge() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyOrdersActivity.this);
        builder.setMessage(getString(R.string.exit_app));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finishAffinity();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra("type") && intent.hasExtra("orderId")) {
            boolean isComingFromNotification = intent.getBooleanExtra("isComingFromNotification", false);
            if (isComingFromNotification) {
                String type = intent.getStringExtra("type");
                String body = intent.getStringExtra("body");
                int orderId = 0;
                String messageData = "";
                if (type.equals(Constants.MessageReceivedFromBuyer) || type.equals(Constants.MessageReceivedFromSeller)) {
                    orderId = Integer.parseInt(intent.getStringExtra("orderId"));
                    messageData = intent.getStringExtra("message");
                }

                checkForNotification(type, orderId, messageData, body);
            }
        }
    }

    private void checkForNotification(String type, int orderId, String messageData, String body) {

        String value = SharedPreference.getSimpleString(getApplicationContext(), Constants.isLoggedIn);

        if (value.equals("false")) {
            Intent i = new Intent(MyOrdersActivity.this, SignIn.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        } else if (type.equals(Constants.NewOrderCreated)) {
            Intent AvailableOrdersBroadCast = new Intent("AvailableOrdersBroadCast");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(AvailableOrdersBroadCast);
        } else if (type.equals(Constants.MessageReceivedFromSeller)) {
            goToSellerChat(messageData);
        } else if (type.equals(Constants.MessageReceivedFromBuyer)) {
            goToBuyerChat(messageData);
        } else if (type.equals(Constants.AdminCommentOnSupport)) {
            goToSupportChatScreen(orderId);
        }

    }

    private void goToSellerChat(String messageData) {
        try {
            JSONObject jsonObject = new JSONObject(messageData);
            int orderId = jsonObject.getInt("order_id");
            Intent intent = new Intent(MyOrdersActivity.this, Driver2Seller.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("userName", jsonObject.getString("user_name"));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToBuyerChat(String messageData) {
        try {
            JSONObject jsonObject = new JSONObject(messageData);
            int orderId = jsonObject.getInt("order_id");
            Intent intent = new Intent(MyOrdersActivity.this, Driver2Buyer.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("userName", jsonObject.getString("user_name"));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Function to go to Complaint chat screen

    public void goToSupportChatScreen(int supportId) {

        System.gc();
        Log.e("Ids:    ", "::::  " + supportId);
        Intent i = new Intent(this, SupportChat.class);
        i.putExtra("supportId", String.valueOf(supportId));
        startActivity(i);
    }

}
