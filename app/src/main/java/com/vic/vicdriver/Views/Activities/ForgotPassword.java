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
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.Common;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicdriver.Utils.Common.showToast;


public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText etForgotPasswordEmail;
    private ConstraintLayout btnSendEmailForgotPassword;
    private ImageView ivForgotPasswordBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();
    }

    //Initializing the views
    private void initViews() {
        ivForgotPasswordBack = findViewById(R.id.ivForgotPasswordBack);
        ivForgotPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        etForgotPasswordEmail = findViewById(R.id.etForgotPasswordEmail);
        btnSendEmailForgotPassword = findViewById(R.id.btnSendEmailForgotPassword);
        btnSendEmailForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSendEmailForgotPassword)
            if (view.getId() == R.id.btnSendEmailForgotPassword) {
                Common.hideKeyboard(ForgotPassword.this);
                validate();
            }
    }

    private void validate() {

        if (etForgotPasswordEmail.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.forgotPasswordErrorMessageEnterEmail), false);
            return;
        }

        Common.showDialog(ForgotPassword.this);
        callForgotApi();


    }

    //Calling forgot password api
    private void callForgotApi() {
        Api api = RetrofitClient.getClient().create(Api.class);
//        String token =SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken);

        Call<ResponseBody> forgotPassword = api.forgotPassword(Splash.appLanguage, etForgotPasswordEmail.getText().toString());

        forgotPassword.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.has("status")) {
                            boolean status = jsonObject.getBoolean("status");
                            if (status) {
                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    showToast(ForgotPassword.this, message, true);

                                    JSONObject data=jsonObject.getJSONObject("data");
                                    String emailOrPhone = data.getString("email");

                                    new Handler().postDelayed(() -> {
                                        Intent intent = new Intent(ForgotPassword.this, ResetPassword.class);
                                        intent.putExtra("email", emailOrPhone);
                                        startActivity(intent);
                                        finish();
                                    }, 1800);
                                }
                            }
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.has("message")) {
                            String message = jsonObject.getString("message");
                            showToast(ForgotPassword.this, message, false);
                            Common.hideKeyboard(ForgotPassword.this);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("ForgotPassword", "onFailure: " + t.getMessage());
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
