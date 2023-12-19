package com.vic.vicdriver.Controllers.Helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Request.LoginRequest;
import com.vic.vicdriver.Models.Response.Login.Login;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;
import com.vic.vicdriver.Views.Activities.EmailConfirmation;
import com.vic.vicdriver.Views.Activities.MyOrdersActivity;
import com.vic.vicdriver.Views.Activities.OtpConfirmation;
import com.vic.vicdriver.Views.Activities.SettingsActivity;
import com.vic.vicdriver.Views.Activities.Splash;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicdriver.Utils.Common.dissmissDialog;
import static com.vic.vicdriver.Utils.Common.showToast;

//import com.elementarylogics.minbio_driver.Views.Activities.CheckingAcitivity;


@SuppressLint("NewApi")
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private static final String TAG = FingerprintHandler.class.getSimpleName();
    private Context context;
    private CancellationSignal cancellationSignal;


    public FingerprintHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        Common.hideKeyboard((AppCompatActivity) context);
        if (SharedPreference.getSimpleString(context, Constants.isFirstLogin).equals("true")) {
            Common.showDialog(context);
            callLoginApi();
        } else
            showToast((AppCompatActivity) context, context.getResources().getString(R.string.please_login_at_least_one_time), false);

    }

    //Calling login api on finger print
    private void callLoginApi() {

        Api api = RetrofitClient.getClient().create(Api.class);
        LoginRequest loginRequest = new LoginRequest(SharedPreference.getSimpleString(context, Constants.fingerPrintUser),
                SharedPreference.getSimpleString(context, Constants.userpassword));
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
                                    String androidID = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                                    SharedPreference.saveSimpleString(context, Constants.userId, String.valueOf(login.getData().getId()));
                                    SharedPreference.saveSimpleString(context, Constants.deviceId, androidID);
                                    getToken();


                                    SharedPreference.saveSimpleString(context, Constants.isLoggedIn, "true");
                                    SharedPreference.saveSimpleString(context, Constants.isFirstLogin, "true");
                                    SharedPreference.saveSimpleString(context, Constants.fingerPrintUser, login.getData().getEmail());
                                    SharedPreference.saveSimpleString(context, Constants.countryCode, login.getData().getCountryCode());
                                    SharedPreference.saveSimpleString(context, Constants.userPhone, login.getData().getPhoneNumber());
                                    SharedPreference.saveSimpleString(context, Constants.accessToken, login.getData().getAccess_token());
//                                    getSettingsApi();
                                    Common.toJson(context, login.getData());

                                    showToast((AppCompatActivity) context, login.getMessage(), true);

                                    SharedPreference.saveSimpleString(context, Constants.language, login.getData().getLang());
                                    changeLocale(login.getData().getLang());
                                    if (login.getData().getIsProfileComplete() == 0) {

                                        Intent i = new Intent(context, SettingsActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        context.startActivity(i);
                                    } else if (login.getData().getIsProfileComplete() == 1) {

                                        Intent i = new Intent(context, MyOrdersActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        context.startActivity(i);
                                    }

                                } else {
                                    int phone_verified = 0, email_verified = 0, is_active = 0;
                                    if (login.getData().getPhoneNumber() != null) {
                                        phone_verified = login.getData().getIsPhoneVerified();
                                        if (phone_verified == 0) {
                                            String phoneNo = login.getData().getPhoneNumber();
                                            Intent i = new Intent(context, OtpConfirmation.class);
                                            i.putExtra("phoneNo", phoneNo);
                                            context.startActivity(i);
                                            return;
                                        }
                                    }
                                    if (login.getData().getEmail() != null) {
                                        email_verified = login.getData().getIsEmailVerified();
                                        if (email_verified == 0) {
                                            String email = login.getData().getEmail();
                                            Intent i = new Intent(context, EmailConfirmation.class);
                                            i.putExtra("email", email);
                                            context.startActivity(i);
                                            return;
                                        }
                                    }
                                    if (login.getData().getIsActive() != null) {
                                        is_active = login.getData().getIsActive();
                                        if (is_active == 0) {
                                            showToast((AppCompatActivity) context, context.getResources().getString(R.string.blocked_by_minbio), false);
                                        }
                                    }
                                }
                            } else
                                showToast((AppCompatActivity) context, response.body().getMessage(), false);

                        } else
                            showToast((AppCompatActivity) context, login.getMessage(), false);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (!jsonObject.getJSONObject("data").toString().equals("{}")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            int phone_verified = 0, email_verified = 0, is_active = 0;
                            if (data.has("phone_number")) {
                                phone_verified = data.getInt("phone_verified");
                                if (phone_verified == 0) {
                                    String phoneNo = data.getString("phone");
                                    Intent i = new Intent(context, OtpConfirmation.class);
                                    i.putExtra("phoneNo", phoneNo);
                                    context.startActivity(i);
                                }
                            } else if (data.has("email")) {
                                email_verified = data.getInt("is_email_verified");
                                if (email_verified == 0) {
                                    String email = data.getString("email");
                                    Intent i = new Intent(context, EmailConfirmation.class);
                                    i.putExtra("email", email);
                                    context.startActivity(i);
                                }
                            } else if (data.has("is_active")) {
                                is_active = data.getInt("is_active");
                                if (is_active == 0) {
                                    showToast((AppCompatActivity) context, context.getResources().getString(R.string.blocked_by_minbio), false);
                                }
                            }

                            String message = jsonObject.getString("message");
                            if (!message.isEmpty())
                                showToast((AppCompatActivity) context, message, false);

                        } else {
                            String message = jsonObject.getString("message");
                            if (!message.isEmpty())
                                showToast((AppCompatActivity) context, message, false);
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
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);
    }

    public void StopListener() {
        try {
            if (cancellationSignal != null)
                cancellationSignal.cancel();
            cancellationSignal = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
    }

    public void completeFingerAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            cancellationSignal = new CancellationSignal();
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        } catch (SecurityException ex) {
            Log.d(TAG, "An error occurred:\n" + ex.getMessage());
        } catch (Exception ex) {
            Log.d(TAG, "An error occurred\n" + ex.getMessage());
        }
    }

    private void getToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        SharedPreference.saveSimpleString(context, Constants.fcmToken, token);
                        Common.fcmDevices(context,
                                SharedPreference.getSimpleString(context, Constants.deviceId),
                                SharedPreference.getSimpleString(context, Constants.fcmToken),
                                "driver");

                    }

                });

    }

}
