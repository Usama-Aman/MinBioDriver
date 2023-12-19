package com.vic.vicdriver.Views.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vic.vicdriver.BuildConfig;
import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Response.Login.Data;
import com.vic.vicdriver.Models.Response.Login.Login;
import com.vic.vicdriver.Models.Response.Login.TruckDetail;
import com.vic.vicdriver.Models.Response.PrivacyPolicy.PrivacyPolicyModel;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.ApplicationClass;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.CustomTextWatcher;
import com.vic.vicdriver.Utils.FilePath;
import com.vic.vicdriver.Utils.SharedPreference;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.hbb20.CountryCodePicker;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static com.vic.vicdriver.Utils.Common.showToast;

//import okhttp3.MultipartBody;

public class SettingsActivity extends AppCompatActivity {
    EditText etAddress;
    //    private ImageView logout, back;
    private EditText etBussiness, etCompanyName, etFirstName, etLastName, etPhone, etEmail, etPassword, etPasswordConfirmation;
    private ConstraintLayout btnSave;
    private ConstraintLayout changeLanguage;
    private ImageView ivChangeLanguage;
    private ConstraintLayout locationConstraint;
    private AlertDialog alertDialog;
    private CountryCodePicker countryCodePicker;
    private Dialog dialog;
    private ConstraintLayout cartConstraintLayout, cartConstraint;
    LinearLayout linProfileImage;
    private TextView tvVersionNo;
    private ImageView imgLogout, imgCashByHand, imgCashByVisa, imgProfile, passwordTick, ivAddress,
            CpassswordTick, bussinessTick, siretTick, phoneTick, emailTick, firstNameTick, lastNameTick,
            proofOfInsuranceTick, proofOfLicenseTick, idCardBackTick, idCardFrontTick, proofofTruckTick,
            truckPlateTick, imgTruckPlate, imgProfileOfTruckId, imgIdCardFront, imgIdCardBack, imgProofOfLicenseId, imgProofOfInsurance;
    RelativeLayout imgBack, relTruckPlate, relProfileOfTruckId, relIdCardFront, relIdCardBack, relProofOfLicenseId, relProofOfInsurance;

    private Uri cameraUri;
    String pathProfileImage = "", pathTruckPlate = "", pathProfileOfTruckId = "", pathIdCardFront = "", pathIdCardBack = "", pathProofOfLicenseId = "", pathProofOfInsurance = "";
    String pathTruckPlateExtension = "", pathProfileOfTruckIdExtension = "", pathIdCardFrontExtension = "", pathIdCardBackExtension = "",
            pathProofOfLicenseIdExtension = "", pathProofOfInsuranceExtension = "";

    private boolean istruckPlate, isProofOfTruck, isLicence, isInsurance, isCardFront, isCardBack, isProfileSet;
    private Uri destinationUriCropper;
    private boolean from0ProfileApi = false;

