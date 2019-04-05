package com.adobe.vehicletelemetry.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adobe.vehicletelemetry.model.Alert;
import com.adobe.vehicletelemetry.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.RideViewHolder>{

    private List<Alert> alertsList;
    private SimpleDateFormat ddFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
    private SimpleDateFormat mmmFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);

    public AlertAdapter(List<Alert> alertList){
        this.alertsList = alertList;
    }

    @Override
    public RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alert_layout, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RideViewHolder holder, int position) {
        Alert alert = alertsList.get(position);

        holder.ddTextView.setText(ddFormat.format(alert.getDate()));
        holder.mmmTextView.setText(mmmFormat.format(alert.getDate()));
        holder.detailTextView.setText(alert.getDetail());
    }

    @Override
    public int getItemCount() {
        return alertsList.size();
    }

    class RideViewHolder extends RecyclerView.ViewHolder{

        TextView ddTextView;
        TextView mmmTextView;
        TextView detailTextView;

        RideViewHolder(View view){
            super(view);

            ddTextView = view.findViewById(R.id.item_alert_dd);
            mmmTextView = view.findViewById(R.id.item_alert_mmm);
            detailTextView = view.findViewById(R.id.item_alert_text);
        }
    }
}
