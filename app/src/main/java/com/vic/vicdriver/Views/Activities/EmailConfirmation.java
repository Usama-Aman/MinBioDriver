package com.vic.vicdriver.Views.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicdriver.Utils.Common.showToast;


public class EmailConfirmation extends AppCompatActivity implements View.OnClickListener {


    private ConstraintLayout btnSendEmailBack, btnToLoginEmail;
    private String emailToResendEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirmation);
        initViews();
    }

    //Initializing the views
    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        emailToResendEmail = bundle.getString("email");
        btnToLoginEmail = findViewById(R.id.btnToLoginEmail);
        btnSendEmailBack = findViewById(R.id.btnSendEmailBack);
        btnToLoginEmail.setOnClickListener(this);
        btnSendEmailBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnToLoginEmail) {
            Common.showDialog(EmailConfirmation.this);
            callConfirmEmailApi();
        } else if (view.getId() == R.id.btnSendEmailBack) {
            Common.showDialog(EmailConfirmation.this);
            callResendEmailApi();
        }
    }

    //Calling api to resent the email on user email account
    private void callResendEmailApi() {
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> resendEmail = api.resendEmail(Splash.appLanguage, emailToResendEmail);
        resendEmail.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        boolean status = false;
                        if (jsonObject.has("status")) {
                            status = jsonObject.getBoolean("status");
                            if (status) {
                                String message = jsonObject.getString("message");
                                if (!message.isEmpty())
                                    showToast(EmailConfirmation.this, message, true);
                            }
                        }
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        boolean status = false;
                        String message = jsonObject.getString("message");
                        if (!message.isEmpty())
                            showToast(EmailConfirmation.this, message, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Resend Email", "onFailure: " + t.getMessage());
            }
        });
    }

    //Calling api to check whether the user confirm his email or not
    private void callConfirmEmailApi() {
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> confirmEmail = api.confirmEmail(Splash.appLanguage, emailToResendEmail);

        Log.d("EmailConfirmation", "callConfirmEmailApi: " + SharedPreference.getSimpleString(EmailConfirmation.this,
                Constants.userEmail));

        confirmEmail.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        boolean status = false;
                        if (jsonObject.has("status")) {
                            status = jsonObject.getBoolean("status");
                            if (status) {
                                String message = jsonObject.getString("message");
                                if (!message.isEmpty())
                                    showToast(EmailConfirmation.this, message, true);
                                Intent i = new Intent(EmailConfirmation.this, SignIn.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(i);
                                    }
                                }, 1800);
                            }
                        }
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        boolean status = false;
                        String message = jsonObject.getString("message");
                        if (!message.isEmpty())
                            showToast(EmailConfirmation.this, message, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Confirm Email", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
