package com.adobe.vehicletelemetry.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adobe.vehicletelemetry.R;
import com.adobe.vehicletelemetry.model.Report;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.RideViewHolder> {

    private Context context;
    private List<Report> reportList;
    private ReportListener listener;

    private SimpleDateFormat ddFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
    private SimpleDateFormat mmmFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
    private SimpleDateFormat hhmmFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
    private SimpleDateFormat aFormat = new SimpleDateFormat("a", Locale.ENGLISH);

    public ReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
        listener = (ReportListener) context;
    }

    @Override
    public RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_layout, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RideViewHolder holder, final int position) {
        Report report = reportList.get(position);

        holder.mapImage.setImageResource(report.getMapId());

        holder.ddTextView.setText(ddFormat.format(report.getDate()));
        holder.mmmTextView.setText(mmmFormat.format(report.getDate()));
        holder.hhmmTextView.setText(hhmmFormat.format(report.getDate()));
        holder.aTextView.setText(aFormat.format(report.getDate()));

        if(report.isLive()){
            holder.ratingImageView.setVisibility(View.GONE);
            holder.liveTextView.setVisibility(View.VISIBLE);
        }
        else {
            holder.ratingImageView.setVisibility(View.VISIBLE);
            holder.liveTextView.setVisibility(View.GONE);

            int color = ContextCompat.getColor(context, R.color.averageRide);
            ImageViewCompat.setImageTintList(holder.ratingImageView, ColorStateList.valueOf(color));
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    class RideViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView mapImage;
        TextView liveTextView;
        TextView ddTextView;
        TextView mmmTextView;
        TextView hhmmTextView;
        TextView aTextView;
        ImageView ratingImageView;

        RideViewHolder(View view) {
            super(view);

            this.view = view;
            mapImage = view.findViewById(R.id.item_report_map);
            liveTextView = view.findViewById(R.id.item_report_live_text);
            ddTextView = view.findViewById(R.id.item_report_dd_text);
            mmmTextView = view.findViewById(R.id.item_report_mmm_text);
            hhmmTextView = view.findViewById(R.id.item_report_hhmm_text);
            aTextView = view.findViewById(R.id.item_report_ampm_text);
            ratingImageView = view.findViewById(R.id.item_report_rating_image);
        }
    }

    public interface ReportListener {
        void onItemClick(int index);
    }
}
