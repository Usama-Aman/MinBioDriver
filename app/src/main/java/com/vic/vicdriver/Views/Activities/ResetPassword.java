package com.vic.vicdriver.Views.Activities;

import android.content.Context;
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

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

    private ConstraintLayout btnResetPassword;
    private EditText etOtp, etpassword, etCPassword;
    private String email = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();
    }

    private void initViews() {

        email = getIntent().getStringExtra("email");

        etOtp = findViewById(R.id.otpResetPassword);
        etpassword = findViewById(R.id.etpasswordResetPassword);
        etCPassword = findViewById(R.id.etCpasswordResetPassword);

        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(view -> {
            validate();
        });
    }

    private void validate() {

        if (etOtp.getText().toString().isEmpty()) {
            Common.showToast(this, getResources().getString(R.string.reset_error_please_enter_the_verification_code), false);
            return;
        }

        if (etpassword.getText().toString().isEmpty()) {
            Common.showToast(this, getResources().getString(R.string.reset_error_message_enter_password), false);
            return;
        }

        if (etCPassword.getText().toString().isEmpty()) {
            Common.showToast(this, getResources().getString(R.string.reset_error_enter_password_confirmation), false);
            return;
        }

        if (!etpassword.getText().toString().equals(etCPassword.getText().toString())) {
            Common.showToast(this, getResources().getString(R.string.reset_error_password_not_same), false);
            return;
        }

        if (email.equals("")) {
            Common.showToast(this, getResources().getString(R.string.something_is_not_right), false);
            return;
        }

        callRestPasswordApi();
    }

    private void callRestPasswordApi() {

        Common.showDialog(this);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.resetPassword(etpassword.getText().toString(), etCPassword.getText().toString(), email, etOtp.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());

                        Common.showToast(ResetPassword.this, jsonObject.getString("message"), true);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1800);


                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast(ResetPassword.this, jsonObject.getString("message"), false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("TAG", "onFailure: " + t.getMessage());
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
