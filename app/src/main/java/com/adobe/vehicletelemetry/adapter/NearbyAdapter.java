package com.adobe.vehicletelemetry.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adobe.vehicletelemetry.R;
import com.adobe.vehicletelemetry.model.Nearby;

import java.util.List;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.RideViewHolder>{

    private List<Nearby> nearbyList;

    @Override
    public RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nearby_layout, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RideViewHolder holder, int position) {
        Nearby nearby = nearbyList.get(position);

        int iconResId;
        if (nearby.getType() == Nearby.TYPE_PETROL_PUMP) {
            iconResId = R.drawable.ic_petrol_station;
        } else if (nearby.getType() == Nearby.TYPE_ENTERTAINMENT) {
            iconResId = R.drawable.ic_entertainment;
        } else if (nearby.getType() == Nearby.TYPE_RESTAURANT) {
            iconResId = R.drawable.ic_restaurants;
        } else {
            iconResId = R.drawable.ic_service_center;
        }

        holder.iconImageView.setImageResource(iconResId);
        holder.detailTextView.setText(nearby.getPoiName() + "\n" + nearby.getAddress());
    }

    public void setNearbyList(List<Nearby> nearbyList) {
        this.nearbyList = nearbyList;
    }

    @Override
    public int getItemCount() {
        return nearbyList.size();
    }

    class RideViewHolder extends RecyclerView.ViewHolder{

        ImageView iconImageView;
        TextView detailTextView;

        RideViewHolder(View view){
            super(view);

            iconImageView = view.findViewById(R.id.item_nearby_image);
            detailTextView = view.findViewById(R.id.item_nearby_text);
        }
    }
}
