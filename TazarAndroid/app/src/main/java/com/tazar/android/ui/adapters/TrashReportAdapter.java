package com.tazar.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tazar.android.R;
import com.tazar.android.models.TrashReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrashReportAdapter extends RecyclerView.Adapter<TrashReportAdapter.ReportViewHolder> {

    private List<TrashReport> reports;
    private final SimpleDateFormat dateFormat;

    public TrashReportAdapter(List<TrashReport> reports) {
        this.reports = reports;
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trash_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        TrashReport report = reports.get(position);
        holder.bind(report);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void updateReports(List<TrashReport> newReports) {
        this.reports = newReports;
        notifyDataSetChanged();
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView descriptionTextView;
        private final TextView locationTextView;
        private final TextView dateTextView;
        private final TextView statusTextView;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.report_image);
            descriptionTextView = itemView.findViewById(R.id.report_description);
            locationTextView = itemView.findViewById(R.id.report_location);
            dateTextView = itemView.findViewById(R.id.report_date);
            statusTextView = itemView.findViewById(R.id.report_status);
        }

        void bind(TrashReport report) {
            descriptionTextView.setText(report.getDescription());
            locationTextView.setText(String.format("%.6f, %.6f", 
                report.getLatitude(), report.getLongitude()));
            dateTextView.setText(dateFormat.format(report.getCreatedAt()));
            statusTextView.setText(report.getStatus());

            if (report.getImageUrl() != null && !report.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(report.getImageUrl())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_error)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_image_placeholder);
            }
        }
    }
} 