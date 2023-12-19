package com.vic.vicdriver.Views.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Request.FeedBackRequest;
import com.vic.vicdriver.Models.Response.GenResponse;
import com.vic.vicdriver.Models.Response.orders_pack.Datum;
import com.vic.vicdriver.Models.Response.orders_pack.Item;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.vic.vicdriver.Utils.Common.showToast;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderNumber, tvDateOfOrder, tvName, tvAddress, tvPhone, tvEmail, tvStatus;
    private TableLayout tabItems;
    private TextView tvTotalHtTxt, tvDisountTxt, tvTVATxt, tvTotalTTcTxt;
    private ConstraintLayout btnFeeadBack;
    private MaterialRatingBar scaleRatingBar;
    private RelativeLayout imgAppRating, imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        tvTotalHtTxt = findViewById(R.id.tvTotalHtTxt);
        tvTVATxt = findViewById(R.id.tvTVATxt);
        tvTotalTTcTxt = findViewById(R.id.tvTotalTTcTxt);
        tvDisountTxt = findViewById(R.id.tvDisountTxt);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvDateOfOrder = findViewById(R.id.tvDateOfOrder);


        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvStatus = findViewById(R.id.tvStatus);
        tvOrderNumber = findViewById(R.id.tvOrderNumber);

        tabItems = (TableLayout) findViewById(R.id.tabItems);

        btnFeeadBack = findViewById(R.id.btnFeeadBack);
        imgAppRating = findViewById(R.id.imgAppRating);
        imgBack = findViewById(R.id.imgBack);

        btnFeeadBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedBackDialog(getString(R.string.driver_feed_back), "driver");
            }
        });

        imgAppRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedBackDialog(getString(R.string.app_rating), "app");
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        populateData();

    }


    String currency = "€";

    TextView tvProductTxt, tvQuantityTxt, tvPriceTxt, tvTotalTxt;
    Datum orderModel;

    void populateData() {
        try {
            String symbol = SharedPreference.getSimpleString(getApplicationContext(),
                    currency);
            if (symbol != "" && !symbol.isEmpty()) {
                currency = symbol;
            }

            Bundle bundle = getIntent().getExtras();
            orderModel = bundle.getParcelable("orderModel");
            orderModel.setItems(bundle.getParcelableArrayList("items"));
            orderModel.setPickupAddresses(bundle.getParcelableArrayList("pickAddresses"));


            if (orderModel != null && orderModel.getOrderNumber() != null) {

                tvPhone.setText(orderModel.getTelephone().toString());
                tvEmail.setText(orderModel.getEmail().toString());

                if (orderModel.getDeliveryStatus().toString().equals("Completed"))
                    tvStatus.setText(getResources().getString(R.string.orderStatusCompleted));
                tvOrderNumber.setText(orderModel.getOrderNumber().toString());
                tvTotalHtTxt.setText(orderModel.getProductSubTotal().toString() + currency);
                tvDisountTxt.setText(orderModel.getProductDiscount().toString() + currency);
                tvTVATxt.setText(orderModel.getProductVat().toString() + currency);
                tvTotalTTcTxt.setText(orderModel.getProductTotal().toString() + currency);
                tvName.setText(orderModel.getBuyerCompany());
                tvAddress.setText(orderModel.getAddress());
                tvDateOfOrder.setText(orderModel.getDate());

                if (orderModel.getIsDriverAppReview() == 1) {
                    imgAppRating.setVisibility(View.GONE);
                }
                if (orderModel.getIsDriverReview() == 1) {
                    btnFeeadBack.setVisibility(View.GONE);
                }

                if (orderModel.getItems() != null && orderModel.getItems().size() > 0) {

                    for (int i = 0; i < orderModel.getItems().size(); i++) {
                        Item itemModel = orderModel.getItems().get(i);
                        View view = getLayoutInflater().inflate(R.layout.detai_items_row, null);
                        tvProductTxt = view.findViewById(R.id.tvProductTxt);
                        tvQuantityTxt = view.findViewById(R.id.tvQuantityTxt);
                        tvPriceTxt = view.findViewById(R.id.tvPriceTxt);
                        tvTotalTxt = view.findViewById(R.id.tvTotalTxt);

                        tvProductTxt.setText(itemModel.getProductVariety().toString());
                        tvQuantityTxt.setText(itemModel.getQuantity().toString() + " " + itemModel.getUnit());
                        tvPriceTxt.setText(itemModel.getPrice().toString() + currency);
                        tvTotalTxt.setText(itemModel.getTotal().toString() + currency);
                        tabItems.addView(view);

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(this, getResources().getString(R.string.something_is_not_right), false);
        }
    }

    String ratings = "0";
    EditText etCompliment;
    LinearLayout linMain;

    void showFeedBackDialog(String title, String type) {
        // custom dialog

        AlertDialog.Builder dialogue = new AlertDialog.Builder(OrderDetailActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialoge_driver_feed_back, null);
        dialogue.setView(view);
        AlertDialog alertDialog = dialogue.create();
        TextView tvFeedBackTitle = (TextView) view.findViewById(R.id.tvFeedBackTitle);
        etCompliment = (EditText) view.findViewById(R.id.etCompliment);
        linMain = (LinearLayout) view.findViewById(R.id.linMain);
        ConstraintLayout btnRatingDone = view.findViewById(R.id.btnRatingDone);
        tvFeedBackTitle.setText(title);
        etCompliment.addTextChangedListener(checkEmpty);
        scaleRatingBar = view.findViewById(R.id.ratingBarFeedBack);

        scaleRatingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                ratings = rating + "";
            }
        });


        btnRatingDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etCompliment.getText().toString().replace(" ", "").isEmpty()) {
                    etCompliment.setBackgroundResource(R.drawable.red_border);
                } else {

                    alertDialog.dismiss();
                    saveFeedBack(ratings, etCompliment.getText().toString(), type);
                }
            }
        });
        linMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//Hide:
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//Show
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                hideKeyboard();
            }
        });


        alertDialog.show();

    }


    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
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


    TextWatcher checkEmpty = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().replace(" ", "").isEmpty()) {
                etCompliment.setBackgroundResource(R.drawable.red_border);
            } else {
                etCompliment.setBackgroundResource(R.color.row_fotter);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    void saveFeedBack(String rating, String comment, String type) {
        Common.showDialog(OrderDetailActivity.this);

        Api api = RetrofitClient.getClient().create(Api.class);
        String token = SharedPreference.getSimpleString(getApplicationContext(), Constants.accessToken);
        token = "Bearer " + token;

        if (orderModel != null && orderModel.getOrderNumber() != null) {
            FeedBackRequest acceptOrderRequest = new FeedBackRequest(orderModel.getDeliveryAddressId().toString(), rating, comment, type);

            Call<GenResponse> call = api.updateFeedBack(token, acceptOrderRequest);

            call.enqueue(new Callback<GenResponse>() {
                @Override
                public void onResponse(Call<GenResponse> call, Response<GenResponse> response) {

                    Common.dissmissDialog();
                    try {
                        if (response.isSuccessful()) {

                            if (response.body().getStatus()) {
                                if (type.equalsIgnoreCase("driver")) {
                                    orderModel.setIsDriverReview(1);
                                    Common.orderModeltoJson(getApplicationContext(), orderModel);
                                    btnFeeadBack.setVisibility(View.GONE);
                                } else if (type.equalsIgnoreCase("app")) {
                                    orderModel.setIsDriverAppReview(1);
                                    Common.orderModeltoJson(getApplicationContext(), orderModel);
                                    imgAppRating.setVisibility(View.GONE);
                                }
                                showToast(OrderDetailActivity.this, response.body().getMessage() + "", true);

                                Intent deliveredOrdersBroadCast = new Intent("DeliveredOrdersBroadCast");
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(deliveredOrdersBroadCast);

                            } else {
                                showToast(OrderDetailActivity.this, response.body().getMessage() + "", false);

                            }

                        } else {
                            showToast(OrderDetailActivity.this, response.body().getMessage() + "", false);
                        }

                    } catch (Exception e) {
                        Common.dissmissDialog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GenResponse> call, Throwable t) {
                    Common.dissmissDialog();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }
    }

}
