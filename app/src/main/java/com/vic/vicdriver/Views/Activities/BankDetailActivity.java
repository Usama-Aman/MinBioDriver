package com.vic.vicdriver.Views.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Response.Login.Data;
import com.vic.vicdriver.Models.Response.bank_detail.BankDetailModel;
import com.vic.vicdriver.Models.Response.bank_detail.BankDetailResponse;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.CustomTextWatcher;
import com.vic.vicdriver.Utils.FilePath;
import com.vic.vicdriver.Utils.SharedPreference;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicdriver.Utils.Common.showToast;

public class BankDetailActivity extends AppCompatActivity {

    ImageView ivAccountName, ivSwiftNumber, ivIBAN;
    EditText etAccountName, etSwiftNumber, etIBAN;
    ConstraintLayout btnSave;
    LinearLayout imgUploadBankDetailFile;
    ImageView imgBankDetail;
    RelativeLayout imgBack;
    Context ctx;

    private Uri destinationUriCropper;
    private String bankImagePath = "", bankImageExtension = "";
    private boolean isPathUrl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);

        // image tick and image error

        ivAccountName = findViewById(R.id.ivAccountName);
        ivSwiftNumber = findViewById(R.id.ivSwiftNumber);
        ivIBAN = findViewById(R.id.ivIBAN);

        // edittext
        etAccountName = findViewById(R.id.etAccountName);
        etSwiftNumber = findViewById(R.id.etSwiftNumber);
        etIBAN = findViewById(R.id.etIBAN);

        // button
        btnSave = findViewById(R.id.btnSave);
        // upload bank file
        imgUploadBankDetailFile = findViewById(R.id.imgUploadBankDetailFile);
        imgBankDetail = findViewById(R.id.imgBankDetail);
        imgBack = findViewById(R.id.imgBack);


        ctx = getApplicationContext();
        etAccountName.addTextChangedListener(new CustomTextWatcher(ivAccountName, ctx).textWatcher);
        etSwiftNumber.addTextChangedListener(new CustomTextWatcher(ivSwiftNumber, ctx).textWatcher);
        etIBAN.addTextChangedListener(new CustomTextWatcher(ivIBAN, ctx).textWatcher);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        imgUploadBankDetailFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        populateData();


    }

    void populateData() {
        Common.showDialog(BankDetailActivity.this);
        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken);
        token = "Bearer " + token;

        String id = Common.fromJson(getApplicationContext()).getBank_detail_id();

        Call<BankDetailResponse> register = api.getBankDetail(token, id + "");

        register.enqueue(new Callback<BankDetailResponse>() {
            @Override
            public void onResponse(Call<BankDetailResponse> call, Response<BankDetailResponse> response) {
                System.out.println("Code   " + response.code());
                if (response.isSuccessful()) {
                    Common.dissmissDialog();
                    if (response.body().getStatus()) {
                        Common.bankDetailtoJson(getApplicationContext(), response.body().getData());
                        if (response.body().getStatus()) {
                            BankDetailModel bankDetailModel = response.body().getData();
                            etAccountName.setText(bankDetailModel.getAccountName());
                            etSwiftNumber.setText(bankDetailModel.getSwiftNumber());
                            etIBAN.setText(bankDetailModel.getIban());

                            if (bankDetailModel.getImagePath() != null && bankDetailModel.getImagePath() != "") {
                                bankImagePath = bankDetailModel.getImagePath();
                                bankImageExtension = bankDetailModel.getBankDetailImageExtension();

                                isPathUrl = true;

                                if (bankImageExtension.equals("png") || bankImageExtension.equals("jpg") || bankImageExtension.equals("jpeg"))
                                    Glide.with(BankDetailActivity.this).load(bankImagePath).into(imgBankDetail);
                                else if (bankImageExtension.equals("pdf"))
                                    Glide.with(BankDetailActivity.this).load(R.drawable.ic_pdf).into(imgBankDetail);
                            }

                        }

                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(BankDetailActivity.this, jsonObject.getString("message"), false);
                        Common.dissmissDialog();
                    } catch (Exception e) {
                        Common.dissmissDialog();
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<BankDetailResponse> call, Throwable t) {
                Common.dissmissDialog();
            }
        });

    }


    void pickImage() {
        Intent intent = new Intent(BankDetailActivity.this, FilePickerActivity.class);
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
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 101) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files.size() > 0) {
                    Uri fileUri = files.get(0).getUri();

                    bankImagePath = files.get(0).getPath();
                    bankImageExtension = bankImagePath.substring(bankImagePath.lastIndexOf("."));
                    if (bankImageExtension.equalsIgnoreCase(".png") || bankImageExtension.equalsIgnoreCase(".jpg") || bankImageExtension.equalsIgnoreCase(".jpeg")) {
                        startCrop(fileUri, 201);
                    } else if (bankImageExtension.equalsIgnoreCase(".pdf")) {
                        Glide.with(BankDetailActivity.this).load(R.drawable.ic_pdf).into(imgBankDetail);
                        isPathUrl = false;
                    }
                }
            }
            if (requestCode == 201) {
                Uri cropperUri = UCrop.getOutput(data);
                bankImagePath = FilePath.getPath(BankDetailActivity.this, cropperUri);
                bankImageExtension = bankImagePath.substring(bankImagePath.lastIndexOf("."));
                Glide.with(BankDetailActivity.this).load(bankImagePath).into(imgBankDetail);
                isPathUrl = false;
            }

        }
    }

    private void startCrop(Uri sourceUri, int requestCode) {

        File file = new File(BankDetailActivity.this.getCacheDir(), "IMG_" + System.currentTimeMillis() + ".jpg");
        destinationUriCropper = Uri.fromFile(file);

        startActivityForResult(UCrop.of(sourceUri, destinationUriCropper)
                .withMaxResultSize(1000, 1000)
                .withAspectRatio(5f, 5f)
                .getIntent(BankDetailActivity.this), requestCode);

    }

    void validateData() {


        if (etAccountName.getText().toString().isEmpty()) {
            showToast((BankDetailActivity.this), getResources().getString(R.string.bank_error_enter_account_name), false);
            return;
        }

        if (etSwiftNumber.getText().toString().isEmpty()) {
            showToast((BankDetailActivity.this), getResources().getString(R.string.bank_error_enter_swift_number), false);
            return;
        }
        if (etIBAN.getText().toString().isEmpty()) {
            showToast((BankDetailActivity.this), getResources().getString(R.string.bank_error_enter_iban), false);
            return;
        }

        if (bankImagePath.equals("")) {
            showToast((BankDetailActivity.this), getResources().getString(R.string.bank_error_enter_bank_detail_file), false);
            return;
        }


        SaveBankDetail();
    }

    void SaveBankDetail() {
        Common.showDialog(BankDetailActivity.this);

        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken);
        token = "Bearer " + token;
