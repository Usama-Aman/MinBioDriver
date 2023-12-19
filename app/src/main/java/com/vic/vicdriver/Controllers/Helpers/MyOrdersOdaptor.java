package com.vic.vicdriver.Controllers.Helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicdriver.Models.Response.orders_pack.Datum;
import com.vic.vicdriver.R;

import java.util.List;

public class MyOrdersOdaptor extends RecyclerView.Adapter<MyOrdersOdaptor.MyViewHolder> {

    private List<Datum> orderModels;
    AppCompatActivity ctx;
    OnItemClickListner onItemClickListner;
    public interface OnItemClickListner{
        void onRowClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linRow;
        ImageView imgProfile, imgMap, imgDetail;
        TextView tvName, tvDateTime, tvAddress, tvMerchantName, tvAmount, tvStatus;

        public MyViewHolder(View view) {
            super(view);
            linRow = (LinearLayout) view.findViewById(R.id.linRow);

            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
            imgMap = (ImageView) view.findViewById(R.id.imgMap);
            imgDetail = (ImageView) view.findViewById(R.id.imgDetail);


            tvName = (TextView) view.findViewById(R.id.tvName);
            tvDateTime = (TextView) view.findViewById(R.id.tvDateTime);
            tvAddress = (TextView) view.findViewById(R.id.tvAddress);
            tvMerchantName = (TextView) view.findViewById(R.id.tvMerchantName);
            tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        }
    }


    public MyOrdersOdaptor(List<Datum> orderModels, AppCompatActivity ctx,OnItemClickListner onItemClickListner) {
        this.orderModels = orderModels;
        this.ctx = ctx;
        this.onItemClickListner=onItemClickListner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_orders_recycler_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Datum orderModel = orderModels.get(position);
        holder.linRow.setTag(orderModel.getOrderNumber());
        holder.linRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListner.onRowClick(position);
//                showToast(ctx,position+"",false);
            }
        });
        holder.imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.imgDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Glide.with(ctx)  //2
                .load(orderModel.getUserImage()) //3
                .into(holder.imgProfile); //8


        holder.tvName.setText(orderModel.getBuyerCompany());
        holder.tvDateTime.setText(orderModel.getDate());
        holder.tvAddress.setText(orderModel.getAddress());
        holder.tvMerchantName.setText(orderModel.getMerchantCompany());
        holder.tvAmount.setText(orderModel.getProductTotal() .toString());
        holder.tvStatus.setText(orderModel.getDeliveryStatus());

    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }
}
