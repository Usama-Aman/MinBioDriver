package com.vic.vicdriver.Controllers.Helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicdriver.Models.Response.orders_pack.Datum;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.SharedPreference;
import com.vic.vicdriver.Views.Activities.OrderDetailActivity;
import com.vic.vicdriver.Views.Activities.SummaryActivity;

import java.util.ArrayList;

import static com.vic.vicdriver.Utils.Constants.mLastClickTime;

public class OrderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Datum> datum = new ArrayList<>();
    private final int VIEW_TYPE_Items = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public OrderRecyclerAdapter(Context context, ArrayList<Datum> datum) {
        this.context = context;
        this.datum = datum;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == VIEW_TYPE_Items) {
            v = inflater.inflate(R.layout.my_orders_recycler_item, parent, false);
            viewHolder = new Items(v);
        } else if (viewType == VIEW_TYPE_LOADING) {
            v = inflater.inflate(R.layout.item_loader_pagination_products, parent, false);
            viewHolder = new Progress(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof Items) {

            ((Items) holder).linRow.setTag(datum.get(position).getOrderNumber());

            ((Items) holder).tvName.setText(datum.get(position).getBuyerCompany());
            ((Items) holder).orderNumber.setText(datum.get(position).getBuyerOrderNumber());
            ((Items) holder).tvDateTime.setText(datum.get(position).getDate() + ", " + datum.get(position).getTime());
            ((Items) holder).tvAddress.setText(datum.get(position).getAddress());
            ((Items) holder).tvMerchantName.setText(datum.get(position).getMerchantCompany());
            if (datum.get(position).getProductTotal() != null) {
                ((Items) holder).tvAmount.setText(datum.get(position).getProductTotal().toString() + SharedPreference.getSimpleString(context, "€"));
            }
            if (datum.get(position).getDeliveryStatus() != null) {

                if (datum.get(position).getDeliveryStatus().equalsIgnoreCase("Completed")) {
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.completed_status));
                } else if (datum.get(position).getDeliveryStatus().equalsIgnoreCase("Accepted")) {
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.blue));
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.accepted));
                } else if (datum.get(position).getDeliveryStatus().equalsIgnoreCase("Pending")) {
                    ((Items) holder).tvStatus.setText(context.getResources().getString(R.string.pending));
                    ((Items) holder).tvStatus.setTextColor(context.getResources().getColor(R.color.black));
                }
            }

            ((Items) holder).linRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    if (datum.get(position).getDeliveryStatus().equalsIgnoreCase("pending") ||
                            datum.get(position).getDeliveryStatus().equalsIgnoreCase("accepted")) {
                        Intent intent = new Intent(context, SummaryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("orderModel", datum.get(position));
                        bundle.putParcelableArrayList("items", datum.get(position).getItems());
                        bundle.putParcelableArrayList("pickAddresses", datum.get(position).getPickupAddresses());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    } else if (datum.get(position).getDeliveryStatus().equalsIgnoreCase("completed")) {
                        Intent intent = new Intent(context, OrderDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("orderModel", datum.get(position));
                        bundle.putParcelableArrayList("items", datum.get(position).getItems());
                        bundle.putParcelableArrayList("pickAddresses", datum.get(position).getPickupAddresses());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                }
            });
        }


    }

    @Override
    public int getItemViewType(int position) {
        return datum.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_Items;
    }


    @Override
    public int getItemCount() {
        return datum == null ? 0 : datum.size();
    }


    private class Items extends RecyclerView.ViewHolder {

        LinearLayout linRow;
        ImageView imgMap;
        ImageView imgDetail;
        TextView tvName;
        TextView tvDateTime;
        TextView tvAddress;
        TextView tvMerchantName;
        TextView tvAmount;
        TextView tvStatus;
        TextView orderNumber;

        public Items(@NonNull View ItemsView) {
            super(ItemsView);

            linRow = ItemsView.findViewById(R.id.linRow);
            imgMap = ItemsView.findViewById(R.id.imgMap);
            imgDetail = ItemsView.findViewById(R.id.imgDetail);
            tvName = ItemsView.findViewById(R.id.tvName);
            tvDateTime = ItemsView.findViewById(R.id.tvDateTime);
            tvAddress = ItemsView.findViewById(R.id.tvAddress);
            tvMerchantName = ItemsView.findViewById(R.id.tvMerchantName);
            tvAmount = ItemsView.findViewById(R.id.tvAmount);
            tvStatus = ItemsView.findViewById(R.id.tvStatus);
            orderNumber = ItemsView.findViewById(R.id.orderNumber);
        }
    }

    private class Progress extends RecyclerView.ViewHolder {

        public Progress(@NonNull View ItemsView) {
            super(ItemsView);
        }
    }
}