//        token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjM5NGJhOTQ1MGI2OWI3Y2M0MDJkMTM1ZDBjZTQwYTQzOWIyYWM2NzRmMTU0YzI2ZDdhYWNlYWQ0OTlkOTRhMzg3OGYxMWU0NzZhODcwMjk3In0.eyJhdWQiOiIxIiwianRpIjoiMzk0YmE5NDUwYjY5YjdjYzQwMmQxMzVkMGNlNDBhNDM5YjJhYzY3NGYxNTRjMjZkN2FhY2VhZDQ5OWQ5NGEzODc4ZjExZTQ3NmE4NzAyOTciLCJpYXQiOjE1NzM2MTg2NjIsIm5iZiI6MTU3MzYxODY2MiwiZXhwIjoxNjA1MjQxMDYyLCJzdWIiOiI1Iiwic2NvcGVzIjpbXX0.OfgRLybr1gFDL4--zV5FxrRLyNW28tidkP4Fhf_9MO-MMvzePj-D2F4Y8V2v9y1RsG4pLtx3qY542QEbZ7PN6nP6NHYdQ1OsGV3LQFN4QNIdBSUKhOHsNmMJ-P9ZomkAVDejLoi9kjkh8pOWRiZj7UyGwXH6j-55RuId-llOufEjZ_i5ZiM5J72QlbnRg9Fg6NCemhYwyFhmk2lq8WFXVbes3VDf8YCW44maNFRYrSWUZsfR9lxS5kcNbW1jP9fClGRxLef4MTtKkw9rkI2-fMABpCbW2SAPHxkuGFx5AhfzWT0ha7KNe1SxBD1tj7X-njbBFHYh-gpCgwc6puZLRY_HaLLc3qAGzxtGf9mcEMB6jvC-ieZM8zz8JD4pCHlcBJjvvDw_rcXzVy1uB0Y9gAGdbj-yR0iA1FquSILNYZITh1x9sxdNzI5NTN-OnJUovdzAjqvNykv9Xvt3hL8wo_xQWfF2-3s7gEDEKEgYjqvZNQt_rl3wdxa1TZOvS8Vk7s9HML4x-Tyy-eDxRiiXWj5Pgzj-MGCbQhFtgKuDje1PU7ziCQzYhx5qAaTxHO2yqQexJ49-YlV4N8SEpFiDRPhGr31-gQ-VHSjLv8f1Vo0KR-pwIScutRz8deJyNxRtBh9YzGdlsW6PBGF_c_GhyHoWOHbCXeKPGPcTkDpWGVU";
        RequestBody account_name = RequestBody.create(MediaType.parse("text/plain"), etAccountName.getText().toString());
        RequestBody swift_number = RequestBody.create(MediaType.parse("text/plain"), etSwiftNumber.getText().toString());
        RequestBody iban = RequestBody.create(MediaType.parse("text/plain"), etIBAN.getText().toString());

        MultipartBody.Part bankDetailFileBodyPart = null;
        RequestBody bank_detail_photo = null;

        if (!bankImagePath.equals("")) {
            if (!isPathUrl) {
                File bankDetailFile = null;
                bankDetailFile = new File(bankImagePath);
                if (bankImageExtension.equals(".png") || bankImageExtension.equals(".jpg") || bankImageExtension.equals(".jpeg"))
                    bank_detail_photo = RequestBody.create(MediaType.parse("image/*"), bankDetailFile);
                else if (bankImageExtension.equals(".pdf"))
                    bank_detail_photo = RequestBody.create(MediaType.parse("application/pdf"), bankDetailFile);
                bankDetailFileBodyPart = MultipartBody.Part.createFormData("bank_detail_photo", bankDetailFile.getName(), bank_detail_photo);
            }
        }

        Call<BankDetailResponse> register = api.updateBankDetail(token, account_name, swift_number, iban, bankDetailFileBodyPart);
        register.enqueue(new Callback<BankDetailResponse>() {
            @Override
            public void onResponse(Call<BankDetailResponse> call, Response<BankDetailResponse> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        BankDetailResponse bankDetailResponse = new BankDetailResponse(response.body().getStatus(), response.body().getMessage(), response.body().getData());


                        if (bankDetailResponse.getStatus()) {
                            Data data = Common.fromJson(getApplicationContext());
                            data.setBank_detail_id(response.body().getData().getId().toString());
                            Common.toJson(getApplicationContext(), data);
                            showToast(BankDetailActivity.this, response.body().getMessage(), true);
                            Common.bankDetailtoJson(getApplicationContext(), bankDetailResponse.getData());

                        } else {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            showToast(BankDetailActivity.this, jsonObject.getString("message"), false);
                        }

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(BankDetailActivity.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    Common.dissmissDialog();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<BankDetailResponse> call, Throwable t) {
                Common.dissmissDialog();
            }
        });


    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
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
