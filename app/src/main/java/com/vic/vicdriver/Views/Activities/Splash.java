package com.vic.vicdriver.Views.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Response.Login.Data;
import com.vic.vicdriver.Models.Response.Login.Login;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.ApplicationClass;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Splash extends AppCompatActivity {

    public static String appLanguage;
    private String type = "", message;
    private String orderId;
    private boolean isNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Constants.isAppRunning = true;

        if (!SharedPreference.getBoolean(Splash.this, Constants.isFirstTime)) {
            SharedPreference.saveSimpleString(this, Constants.language, Constants.french);
            SharedPreference.saveBoolean(this, Constants.isFirstTime, true);
            Splash.appLanguage = Constants.french;
            ApplicationClass applicationClass = (ApplicationClass) Splash.this.getApplication();
            applicationClass.changeLocale(Splash.this);
        } else {
            if (SharedPreference.getSimpleString(Splash.this, Constants.language).equals(Constants.french)) {
                appLanguage = Constants.french;
                ApplicationClass applicationClass = (ApplicationClass) Splash.this.getApplication();
                applicationClass.changeLocale(Splash.this);
            } else if (SharedPreference.getSimpleString(Splash.this, Constants.language).equals(Constants.english)) {
                appLanguage = Constants.english;
                ApplicationClass applicationClass = (ApplicationClass) Splash.this.getApplication();
                applicationClass.changeLocale(Splash.this);
            }
        }


        new Handler().postDelayed(() -> {
            try {
                String value = SharedPreference.getSimpleString(getApplicationContext(), Constants.isLoggedIn);
                if (!value.isEmpty()) {
                    if (value.equals("true")) {
                        Data obj = Common.fromJson(getApplicationContext());
                        if (obj != null && obj.getId() != null) {

                            if (getIntent().hasExtra("type")) {
                                type = getIntent().getStringExtra("type");
                                orderId = (getIntent().getStringExtra("orderId"));
                                message = (getIntent().getStringExtra("message"));
                                isNotification = getIntent().getBooleanExtra("isComingFromNotification", false);


                                Intent i = new Intent(Splash.this, MyOrdersActivity.class);
                                i.putExtra("type", type);
                                i.putExtra("orderId", orderId);
                                i.putExtra("message", message);
                                if (isNotification)
                                    i.putExtra("isComingFromNotification", true);
                                startActivity(i);
                                finish();

                            } else {
                                getProfileDataApi();
                            }

                        }
                    } else if (value.equals("false")) {
                        Intent i = new Intent(Splash.this, SignIn.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    Intent i = new Intent(Splash.this, SignIn.class);
                    startActivity(i);
                    finish();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }, 3000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    //Setting api to get the global values in the apps
    private void getProfileDataApi() {

        if (!Common.isOnline(this)) {
            callSettingActivity();
            return;
        }


        Api api = RetrofitClient.getClient().create(Api.class);
        Call<Login> getSettings = api.getProfileData("Bearer " +
                SharedPreference.getSimpleString(Splash.this, Constants.accessToken));

        getSettings.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {

                try {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus()) {

                            if (response.body().getData().getIsProfileComplete() == 1) {

                                Intent i = new Intent(Splash.this, MyOrdersActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                callSettingActivity();
                            }
                        } else {
                            callSettingActivity();
                        }
                    } else {
                        callSettingActivity();
                    }
                } catch (Exception e) {
                    callSettingActivity();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                callSettingActivity();
            }
        });

    }

    private void callSettingActivity() {
        Intent i = new Intent(Splash.this, SettingsActivity.class);
        i.putExtra("from0Profile", true);
        startActivity(i);
        finish();
    }
}