package com.vic.vicdriver.Views.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicdriver.Controllers.Helpers.TransactionHistoryAdapter;
import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Response.TransactionHistory.Datum;
import com.vic.vicdriver.Models.Response.TransactionHistory.TransactionHistroyModel;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicdriver.Utils.Common.showToast;

public class TransactionHistory extends AppCompatActivity {

    private TextView totalBalance, pendingBalance;
    private RecyclerView transactionRecycler;
    private TransactionHistoryAdapter transactionHistoryAdapter;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout transactionPullToRefresh;
    private TransactionHistroyModel transactionHistroyModel;
    private ImageView ivListNull;
    private TextView listNull, amountLabel;
    private ArrayList<Datum> datum = new ArrayList<>();
    private ConstraintLayout mainConstraint,transactionDataConstraint;
    private RelativeLayout imgBack;

    private boolean isLoading = false;
    private int CURRENT_PAGE = 0;
    private int LAST_PAGE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        initViews();
    }

    private void initViews() {

        pendingBalance = findViewById(R.id.tvPendingBalanceTransaction);
        totalBalance = findViewById(R.id.tvTotalBalanceTransaction);
        amountLabel = findViewById(R.id.l3);
        amountLabel.setText(getResources().getString(R.string.amount_transaction_history) + "(" + SharedPreference.getSimpleString(TransactionHistory.this,
                Constants.currency) + ")");

        transactionRecycler = findViewById(R.id.recyclerTransaction);
        mainConstraint = findViewById(R.id.transactionMainConstraint);
        transactionDataConstraint = findViewById(R.id.transactionDataConstraint);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        transactionPullToRefresh = findViewById(R.id.transactionSwipeToRefresh);
        listNull = findViewById(R.id.tvListNull);
        ivListNull = findViewById(R.id.ivListNull);
        transactionPullToRefresh = findViewById(R.id.transactionSwipeToRefresh);
        transactionPullToRefresh.setOnRefreshListener(() -> {
            getDataFromApi(0, true);
        });

        setAdapter();
        Common.showDialog(this);
        getDataFromApi(0, false);
    }

    private void setAdapter() {
        transactionHistoryAdapter = new TransactionHistoryAdapter(this, datum);
        layoutManager = new LinearLayoutManager(TransactionHistory.this);
        transactionRecycler.setLayoutManager(layoutManager);
        transactionRecycler.setAdapter(transactionHistoryAdapter);

        initScrollListener();
    }

    private void getDataFromApi(int c, boolean isRefresh) {

        Api api = RetrofitClient.getClient().create(Api.class);
        Call<TransactionHistroyModel> call = api.getDriverPayment("Bearer " +
                SharedPreference.getSimpleString(TransactionHistory.this, Constants.accessToken), c);

        call.enqueue(new Callback<TransactionHistroyModel>() {
            @Override
            public void onResponse(Call<TransactionHistroyModel> call, Response<TransactionHistroyModel> response) {
                if (isRefresh) {
                    transactionPullToRefresh.setRefreshing(false);
                    datum.clear();
                }
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        LAST_PAGE = response.body().getMeta().getLastPage();
                        CURRENT_PAGE = response.body().getMeta().getCurrentPage();

                        if (datum.size() > 0) {
                            datum.remove(datum.size() - 1);
                            transactionHistoryAdapter.notifyItemRemoved(datum.size());
                        }

                        transactionHistroyModel = new TransactionHistroyModel(response.body().getData(),
                                response.body().getLinks(), response.body().getMeta(), response.body().getExtra(), response.body().getStatus(), response.message());
                        datum.addAll(response.body().getData());

                        if (datum.size() == 0) {
                            listNull.setVisibility(View.VISIBLE);
                            ivListNull.setVisibility(View.VISIBLE);
                            transactionDataConstraint.setVisibility(View.GONE);
                        } else {

                            pendingBalance.setText(transactionHistroyModel.getExtra().getPending() + SharedPreference.getSimpleString(TransactionHistory.this,
                                    Constants.currency));
                            totalBalance.setText(transactionHistroyModel.getExtra().getReceived() + SharedPreference.getSimpleString(TransactionHistory.this,
                                    Constants.currency));

                            listNull.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.GONE);
                            transactionDataConstraint.setVisibility(View.VISIBLE);
                        }

                        transactionHistoryAdapter.notifyDataSetChanged();
                        isLoading = false;
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(TransactionHistory.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TransactionHistroyModel> call, Throwable t) {
                Common.dissmissDialog();
                transactionPullToRefresh.setRefreshing(false);
                Log.d("", "onFailure: " + t.getMessage());
                listNull.setVisibility(View.VISIBLE);
                ivListNull.setVisibility(View.VISIBLE);
                transactionDataConstraint.setVisibility(View.GONE);
            }
        });

    }

    //Initializing the scroll view to check the last vale to show the prograss bar for pagination
    private void initScrollListener() {
        transactionRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == datum.size() - 1) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                loadMore();
                            }
                        });
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        try {

            if (CURRENT_PAGE != LAST_PAGE) {

                datum.add(null);
                transactionHistoryAdapter.notifyItemInserted(datum.size() - 1);


                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getDataFromApi(CURRENT_PAGE + 1, false);
                    }
                }, 500);
            }

            transactionHistoryAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
