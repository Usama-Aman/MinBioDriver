package com.vic.vicdriver.Controllers.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicdriver.Models.Response.TransactionHistory.Datum;
import com.vic.vicdriver.R;

import java.util.ArrayList;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final int VIEW_TYPE_Items = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ArrayList<Datum> datum = new ArrayList<>();

    public TransactionHistoryAdapter(Context context, ArrayList<Datum> datum) {
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
            v = inflater.inflate(R.layout.item_transaction_history, parent, false);
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

            try {

                ((Items) holder).transactionDate.setText(datum.get(position).getDate());
                ((Items) holder).transactionOrderNumber.setText(datum.get(position).getOrderNumbers());
                ((Items) holder).transactionAddress.setText(datum.get(position).getAddress());
                ((Items) holder).transactionAmount.setText(datum.get(position).getDriverAmount());

                if (datum.get(position).getPaymentStatus().equalsIgnoreCase("Unpaid")) {
                    ((Items) holder).transactionStatus.setText(context.getResources().getString(R.string.status_unpaid_transaction_history));
                    ((Items) holder).transactionStatus.setTextColor(context.getResources().getColor(R.color.red));
                    ((Items) holder).transactionStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_edpired), null, null, null);
                    ((Items) holder).transactionStatus.setCompoundDrawablePadding(8);
                } else if (datum.get(position).getPaymentStatus().equalsIgnoreCase("Paid")) {
                    ((Items) holder).transactionStatus.setText(context.getResources().getString(R.string.status_paid_transaction_history));
                    ((Items) holder).transactionStatus.setTextColor(context.getResources().getColor(R.color.signUpButtonColor));
                    ((Items) holder).transactionStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_paid), null, null, null);
                    ((Items) holder).transactionStatus.setCompoundDrawablePadding(8);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

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

        TextView transactionDate, transactionOrderNumber, transactionAddress, transactionAmount, transactionStatus;

        public Items(@NonNull View ItemsView) {
            super(ItemsView);

            transactionDate = ItemsView.findViewById(R.id.tvDateTransaction);
            transactionAddress = ItemsView.findViewById(R.id.addressTransaction);
            transactionOrderNumber = ItemsView.findViewById(R.id.orderNumberTransaction);
            transactionAmount = ItemsView.findViewById(R.id.amountTransaction);
            transactionStatus = ItemsView.findViewById(R.id.statusTransaction);
        }
    }


    private class Progress extends RecyclerView.ViewHolder {

        public Progress(@NonNull View ItemsView) {
            super(ItemsView);
        }
    }
}
