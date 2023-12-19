package com.vic.vicdriver.Views.Activities;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vic.vicdriver.Controllers.Helpers.FingerprintHandler;
import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Request.LoginRequest;
import com.vic.vicdriver.Models.Response.Login.Login;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.suke.widget.SwitchButton;

import org.json.JSONObject;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicdriver.Utils.Common.dissmissDialog;
import static com.vic.vicdriver.Utils.Common.showToast;
import static com.vic.vicdriver.Utils.SharedPreference.getSimpleString;


public class SignIn extends AppCompatActivity implements View.OnClickListener {


    private ConstraintLayout btnLoginCreateAccount, btnLogin;
    private TextView tvForgotPasswordSignIn, tvSignInEmailError, tvSignInPasswordError;
    private EditText etSignInEmail, etSignInPassword;
    private boolean isEmail, isPassword;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintHandler fingerprintHandler;
    private static final String FINGERPRINT_KEY = "MINBIO";
    private SwitchButton fingerPrintSwitch;
    private AlertDialog alertDialog;
    private TextView dontHaveAnAccount;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();
        if (getSimpleString(getApplicationContext(), Constants.isBiometric).equals("true")) {
            alertDialog = new AlertDialog.Builder(SignIn.this)
                    .setMessage(getResources().getString(R.string.touch_rear_sensor))
                    .setPositiveButton("Cancel", (dialog, which) -> alertDialog.dismiss()).show();
            fingerPrint();
        }

//        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
//        appSignatureHelper.getAppSignatures();

    }

    //Initializing the views
    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        dontHaveAnAccount = findViewById(R.id.dontHaveAnAccount);

        String text = "<font color='black'>" + getResources().getString(R.string.signInDontHaveAnAccount) + "</font>" + " " +
                "<font color='#50a936'>" + getResources().getString(R.string.signInRegisterNow) + "</font>";
        dontHaveAnAccount.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

        fingerPrintSwitch = findViewById(R.id.fingerPrintSwitch);
        etSignInEmail = findViewById(R.id.etSignInEmail);
        etSignInPassword = findViewById(R.id.etSignInPassword);
        tvSignInEmailError = findViewById(R.id.tvSignInEmailError);
        tvSignInPasswordError = findViewById(R.id.tvSignInPasswordError);
        btnLoginCreateAccount = findViewById(R.id.btnLoginCreateAccount);
        tvForgotPasswordSignIn = findViewById(R.id.tvForgotPasswordSignIn);
        ImageView ivFingerPrint = findViewById(R.id.ivFingerPrint);
        ivFingerPrint.setOnClickListener(view -> {
            alertDialog = new AlertDialog.Builder(SignIn.this)
                    .setMessage(getResources().getString(R.string.touch_rear_sensor))
                    .setPositiveButton("Cancel", (dialog, which) -> alertDialog.dismiss()).show();
        });
        btnLogin.setOnClickListener(this);
        tvForgotPasswordSignIn.setOnClickListener(this);
        btnLoginCreateAccount.setOnClickListener(this);
        if (getSimpleString(SignIn.this, Constants.isBiometric).equals("true"))
            fingerPrintSwitch.setChecked(true);
        fingerPrintSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked)
                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.isBiometric, "true");
                else
                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.isBiometric, "false");
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.btnLogin:
                validate();
                break;
            case R.id.btnLoginCreateAccount:
                i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);
                break;
            case R.id.tvForgotPasswordSignIn:
                i = new Intent(SignIn.this, ForgotPassword.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    //Validating the fields

    private void validate() {
        if (etSignInEmail.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.login_error_message_please_enter_email_address), false);
            return;
        }

        if (!Common.isValidEmailId(etSignInEmail.getText().toString())) {
            showToast(this, getResources().getString(R.string.login_error_message_invalidEmail), false);
            return;
        }

        if (etSignInPassword.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.login_error_message_enter_password), false);
            return;
        }

        callLoginApi();

    }
    //calling the login api

    private void callLoginApi() {
        Common.showDialog(SignIn.this);

        Api api = RetrofitClient.getClient().create(Api.class);
        LoginRequest loginRequest = new LoginRequest(etSignInEmail.getText().toString(), etSignInPassword.getText().toString());
        Call<Login> loginCall = api.login(Splash.appLanguage, loginRequest);

        loginCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        Login login = new Login(response.body().getData(), response.body().getStatus(),
                                response.body().getMessage());

                        if (login.getStatus()) {


                            if (login.getData() != null) {

                                if (login.getData().getIsPhoneVerified() == 1 && login.getData().getIsEmailVerified() == 1 && login.getData().getIsActive() == 1) {

                                    @SuppressLint("HardwareIds")
                                    String androidID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.userId, String.valueOf(login.getData().getId()));
                                    SharedPreference.saveSimpleString(SignIn.this, Constants.deviceId, androidID);
                                    getToken();


                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.isLoggedIn, "true");
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.isFirstLogin, "true");
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.fingerPrintUser, login.getData().getEmail());
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.countryCode, login.getData().getCountryCode());
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.userPhone, login.getData().getPhoneNumber());
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.userEmail, login.getData().getEmail());
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.userpassword, etSignInPassword.getText().toString());
                                    SharedPreference.saveSimpleString(getApplicationContext(), Constants.accessToken, login.getData().getAccess_token());
                                    Common.toJson(getApplicationContext(), login.getData());

                                    showToast(SignIn.this, login.getMessage(), true);

                                    SharedPreference.saveSimpleString(SignIn.this, Constants.language, login.getData().getLang());
                                    changeLocale(login.getData().getLang());
                                    if (login.getData().getIsProfileComplete() == 0) {

                                        Intent i = new Intent(SignIn.this, SettingsActivity.class);
                                        i.putExtra("from0Profile", true);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    } else if (login.getData().getIsProfileComplete() == 1) {
                                        Intent i = new Intent(SignIn.this, MyOrdersActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }

                                } else {
                                    int phone_verified = 0, email_verified = 0, is_active = 0;
                                    if (login.getData().getPhoneNumber() != null) {
                                        phone_verified = login.getData().getIsPhoneVerified();
                                        if (phone_verified == 0) {
                                            String phoneNo = login.getData().getPhoneNumber();
                                            Intent i = new Intent(SignIn.this, OtpConfirmation.class);
                                            i.putExtra("phoneNo", phoneNo);
                                            startActivity(i);
                                            return;
                                        }
                                    }
                                    if (login.getData().getEmail() != null) {
                                        email_verified = login.getData().getIsEmailVerified();
                                        if (email_verified == 0) {
                                            String email = login.getData().getEmail();
                                            Intent i = new Intent(SignIn.this, EmailConfirmation.class);
                                            i.putExtra("email", email);
                                            startActivity(i);
                                            return;
                                        }
                                    }
                                    if (login.getData().getIsActive() != null) {
                                        is_active = login.getData().getIsActive();
                                        if (is_active == 0) {
                                            showToast(SignIn.this, getResources().getString(R.string.blocked_by_minbio), false);
                                        }
                                    }
                                }
                            } else
                                showToast(SignIn.this, response.body().getMessage(), false);

                        } else
                            showToast(SignIn.this, login.getMessage(), false);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (!jsonObject.getJSONObject("data").toString().equals("{}")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            int phone_verified = 0, email_verified = 0, is_active = 0;
                            if (data.has("phone_number")) {
                                phone_verified = data.getInt("is_phone_verified");
                                if (phone_verified == 0) {
                                    String phoneNo = data.getString("phone");
                                    Intent i = new Intent(SignIn.this, OtpConfirmation.class);
                                    i.putExtra("phoneNo", phoneNo);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(i);
                                        }
                                    }, 1800);
                                }
                            } else if (data.has("email")) {
                                email_verified = data.getInt("is_email_verified");
                                if (email_verified == 0) {
                                    String email = data.getString("email");
                                    Intent i = new Intent(SignIn.this, EmailConfirmation.class);
                                    i.putExtra("email", email);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(i);
                                        }
                                    }, 1800);
                                }
                            } else if (data.has("is_active")) {
                                is_active = data.getInt("is_active");
                                if (is_active == 0) {
                                    showToast(SignIn.this, jsonObject.getString("message"), false);
                                }
                            }

                            String message = jsonObject.getString("message");
                            if (!message.isEmpty())
                                showToast(SignIn.this, message, false);

                        } else {
                            String message = jsonObject.getString("message");
                            if (!message.isEmpty())
                                showToast(SignIn.this, message, false);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                dissmissDialog();
                Log.d("Login Failure", "onFailure: " + t.getMessage());
            }
        });


    }


    private void changeLocale(String language) {
        Resources res = SignIn.this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fingerPrint() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintHandler = new FingerprintHandler(SignIn.this);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (fingerprintManager != null) {
                checkDeviceFingerprintSupport();
                generateFingerprintKeyStore();
                Cipher mCipher = instantiateCipher();
                if (mCipher != null) {
                    cryptoObject = new FingerprintManager.CryptoObject(mCipher);
                }

                fingerprintHandler.completeFingerAuthentication(fingerprintManager, cryptoObject);
            }
        }
    }


    //  ------------------ Finger print handler settings -------------------------------
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRestart() {

        if (fingerprintHandler != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintHandler.completeFingerAuthentication(fingerprintManager, cryptoObject);
            }
        super.onRestart();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkDeviceFingerprintSupport() {
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateFingerprintKeyStore() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            keyGenerator.init(new KeyGenParameterSpec.Builder(FINGERPRINT_KEY, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        try {
            keyGenerator.generateKey();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Cipher instantiateCipher() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(FINGERPRINT_KEY, null);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintHandler.StopListener();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    //Getting token from firebase
    private void getToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();

                        Log.d("Firebase is Working", "getToken: ");

                        SharedPreference.saveSimpleString(SignIn.this, Constants.fcmToken, token);
                        Common.fcmDevices(SignIn.this,
                                SharedPreference.getSimpleString(SignIn.this, Constants.deviceId),
                                SharedPreference.getSimpleString(SignIn.this, Constants.fcmToken),
                                "driver");

                    }

                });

    }

}
