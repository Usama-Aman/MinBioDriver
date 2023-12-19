package com.vic.vicdriver.Views.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Request.RegisterRequest;
import com.vic.vicdriver.Models.Response.PrivacyPolicy.PrivacyPolicyModel;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicdriver.Utils.Common.showToast;


public class SignUp extends AppCompatActivity implements View.OnClickListener {


    private ConstraintLayout btnSignUp, btnToLogin;
    private EditText etSignUpEmail, etSignUpName, etSignUpLastName, etSignUpPassword, etSignUpConfirmPassword, etPhoneNumber;
    private LinearLayout phoneLinear;
    private boolean isEmail, isPassword, isPassWordConfirm, isPhone, isName;
    private CountryCodePicker countryCodePicker;
    private ImageView back;
    private TextView alreadyHaveAnAccount;
    private CheckBox privacyCheck;
    private TextView privacyText;

    private boolean isChecked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }

    //Initializing the views
    private void initViews() {
        btnSignUp = findViewById(R.id.btnSignUp);
        back = findViewById(R.id.ivSignUpBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        phoneLinear = findViewById(R.id.signUpPhoneLinear);
        btnToLogin = findViewById(R.id.btnToLogin);

        alreadyHaveAnAccount = findViewById(R.id.alreadyHaveAnAccount);

        String text = "<font color='black'>" + getResources().getString(R.string.SignUpAlreadyHaveAnAccount) + "</font>" + " " +
                "<font color='#50a936'>" + getResources().getString(R.string.signUpLoginNow) + "</font>";
        alreadyHaveAnAccount.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);


        etSignUpName = findViewById(R.id.etSignUpFirstName);
        etSignUpEmail = findViewById(R.id.etSignUpEmail);
        etSignUpLastName = findViewById(R.id.etSignUpLastName);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        etPhoneNumber = findViewById(R.id.etSignUpPhoneNumber);
        countryCodePicker = findViewById(R.id.ccp);
        etSignUpConfirmPassword = findViewById(R.id.etSignUpConfirmPassword);
        btnSignUp.setOnClickListener(this);
        btnToLogin.setOnClickListener(this);
        countryCodePicker.setKeyboardAutoPopOnSearch(false);

        privacyCheck = findViewById(R.id.privacyCheckBox);
        privacyText = findViewById(R.id.privacyText);

        privacyCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.isChecked = isChecked;
        });

        if (isChecked)
            privacyCheck.setChecked(true);
        else
            privacyCheck.setChecked(false);


        setPrivacyText();

    }

    private void setPrivacyText() {

        String text = getResources().getString(R.string.SignUpAcceptTermsText);

        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                callTermsAndConditionApi();
            }
            @Override
            public void updateDrawState(TextPaint ds) {// override updateDrawState
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
        spannableString.setSpan(clickableSpan1, 13, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.signUpButtonColor)), 13, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyText.setText(spannableString);
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void callTermsAndConditionApi() {
        Common.showDialog(SignUp.this);
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<PrivacyPolicyModel> call = api.getPrivacyPolicy("privacy_policy_page");
        call.enqueue(new Callback<PrivacyPolicyModel>() {
            @Override
            public void onResponse(Call<PrivacyPolicyModel> call, Response<PrivacyPolicyModel> response) {
                Common.dissmissDialog();
                try {

                    if (response.isSuccessful()) {

                        String content = "";

                        if (SharedPreference.getSimpleString(SignUp.this, Constants.language).equals(Constants.french))
                            content = response.body().getData().getContentFr();
                        else
                            content = response.body().getData().getContent();


                        Intent intent = new Intent(SignUp.this, WebViewActivity.class);
                        intent.putExtra("content", content);
                        startActivity(intent);


                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SignUp.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<PrivacyPolicyModel> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.btnSignUp:
                validate();
                break;
            case R.id.btnToLogin:
                finish();
                break;
            default:
                break;
        }
    }

    //Validating the fields
    private void validate() {
        if (etSignUpName.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.signUp_error_enter_name), false);
            return;

        }
        if (etSignUpLastName.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.signUp_error_enterLastName), false);
            return;

        }

        if (etSignUpEmail.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.signUp_error_message_please_enter_email_address), false);
            return;
        }

        if (etPhoneNumber.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.signUp_error_please_enter_phone_number), false);
            return;
        }


        if (etSignUpPassword.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.signUp_error_message_enter_password), false);
            return;
        }


        if (etSignUpPassword.getText().toString().length() < 8) {
            showToast(this, getResources().getString(R.string.signUp_error_password_length), false);
            return;
        }

        if (etSignUpConfirmPassword.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.signUp_error_enter_password_confirmation), false);
            return;

        }

        if (etSignUpConfirmPassword.getText().toString().length() < 8) {
            showToast(this, getResources().getString(R.string.signUp_error_confirmation_password_length), false);
            return;
        }

        if (!etSignUpPassword.getText().toString().equals(etSignUpConfirmPassword.getText().toString())) {
            showToast(this, getResources().getString(R.string.signUp_error_password_not_same), false);
            return;
        }

        if (!isChecked) {
            showToast(this, getResources().getString(R.string.signUp_error_privacy), false);
            return;
        }

        Common.showDialog(SignUp.this);
        callRegisterApi();

    }


    private void callRegisterApi() {
        Api api = RetrofitClient.getClient().create(Api.class);
        RegisterRequest registerRequest = new RegisterRequest(etSignUpName.getText().toString(), etSignUpLastName.getText().toString(), etSignUpEmail.getText().toString()
                , etSignUpPassword.getText().toString(),
                etSignUpConfirmPassword.getText().toString(), countryCodePicker.getSelectedCountryCodeWithPlus(),
                etPhoneNumber.getText().toString(), countryCodePicker.getSelectedCountryNameCode());
        Call<ResponseBody> register = api.register(Splash.appLanguage, registerRequest);

        register.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.has("status")) {
                            boolean status = jsonObject.getBoolean("status");
                            if (status) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                showToast(SignUp.this, jsonObject.getString("message"), true);

                                if (data.has("phone")) {
                                    Intent i = new Intent(SignUp.this, OtpConfirmation.class);
                                    i.putExtra("phoneNo", data.getString("phone"));

                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        startActivity(i);
                                        finish();
                                    }, 2500);
                                } else if (data.has("email")) {
                                    Intent i = new Intent(SignUp.this, EmailConfirmation.class);
                                    i.putExtra("email", data.getString("email"));

                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        startActivity(i);
                                        finish();
                                    }, 2500);
                                }
                            } else
                                showToast(SignUp.this, jsonObject.getString("message"), false);
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SignUp.this, jsonObject.getString("message"), false);

                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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