    private TextView privacyText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m_setting_fragment);
        getActivityData();
        initViews();
    }

    public void getActivityData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("from0Profile"))
                from0ProfileApi = bundle.getBoolean("from0Profile");
        }
    }


    //Initializing the views
    private void initViews() {


        imgTruckPlate = findViewById(R.id.imgTruckPlate);
        imgProfileOfTruckId = findViewById(R.id.imgProofOfTruckId);
        imgIdCardFront = findViewById(R.id.imgIdCardFront);
        imgIdCardBack = findViewById(R.id.imgIdCardBack);
        imgProofOfLicenseId = findViewById(R.id.imgProofOfLicenseId);
        imgProofOfInsurance = findViewById(R.id.imgProofOfInsurance);
        imgLogout = findViewById(R.id.imgLogout);
        linProfileImage = findViewById(R.id.linProfileImage);
        imgProfile = findViewById(R.id.imgProfile);
        imgCashByHand = findViewById(R.id.imgCashByHand);
        imgCashByVisa = findViewById(R.id.imgCashByVisa);
        imgBack = findViewById(R.id.imgBack);
        proofOfInsuranceTick = findViewById(R.id.poiTick);
        proofOfLicenseTick = findViewById(R.id.polTick);
        idCardBackTick = findViewById(R.id.idCardBackTick);
        idCardFrontTick = findViewById(R.id.idCartFrontTick);
        proofOfLicenseTick = findViewById(R.id.polTick);
        truckPlateTick = findViewById(R.id.truckPlateTick);
        proofofTruckTick = findViewById(R.id.potTick);

        tvVersionNo = findViewById(R.id.tvVersionNo);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        tvVersionNo.setText(getResources().getString(R.string.version) + " " + versionName + "." + String.valueOf(versionCode));


        relTruckPlate = findViewById(R.id.relTruckPlate);
        relProfileOfTruckId = findViewById(R.id.relProfileOfTruckId);
        relIdCardFront = findViewById(R.id.relIdCardFront);
        relIdCardBack = findViewById(R.id.relIdCardBack);
        relProofOfLicenseId = findViewById(R.id.relProofOfLicenseId);
        relProofOfInsurance = findViewById(R.id.relProofOfInsurance);

        paymentType = "cash";


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data != null) {
                    if (data.getIsProfileComplete() == 1 || !from0ProfileApi) {
                        startActivity(new Intent(SettingsActivity.this, MyOrdersActivity.class));
                        finishAffinity();
                    }
                }

            }
        });


        relTruckPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setIntentForFile(001);
                Intent intent = new Intent(SettingsActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(true)
                        .setSuffixes("pdf", "png", "jpg", "jpeg")
                        .enableImageCapture(true)
                        .setMaxSelection(1)
                        .setSingleChoiceMode(true).setShowAudios(false)
                        .setShowVideos(false)
                        .setSkipZeroSizeFiles(true)
                        .setShowFiles(true)
                        .build());
                startActivityForResult(intent, 001);
            }
        });

        relProfileOfTruckId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setIntentForFile(002);

                Intent intent = new Intent(SettingsActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(true)
                        .setSuffixes("pdf", "png", "jpg", "jpeg")
                        .enableImageCapture(true)
                        .setMaxSelection(1)
                        .setSingleChoiceMode(true)
                        .setSkipZeroSizeFiles(true)
                        .setShowFiles(true).setShowAudios(false)
                        .setShowVideos(false)
                        .build());
                startActivityForResult(intent, 002);


            }
        });
        relIdCardFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setIntentForFile(003);
                Intent intent = new Intent(SettingsActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(true).setShowAudios(false)
                        .setShowVideos(false)
                        .setSuffixes("pdf", "png", "jpg", "jpeg")
                        .enableImageCapture(true)
                        .setMaxSelection(1)
                        .setSingleChoiceMode(true)
                        .setSkipZeroSizeFiles(true)
                        .setShowFiles(true)
                        .build());
                startActivityForResult(intent, 003);

            }
        });
        relIdCardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setIntentForFile(004);
                Intent intent = new Intent(SettingsActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(true)
                        .setSuffixes("pdf", "png", "jpg", "jpeg")
                        .enableImageCapture(true)
                        .setMaxSelection(1).setShowAudios(false)
                        .setShowVideos(false)
                        .setSingleChoiceMode(true)
                        .setSkipZeroSizeFiles(true)
                        .setShowFiles(true)
                        .build());
                startActivityForResult(intent, 004);

            }

        });
        relProofOfLicenseId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setIntentForFile(005);
                Intent intent = new Intent(SettingsActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(true)
                        .setSuffixes("pdf", "png", "jpg", "jpeg")
                        .enableImageCapture(true).setShowAudios(false)
                        .setShowVideos(false)
                        .setMaxSelection(1)
                        .setSingleChoiceMode(true)
                        .setSkipZeroSizeFiles(true)
                        .setShowFiles(true)
                        .build());
                startActivityForResult(intent, 005);

            }
        });
        relProofOfInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setIntentForFile(006);
                Intent intent = new Intent(SettingsActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true).setShowAudios(false)
                        .setShowVideos(false)
                        .setShowImages(true)
                        .setSuffixes("pdf", "png", "jpg", "jpeg")
                        .enableImageCapture(true)
                        .setMaxSelection(1)
                        .setSingleChoiceMode(true)
                        .setSkipZeroSizeFiles(true)
                        .setShowFiles(true)
                        .build());
                startActivityForResult(intent, 006);

            }
        });


        linProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgActiveImage = imgProfile;
                id = 1;
                pickImage();
            }
        });


        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialoge();
            }
        });

        privacyText = findViewById(R.id.privacyText);
        setPrivacyText();

        passwordTick = findViewById(R.id.ivPassword);
        phoneTick = findViewById(R.id.ivPhone);
        emailTick = findViewById(R.id.ivEmail);
        firstNameTick = findViewById(R.id.ivName);
        lastNameTick = findViewById(R.id.ivPreName);
        ivAddress = findViewById(R.id.ivAddress);
        bussinessTick = findViewById(R.id.ivBussiness);
        CpassswordTick = findViewById(R.id.ivPasswordConfirmation);

        btnSave = findViewById(R.id.btnSave);
        changeLanguage = findViewById(R.id.changeLanguageLayout);
        ivChangeLanguage = findViewById(R.id.ivChangeLanguage);

        if (SharedPreference.getSimpleString(SettingsActivity.this, Constants.language).equals("en")) {
            ivChangeLanguage.setImageResource(R.drawable.ic_fr);
        } else if (SharedPreference.getSimpleString(SettingsActivity.this, Constants.language).equals("fr")) {
            ivChangeLanguage.setImageResource(R.drawable.ic_en);
        }


        countryCodePicker = findViewById(R.id.settingsCCP);
        countryCodePicker.showFlag(true);
        countryCodePicker.setKeyboardAutoPopOnSearch(false);


        etBussiness = findViewById(R.id.etBussiness);
        Context ctx = getApplicationContext();
        etBussiness.addTextChangedListener(new CustomTextWatcher(bussinessTick, ctx).businesstextWatcher);


        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirmation = findViewById(R.id.etPasswordConfirmation);
        etPassword.addTextChangedListener(new CustomTextWatcher(passwordTick, CpassswordTick, etPasswordConfirmation, ctx).password);
        etPasswordConfirmation.addTextChangedListener(new CustomTextWatcher(CpassswordTick, etPassword, ctx).passwordMatcher);

        etPhone = findViewById(R.id.etPhone);
        etPhone.addTextChangedListener(new CustomTextWatcher(phoneTick, ctx).textWatcher);


        etEmail = findViewById(R.id.etEmail);
        etEmail.addTextChangedListener(new CustomTextWatcher(emailTick, ctx).emailValidatorWatcher);

        etFirstName = findViewById(R.id.etFirstName);
        etFirstName.addTextChangedListener(new CustomTextWatcher(firstNameTick, ctx).textWatcher);

        etLastName = findViewById(R.id.etLastName);
        etLastName.addTextChangedListener(new CustomTextWatcher(lastNameTick, ctx).textWatcher);

        etAddress = findViewById(R.id.etAddress);
        etAddress.addTextChangedListener(new CustomTextWatcher(ivAddress, ctx).textWatcher);


        findViewById(R.id.btnBankDetail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, BankDetailActivity.class));
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPreference.getSimpleString(SettingsActivity.this, Constants.language).equals("en")) {
                    callChangeLanguageApi(Constants.french);
                } else if (SharedPreference.getSimpleString(SettingsActivity.this, Constants.language).equals("fr")) {
                    callChangeLanguageApi(Constants.english);
                }

            }
        });
        imgCashByHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgCashByHand.setBackgroundResource(R.drawable.ic_cash_sl);
                imgCashByVisa.setBackgroundResource(R.drawable.visa_unsl);
                paymentType = "cash";
            }
        });
        imgCashByVisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgCashByHand.setBackgroundResource(R.drawable.ic_cash_un);
                imgCashByVisa.setBackgroundResource(R.drawable.visa);
                paymentType = "visa";
            }
        });

        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                Intent intent = new Intent(SettingsActivity.this, MapsActivity.class);
                if (data != null) {
                    if (data.getAddress() != null) {
                        bundle.putString("address", data.getAddress());
                    } else {
                        bundle.putString("address", "");
                    }
                    if (data.getLongitude() != null && data.getLatitude() != null) {
                        bundle.putDouble("lat", lat);
                        bundle.putDouble("lon", lon);
                    }
                }
                intent.putExtras(bundle);
                startActivityForResult(intent, 102);
            }
        });

        getUserDetails();


    }

    String paymentType;
    Data data;


    @Override
    public void onBackPressed() {
        if (data != null) {
            if (data.getIsProfileComplete() == 1 || !from0ProfileApi) {
                startActivity(new Intent(SettingsActivity.this, MyOrdersActivity.class));
                finishAffinity();
            }
        }
        super.onBackPressed();
    }

    private void setPrivacyText() {

        String text = getResources().getString(R.string.accountPrivacyText);

        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                callTermsAndConditionApi();
            }

            @Override
            public void updateDrawState(TextPaint ds) {// override updateDrawState
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };

        if (SharedPreference.getSimpleString(SettingsActivity.this, Constants.language).equals(Constants.french)) {
            spannableString.setSpan(clickableSpan1, 23, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.signUpButtonColor)), 23, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(clickableSpan1, 27, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.signUpButtonColor)), 27, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        privacyText.setText(spannableString);
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

    }


    private void callTermsAndConditionApi() {
        Common.showDialog(SettingsActivity.this);
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<PrivacyPolicyModel> call = api.getPrivacyPolicy("terms_and_conditions_page");
        call.enqueue(new Callback<PrivacyPolicyModel>() {
            @Override
            public void onResponse(Call<PrivacyPolicyModel> call, Response<PrivacyPolicyModel> response) {
                Common.dissmissDialog();
                try {

                    if (response.isSuccessful()) {

                        String content = "";

                        if (SharedPreference.getSimpleString(SettingsActivity.this, Constants.language).equals(Constants.french))
                            content = response.body().getData().getContentFr();
                        else
                            content = response.body().getData().getContent();


                        Intent intent = new Intent(SettingsActivity.this, WebViewActivity.class);
                        intent.putExtra("content", content);
                        startActivity(intent);


                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SettingsActivity.this, jsonObject.getString("message"), false);
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

    void getUserDetails() {

        data = Common.fromJson(getApplicationContext());
        if (data != null && data.getId() != null) {
            if (data.getFirstName() != null)
                etFirstName.setText(data.getFirstName().toString());
            if (data.getLastName() != null)
                etLastName.setText(data.getLastName().toString());
            if (data.getAddress() != null) {
                etAddress.setText(data.getAddress().toString());
                address = data.getAddress();
                if (data.getLatitude() != null && data.getLatitude() != "") {
                    lat = Double.parseDouble(data.getLatitude());
                }
                if (data.getLongitude() != null && data.getLongitude() != "") {
                    lon = Double.parseDouble(data.getLongitude());
                }
            }
            if (data.getCompanyName() != null)
                etBussiness.setText(data.getCompanyName().toString());

            if (data.getEmail() != null)
                etEmail.setText(data.getEmail().toString());

            if (data.getCountryCode() != null) {
                countryCodePicker.setCountryForPhoneCode(Integer.parseInt(data.getCountryCode().replace("+", "")));
            }
            if (data.getPhoneNumber() != null) {
                etPhone.setText(data.getPhoneNumber().toString());

            }

            if (data.getIsProfileComplete() != 1 || from0ProfileApi) {
                imgBack.setVisibility(GONE);
            } else if (from0ProfileApi) {
                imgBack.setVisibility(View.VISIBLE);
            }

            paymentType = data.getPaymentType();
            if (paymentType.equalsIgnoreCase("cash")) {
                imgCashByHand.setBackgroundResource(R.drawable.ic_cash_sl);
                imgCashByVisa.setBackgroundResource(R.drawable.visa_unsl);
            } else if (paymentType.equalsIgnoreCase("visa")) {
                imgCashByHand.setBackgroundResource(R.drawable.ic_cash_un);
                imgCashByVisa.setBackgroundResource(R.drawable.visa);
            } else {
                paymentType = "cash";
            }
            if (data.getTruckDetail() != null && data.getTruckDetail().getId() != null) {
                try {

                    TruckDetail truckDetail = data.getTruckDetail();
                    if (data.getProfileImage() != null && !data.getProfileImage().equals("")) {
                        Glide.with(getApplicationContext())
                                .applyDefaultRequestOptions(RequestOptions.circleCropTransform())//2
                                .load(data.getProfileImagePath()) //3
                                .into(imgProfile);
                        isProfileSet = true;
                    }


                    if (truckDetail.getTruckPlate().equals("") || truckDetail.getTruckPlate() == null) {
                        truckPlateTick.setVisibility(INVISIBLE);
                    } else {
                        truckPlateTick.setVisibility(View.VISIBLE);
                        istruckPlate = true;
                        pathTruckPlateExtension = truckDetail.getTruckPlatePath().substring(truckDetail.getTruckPlatePath().lastIndexOf("."));
                        if (pathTruckPlateExtension.equals(".png") || pathTruckPlateExtension.equals(".jpg") || pathTruckPlateExtension.equals(".jpeg")) {
                            Glide.with(SettingsActivity.this).load(truckDetail.getTruckPlatePath()).into(imgTruckPlate);
                        } else if (pathTruckPlateExtension.equals(".pdf")) {
                            Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgTruckPlate);
                        }

                    }

                    if (truckDetail.getProofOfTruckId().equals("") || truckDetail.getProofOfTruckId() == null) {
                        proofofTruckTick.setVisibility(INVISIBLE);
                    } else {
                        proofofTruckTick.setVisibility(View.VISIBLE);
                        isProofOfTruck = true;
                        pathProfileOfTruckIdExtension = truckDetail.getProofOfTruckIdPath().substring(truckDetail.getProofOfTruckIdPath().lastIndexOf("."));
                        if (pathProfileOfTruckIdExtension.equals(".png") || pathProfileOfTruckIdExtension.equals(".jpg") || pathProfileOfTruckIdExtension.equals(".jpeg")) {
                            Glide.with(SettingsActivity.this).load(truckDetail.getProofOfTruckIdPath()).into(imgProfileOfTruckId);
                        } else if (pathProfileOfTruckIdExtension.equals(".pdf")) {
                            Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgProfileOfTruckId);
                        }
                    }

                    if (truckDetail.getIdCardFront().equals("") || truckDetail.getIdCardFront() == null) {
                        idCardFrontTick.setVisibility(INVISIBLE);
                    } else {
                        idCardFrontTick.setVisibility(View.VISIBLE);
                        isCardFront = true;
                        pathIdCardFrontExtension = truckDetail.getIdCardFront().substring(truckDetail.getIdCardFront().lastIndexOf("."));
                        if (pathIdCardFrontExtension.equals(".png") || pathIdCardFrontExtension.equals(".jpg") || pathIdCardFrontExtension.equals(".jpeg")) {
                            Glide.with(SettingsActivity.this).load(truckDetail.getIdCardFrontPath()).into(imgIdCardFront);
                        } else if (pathIdCardFrontExtension.equals(".pdf")) {
                            Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgIdCardFront);
                        }
                    }

                    if (truckDetail.getIdCardBack().equals("") || truckDetail.getIdCardBack() == null) {
                        idCardBackTick.setVisibility(INVISIBLE);
                    } else {
                        idCardBackTick.setVisibility(View.VISIBLE);
                        isCardBack = true;
                        pathIdCardBackExtension = truckDetail.getIdCardBack().substring(truckDetail.getIdCardBack().lastIndexOf("."));
                        if (pathIdCardBackExtension.equals(".png") || pathIdCardBackExtension.equals(".jpg") || pathIdCardBackExtension.equals(".jpeg")) {
                            Glide.with(SettingsActivity.this).load(truckDetail.getIdCardBackPath()).into(imgIdCardBack);
                        } else if (pathIdCardBackExtension.equals(".pdf")) {
                            Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgIdCardBack);
                        }
                    }

                    if (truckDetail.getProofOfLicenseId().equals("") || truckDetail.getProofOfLicenseId() == null) {
                        proofOfLicenseTick.setVisibility(INVISIBLE);
                    } else {
                        isLicence = true;
                        proofOfLicenseTick.setVisibility(View.VISIBLE);
                        pathProofOfLicenseIdExtension = truckDetail.getProofOfLicenseId().substring(truckDetail.getProofOfLicenseId().lastIndexOf("."));
                        if (pathProofOfLicenseIdExtension.equals(".png") || pathProofOfLicenseIdExtension.equals(".jpg") || pathProofOfLicenseIdExtension.equals(".jpeg")) {
                            Glide.with(SettingsActivity.this).load(truckDetail.getProofOfLicenseIdPath()).into(imgProofOfLicenseId);
                        } else if (pathProofOfLicenseIdExtension.equals(".pdf")) {
                            Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgProofOfLicenseId);
                        }
                    }

                    if (truckDetail.getProofOfInsurance().equals("") || truckDetail.getProofOfInsurance() == null) {
                        proofOfInsuranceTick.setVisibility(INVISIBLE);
                    } else {
                        proofOfInsuranceTick.setVisibility(View.VISIBLE);
                        isInsurance = true;
                        pathProofOfInsuranceExtension = truckDetail.getProofOfInsurancePath().substring(truckDetail.getProofOfInsurancePath().lastIndexOf("."));
                        if (pathProofOfInsuranceExtension.equals(".png") || pathProofOfInsuranceExtension.equals(".jpg") || pathProofOfInsuranceExtension.equals(".jpeg")) {
                            Glide.with(SettingsActivity.this).load(truckDetail.getProofOfInsurancePath()).into(imgProofOfInsurance);
                        } else if (pathProofOfInsuranceExtension.equals(".pdf")) {
                            Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgProofOfInsurance);
                        }


                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }


        }

    }


    ImageView imgActiveImage;
    File profileImgFile, truckPlateImgFile, proofOfTruckIdImgFile, idCardFrontImgFile, idCardBackImgFile, lisenceImgFile, insuranceImgFile;
    int id = 0;
    String address;
    double lat = 0, lon = 0;

    void pickImage() {

        ImagePicker.Companion.with(this)
                .crop(1f, 1f)                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start(101);
    }


    private void validate() {
        if (etLastName.getText().toString().isEmpty()) {
            showToast((this), getResources().getString(R.string.account_error_enter_pre_name), false);
            return;
        }
        if (etFirstName.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.account_error_enter_name), false);
            return;
        }
        if (etPhone.getText().toString().isEmpty()) {

            showToast(this, getResources().getString(R.string.account_error_please_enter_phone_number), false);

            return;
        }
        if (etAddress.getText().toString().isEmpty()) {

            showToast(this, getResources().getString(R.string.account_error_enter_address), false);

            return;
        }

        if (etPassword.getText().toString().length() > 0 && etPassword.getText().toString().length() < 8) {
            showToast(this, getResources().getString(R.string.account_error_password_length), false);
            return;
        }

        if (etPasswordConfirmation.getText().toString().length() > 0 && etPasswordConfirmation.getText().toString().length() < 8) {
            showToast(this, getResources().getString(R.string.account_error_confirmation_password_length), false);
            return;
        }

        if (!etPassword.getText().toString().equals(etPasswordConfirmation.getText().toString())) {
            showToast(SettingsActivity.this, getResources().getString(R.string.account_error_password_not_same), false);
            return;
        }

        if (etEmail.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.account_error_message_please_enter_email_address), false);

            return;
        }
        if (!Common.isValidEmailId(etEmail.getText().toString())) {

            showToast(this, getResources().getString(R.string.account_error_message_invalidEmail), false);
            return;
        }

        if (!isProfileSet) {
            showToast(this, getResources().getString(R.string.account_error_please_select_profile), false);
            return;
        }

        if (!istruckPlate) {
            showToast(this, getResources().getString(R.string.account_error_please_select_truck_plate), false);
            return;
        }
        if (!isProofOfTruck) {
            showToast(this, getResources().getString(R.string.account_error_please_select_truck_id), false);
            return;
        }
        if (!isCardFront) {
            showToast(this, getResources().getString(R.string.account_error_please_select_id_card_front), false);
            return;
        }
        if (!isCardBack) {
            showToast(this, getResources().getString(R.string.account_error_please_select_id_card_back), false);
            return;
        }
        if (!isLicence) {
            showToast(this, getResources().getString(R.string.account_error_please_select_license), false);
            return;
        }
        if (!isInsurance) {
            showToast(this, getResources().getString(R.string.account_error_please_select_insurance), false);
            return;
        }

        SaveProfile();
    }


    String TAG = "Setting";

    void SaveProfile() {
        Common.showDialog(SettingsActivity.this);

        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken);

        token = "Bearer " + token;
        RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), etFirstName.getText().toString());
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), etLastName.getText().toString());
        RequestBody phone_number = RequestBody.create(MediaType.parse("text/plain"), etPhone.getText().toString());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), etAddress.getText().toString());
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), lat + "");
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), lon + "");
        RequestBody company_name = RequestBody.create(MediaType.parse("text/plain"), etBussiness.getText().toString());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), etPassword.getText().toString());
        RequestBody confirm_password = RequestBody.create(MediaType.parse("text/plain"), etPasswordConfirmation.getText().toString());
        RequestBody country_code = RequestBody.create(MediaType.parse("text/plain"), countryCodePicker.getSelectedCountryCode());
        RequestBody iso_code = RequestBody.create(MediaType.parse("text/plain"), countryCodePicker.getSelectedCountryNameCode());
        RequestBody payment_type = RequestBody.create(MediaType.parse("text/plain"), paymentType);
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), etEmail.getText().toString());
        MultipartBody.Part profileBodyPart = null, truckIdBodyPart = null, trucPlateBodyPart = null, idCardFrontBodyPart = null,
                idCardBackBodyPart = null, licenseBodyPart = null, insuranceBodyPart = null;

        RequestBody profileReq = null;
        File file = null;

        if (profileImgFile != null) {
            profileReq = RequestBody.create(MediaType.parse("image/jpeg"), profileImgFile);
            profileBodyPart = MultipartBody.Part.createFormData("profile_image", profileImgFile.getName(), profileReq);
        }

        if (!pathTruckPlate.equals("")) {
            file = new File(pathTruckPlate);
            if (pathTruckPlateExtension.equals(".png") || pathTruckPlateExtension.equals(".jpg") || pathTruckPlateExtension.equals(".jpeg"))
                profileReq = RequestBody.create(MediaType.parse("image/*"), file);
            else if (pathTruckPlateExtension.equals(".pdf"))
                profileReq = RequestBody.create(MediaType.parse("application/pdf"), file);
            trucPlateBodyPart = MultipartBody.Part.createFormData("truck_plate", file.getName(), profileReq);
        }

        if (!pathProfileOfTruckId.equals("")) {
            file = new File(pathProfileOfTruckId);
            if (pathProfileOfTruckIdExtension.equals(".png") || pathProfileOfTruckIdExtension.equals(".jpg") || pathProfileOfTruckIdExtension.equals(".jpeg"))
                profileReq = RequestBody.create(MediaType.parse("image/*"), file);
            else if (pathProfileOfTruckIdExtension.equals(".pdf"))
                profileReq = RequestBody.create(MediaType.parse("application/pdf"), file);
            truckIdBodyPart = MultipartBody.Part.createFormData("proof_of_truck_id", file.getName(), profileReq);
        }

        if (!pathIdCardFront.equals("")) {
            file = new File(pathIdCardFront);
            if (pathIdCardFrontExtension.equals(".png") || pathIdCardFrontExtension.equals(".jpg") || pathIdCardFrontExtension.equals(".jpeg"))
                profileReq = RequestBody.create(MediaType.parse("image/*"), file);
            else if (pathIdCardFrontExtension.equals(".pdf"))
                profileReq = RequestBody.create(MediaType.parse("application/pdf"), file);
            idCardFrontBodyPart = MultipartBody.Part.createFormData("id_card_front", file.getName(), profileReq);
        }

        if (!pathIdCardBack.equals("")) {
            file = new File(pathIdCardBack);
            if (pathIdCardBackExtension.equals(".png") || pathIdCardBackExtension.equals(".jpg") || pathIdCardBackExtension.equals(".jpeg"))
                profileReq = RequestBody.create(MediaType.parse("image/*"), file);
            else if (pathIdCardBackExtension.equals(".pdf"))
                profileReq = RequestBody.create(MediaType.parse("application/pdf"), file);
            idCardBackBodyPart = MultipartBody.Part.createFormData("id_card_back", file.getName(), profileReq);
        }

        if (!pathProofOfLicenseId.equals("")) {
            file = new File(pathProofOfLicenseId);
            if (pathProofOfLicenseIdExtension.equals(".png") || pathProofOfLicenseIdExtension.equals(".jpg") || pathProofOfLicenseIdExtension.equals(".jpeg"))
                profileReq = RequestBody.create(MediaType.parse("image/*"), file);
            else if (pathProofOfLicenseIdExtension.equals(".pdf"))
                profileReq = RequestBody.create(MediaType.parse("application/pdf"), file);
            licenseBodyPart = MultipartBody.Part.createFormData("proof_of_license_id", file.getName(), profileReq);
        }

        if (!pathProofOfInsurance.equals("")) {
            file = new File(pathProofOfInsurance);
            if (pathProofOfInsuranceExtension.equals(".png") || pathProofOfInsuranceExtension.equals(".jpg") || pathProofOfInsuranceExtension.equals(".jpeg"))
                profileReq = RequestBody.create(MediaType.parse("image/*"), file);
            else if (pathProofOfInsuranceExtension.equals(".pdf"))
                profileReq = RequestBody.create(MediaType.parse("application/pdf"), file);
            insuranceBodyPart = MultipartBody.Part.createFormData("proof_of_insurance", file.getName(), profileReq);
        }


        Call<Login> register = api.updateProfile(token, first_name, last_name, phone_number, address, latitude, longitude, company_name,
                password, confirm_password, profileBodyPart, trucPlateBodyPart, truckIdBodyPart, idCardFrontBodyPart, idCardBackBodyPart, licenseBodyPart,
                insuranceBodyPart, country_code, payment_type, iso_code, email);
        register.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                Common.dissmissDialog();
                System.out.println("Code   " + response.code());
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus()) {

                            showToast(SettingsActivity.this, response.body().getMessage(), true);
                            Common.toJson(getApplicationContext(), response.body().getData());


                            if (response.body().getData().getIsProfileComplete() == 1) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Common.toJson(getApplicationContext(), response.body().getData());


                                        if (response.body().getData().getIsProfileComplete() == 1) {
                                            startActivity(new Intent(SettingsActivity.this, MyOrdersActivity.class));
                                            finish();
                                        }

                                    }
                                }, 1000);
                            }


                        } else
                            showToast(SettingsActivity.this, response.body().getMessage(), false);
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SettingsActivity.this, jsonObject.getString("message") + "", false);

                    }
                } catch (Exception e) {
                    Common.dissmissDialog();
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    void showExitDialoge() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage(getString(R.string.logout_app));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

//                if (isServiceRunning(GPSService.class))
//                    stopService(new Intent(SettingsActivity.this, GPSService.class));
                callLogoutApi();
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

    //calling the change language api

    private void callChangeLanguageApi(String language) {

        Common.showDialog(SettingsActivity.this);

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> responseBodyCall = api.changeLanguage("Bearer " +
                SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken), language);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
//                        showToast(SettingsActivity.this, jsonObject.getString("message"), true);
                        changeLanguage(language);
                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SettingsActivity.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    //changing the language of the app'
    private void changeLanguage(String language) {

        SharedPreference.saveSimpleString(getApplicationContext(), Constants.language, language);
        Splash.appLanguage = language;

        ApplicationClass applicationClass = (ApplicationClass) getApplication();
        applicationClass.changeLocale(getApplication());

        Intent intent = getIntent();
        startActivity(new Intent(SettingsActivity.this, Splash.class));

        finish();
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

    private void callLogoutApi() {

        Common.showDialog(SettingsActivity.this);

        Log.d(TAG, "Token: " + SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken));
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> logout = api.logout("Bearer " + SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken),
                SharedPreference.getSimpleString(getApplicationContext(), Constants.deviceId), "driver");

        logout.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        showToast(SettingsActivity.this, jsonObject.getString("message"), true);

                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();

                        SharedPreference.saveSimpleString(getApplicationContext(), Constants.isLoggedIn, "false");
                        Intent i = new Intent(getApplicationContext(), SignIn.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finishAffinity();

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(SettingsActivity.this, jsonObject.getString("message"), false);
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

    private void setIntentForFile(int code) {
        checkPermission(code);
    }

    // Function to check and request permission
    public void checkPermission(int code) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(SettingsActivity.this, Constants.PERMISSIONS_STORAGE, code);
        } else {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    getResources().getString(R.string.app_name));
            if (!imageStorageDir.exists()) {
                imageStorageDir.mkdirs();
            }
            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            cameraUri = Uri.fromFile(file);

            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);


            Intent chooseFile = new Intent();
            chooseFile.setType("*/*");
            String[] mimetypes = {"image/*", "application/pdf"};
            chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            chooseFile.setAction(Intent.ACTION_GET_CONTENT);
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            chooseFile.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
            startActivityForResult(chooseFile, code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            checkPermission(requestCode);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 101) {
                Uri fileUri = data.getData();
                if (fileUri != null)
                    imgActiveImage.setImageURI(fileUri);
                File file = ImagePicker.Companion.getFile(data);
                if (file != null) {
                    if (id == 1) {
                        profileImgFile = file;
                    }
                }
                pathProfileImage = FilePath.getPath(SettingsActivity.this, fileUri);
                isProfileSet = true;
            }
            if (requestCode == 001) {
                istruckPlate = true;
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files.size() > 0) {
                    Uri fileUri = files.get(0).getUri();

                    pathTruckPlate = files.get(0).getPath();
                    pathTruckPlateExtension = pathTruckPlate.substring(pathTruckPlate.lastIndexOf("."));
                    if (pathTruckPlateExtension.equalsIgnoreCase(".png") || pathTruckPlateExtension.equalsIgnoreCase(".jpg") || pathTruckPlateExtension.equalsIgnoreCase(".jpeg")) {
                        startCrop(fileUri, 201);
                    } else if (pathTruckPlateExtension.equalsIgnoreCase(".pdf")) {
                        truckPlateTick.setVisibility(View.VISIBLE);
                        Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgTruckPlate);
                    }
                }
            } else if (requestCode == 002) {
                isProofOfTruck = true;
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files.size() > 0) {

                    Uri fileUri = files.get(0).getUri();

                    pathProfileOfTruckId = files.get(0).getPath();
                    pathProfileOfTruckIdExtension = pathProfileOfTruckId.substring(pathProfileOfTruckId.lastIndexOf("."));
                    if (pathProfileOfTruckIdExtension.equalsIgnoreCase(".png") || pathProfileOfTruckIdExtension.equalsIgnoreCase(".jpg") || pathProfileOfTruckIdExtension.equalsIgnoreCase(".jpeg")) {
                        startCrop(fileUri, 202);
                    } else if (pathTruckPlateExtension.equalsIgnoreCase(".pdf")) {
                        proofofTruckTick.setVisibility(View.VISIBLE);
                        Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgProfileOfTruckId);
                    }
                }

            } else if (requestCode == 003) {
                isCardFront = true;
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files.size() > 0) {

                    Uri fileUri = files.get(0).getUri();

                    pathIdCardFront = files.get(0).getPath();
                    pathIdCardFrontExtension = pathIdCardFront.substring(pathIdCardFront.lastIndexOf("."));
                    if (pathIdCardFrontExtension.equalsIgnoreCase(".png") || pathIdCardFrontExtension.equalsIgnoreCase(".jpg") || pathIdCardFrontExtension.equalsIgnoreCase(".jpeg")) {
                        startCrop(fileUri, 203);
                    } else if (pathTruckPlateExtension.equalsIgnoreCase(".pdf")) {
                        idCardFrontTick.setVisibility(View.VISIBLE);
                        Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgIdCardFront);
                    }
                }
            } else if (requestCode == 004) {
                isCardBack = true;
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files.size() > 0) {

                    Uri fileUri = files.get(0).getUri();

                    pathIdCardBack = files.get(0).getPath();
                    pathIdCardBackExtension = pathIdCardBack.substring(pathIdCardBack.lastIndexOf("."));
                    if (pathIdCardBackExtension.equalsIgnoreCase(".png") || pathIdCardBackExtension.equalsIgnoreCase(".jpg") || pathIdCardBackExtension.equalsIgnoreCase(".jpeg")) {
                        startCrop(fileUri, 204);
                    } else if (pathIdCardBackExtension.equalsIgnoreCase(".pdf")) {
                        idCardBackTick.setVisibility(View.VISIBLE);
                        Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgIdCardBack);
                    }
                }
            } else if (requestCode == 005) {
                isLicence = true;
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files.size() > 0) {
                    Uri fileUri = files.get(0).getUri();

                    pathProofOfLicenseId = files.get(0).getPath();
                    pathProofOfLicenseIdExtension = pathProofOfLicenseId.substring(pathProofOfLicenseId.lastIndexOf("."));
                    if (pathProofOfLicenseIdExtension.equalsIgnoreCase(".png") || pathProofOfLicenseIdExtension.equalsIgnoreCase(".jpg") || pathProofOfLicenseIdExtension.equalsIgnoreCase(".jpeg")) {
                        startCrop(fileUri, 205);
                    } else if (pathProofOfLicenseIdExtension.equalsIgnoreCase(".pdf")) {
                        proofOfLicenseTick.setVisibility(View.VISIBLE);
                        Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgProofOfLicenseId);
                    }
                }
            } else if (requestCode == 006) {
                isInsurance = true;
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files.size() > 0) {

                    Uri fileUri = files.get(0).getUri();

                    pathProofOfInsurance = files.get(0).getPath();
                    pathProofOfInsuranceExtension = pathProofOfInsurance.substring(pathProofOfInsurance.lastIndexOf("."));
                    if (pathProofOfInsuranceExtension.equalsIgnoreCase(".png") || pathProofOfInsuranceExtension.equalsIgnoreCase(".jpg") || pathProofOfInsuranceExtension.equalsIgnoreCase(".jpeg")) {
                        startCrop(fileUri, 206);
                    } else if (pathProofOfInsuranceExtension.equalsIgnoreCase(".pdf")) {
                        proofOfInsuranceTick.setVisibility(View.VISIBLE);
                        Glide.with(SettingsActivity.this).load(R.drawable.ic_pdf).into(imgProofOfInsurance);
                    }
                }
            } else if (resultCode == Activity.RESULT_OK && requestCode == 102) {
                if (data != null) {
                    address = data.getStringExtra("address");
                    lat = data.getDoubleExtra("lat", 0.0);
                    lon = data.getDoubleExtra("lon", 0.0);
                    etAddress.setText(address.toString());
                }
            } else if (requestCode == 201) {
                truckPlateTick.setVisibility(View.VISIBLE);
                Uri cropperUri = UCrop.getOutput(data);
                pathTruckPlate = FilePath.getPath(SettingsActivity.this, cropperUri);
                pathTruckPlateExtension = pathTruckPlate.substring(pathTruckPlate.lastIndexOf("."));

                Glide.with(SettingsActivity.this).load(pathTruckPlate).into(imgTruckPlate);
            } else if (requestCode == 202) {
                proofofTruckTick.setVisibility(View.VISIBLE);
                Uri cropperUri = UCrop.getOutput(data);
                pathProfileOfTruckId = FilePath.getPath(SettingsActivity.this, cropperUri);
                pathProfileOfTruckIdExtension = pathProfileOfTruckId.substring(pathProfileOfTruckId.lastIndexOf("."));

                Glide.with(SettingsActivity.this).load(pathProfileOfTruckId).into(imgProfileOfTruckId);
            } else if (requestCode == 203) {
                idCardFrontTick.setVisibility(View.VISIBLE);
                Uri cropperUri = UCrop.getOutput(data);
                pathIdCardFront = FilePath.getPath(SettingsActivity.this, cropperUri);
                pathIdCardFrontExtension = pathIdCardFront.substring(pathIdCardFront.lastIndexOf("."));

                Glide.with(SettingsActivity.this).load(pathIdCardFront).into(imgIdCardFront);
            } else if (requestCode == 204) {
                idCardBackTick.setVisibility(View.VISIBLE);
                Uri cropperUri = UCrop.getOutput(data);
                pathIdCardBack = FilePath.getPath(SettingsActivity.this, cropperUri);
                pathIdCardBackExtension = pathIdCardBack.substring(pathIdCardBack.lastIndexOf("."));

                Glide.with(SettingsActivity.this).load(pathIdCardBack).into(imgIdCardBack);
            } else if (requestCode == 205) {
                proofOfLicenseTick.setVisibility(View.VISIBLE);
                Uri cropperUri = UCrop.getOutput(data);
                pathProofOfLicenseId = FilePath.getPath(SettingsActivity.this, cropperUri);
                pathProofOfLicenseIdExtension = pathProofOfLicenseId.substring(pathProofOfLicenseId.lastIndexOf("."));

                Glide.with(SettingsActivity.this).load(pathProofOfLicenseId).into(imgProofOfLicenseId);
            } else if (requestCode == 206) {
                proofOfInsuranceTick.setVisibility(View.VISIBLE);
                Uri cropperUri = UCrop.getOutput(data);
                pathProofOfInsurance = FilePath.getPath(SettingsActivity.this, cropperUri);
                pathProofOfInsuranceExtension = pathProofOfInsurance.substring(pathProofOfInsurance.lastIndexOf("."));

                Glide.with(SettingsActivity.this).load(pathProofOfInsurance).into(imgProofOfInsurance);
            }
        }
    }


    private void startCrop(Uri sourceUri, int requestCode) {

        File file = new File(SettingsActivity.this.getCacheDir(), "IMG_" + System.currentTimeMillis() + ".jpg");
        destinationUriCropper = Uri.fromFile(file);

        startActivityForResult(UCrop.of(sourceUri, destinationUriCropper)
                .withMaxResultSize(1000, 1000)
                .withAspectRatio(5f, 5f)
                .getIntent(SettingsActivity.this), requestCode);

    }

}
