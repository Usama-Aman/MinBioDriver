package com.vic.vicdriver.Views.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicdriver.Controllers.Helpers.OrderRecyclerAdapter;
import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.Response.orders_pack.Datum;
import com.vic.vicdriver.Models.Response.orders_pack.OrdersResponse;
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

public class OngoingOrders extends Fragment {

    private RecyclerView ordersRecycler;
    private SwipeRefreshLayout orderPullToRefresh;
    private final String ORDER_STATUS = "accepted";
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private int CURRENT_PAGE = 1;
    private int LAST_PAGE = 0;
    private ImageView ivListNull;
    private TextView listNull;

    private OrderRecyclerAdapter orderRecyclerAdapter;
    private View v;
    private ArrayList<Datum> datum = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("OngoingOrdersBroadCast")) {
                getOrders(context, 0, true);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_available, container, false);

        initViews();

        if (datum.size() == 0) {
            if (SharedPreference.getBoolean(getContext(), "FromNoti")) {
                SharedPreference.saveBoolean(getContext(), "FromNoti", false);
            } else
                Common.showDialog(getContext());

            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("OngoingOrdersBroadCast"));
            CURRENT_PAGE = 1;
            getOrders(getContext(), CURRENT_PAGE, false);
        }

        return v;
    }

    private void initViews() {
        ordersRecycler = v.findViewById(R.id.orderRecycler);
        orderPullToRefresh = v.findViewById(R.id.orderPullToRefresh);
        listNull = v.findViewById(R.id.tvListNull);
        ivListNull = v.findViewById(R.id.ivListNull);
        orderPullToRefresh = v.findViewById(R.id.orderPullToRefresh);
        orderPullToRefresh.setOnRefreshListener(() -> {
            CURRENT_PAGE = 1;
            getOrders(getContext(), 1, true);
        });

        setAdapter();
        initScrollListener();
    }

    private void setAdapter() {
        orderRecyclerAdapter = new OrderRecyclerAdapter(getContext(), datum);
        layoutManager = new LinearLayoutManager(getContext());
        ordersRecycler.setLayoutManager(layoutManager);
        ordersRecycler.setAdapter(orderRecyclerAdapter);
    }

    //Initializing the scroll view to check the last vale to show the prograss bar for pagination
    private void initScrollListener() {
        ordersRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            datum.add(null);
            orderRecyclerAdapter.notifyItemInserted(datum.size() - 1);

            if (CURRENT_PAGE != LAST_PAGE) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CURRENT_PAGE = CURRENT_PAGE + 1;
                        getOrders(getContext(), CURRENT_PAGE, false);
                    }
                }, 500);
            } else {
                ordersRecycler.post(new Runnable() {
                    @Override
                    public void run() {
                        datum.remove(datum.size() - 1);
                        orderRecyclerAdapter.notifyItemRemoved(datum.size());
                    }
                });
            }

            orderRecyclerAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getOrders(Context context, int c, boolean isRefresh) {
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<OrdersResponse> call = api.getMyOrders("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), ORDER_STATUS, c);

        call.enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                if (isRefresh) {
                    orderPullToRefresh.setRefreshing(false);
                    datum.clear();
                }
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        LAST_PAGE = response.body().getMeta().getLastPage();
                        // CURRENT_PAGE = response.body().getMeta().getCurrentPage();

                        if (datum.size() > 0) {
                            datum.remove(datum.size() - 1);
                            orderRecyclerAdapter.notifyItemRemoved(datum.size());
                        }

                        datum.addAll(response.body().getData());

                        if (datum.size() == 0) {
                            listNull.setVisibility(View.VISIBLE);
                            ivListNull.setVisibility(View.VISIBLE);
                            ordersRecycler.setVisibility(View.GONE);
                        } else {
                            listNull.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.GONE);
                            ordersRecycler.setVisibility(View.VISIBLE);
                        }

                        orderRecyclerAdapter.notifyDataSetChanged();
                        isLoading = false;
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(getActivity(), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<OrdersResponse> call, Throwable t) {
                Common.dissmissDialog();
                orderPullToRefresh.setRefreshing(false);
                Log.d("", "onFailure: " + t.getMessage());
                listNull.setVisibility(View.VISIBLE);
                ivListNull.setVisibility(View.VISIBLE);
                ordersRecycler.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

}
