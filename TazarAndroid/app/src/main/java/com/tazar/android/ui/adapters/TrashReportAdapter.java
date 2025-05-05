package com.tazar.android.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.tazar.android.R;
import com.tazar.android.models.TrashReport;
import com.tazar.android.ui.interfaces.OnReportClickListener;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrashReportAdapter extends RecyclerView.Adapter<TrashReportAdapter.ReportViewHolder> {

    private List<TrashReport> reports;
    private OnReportClickListener listener;
    private static final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
    private static final SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public TrashReportAdapter(List<TrashReport> reports, OnReportClickListener listener) {
        this.reports = reports;
        this.listener = listener;
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
        holder.bind(reports.get(position));
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
        private final Chip statusChip;
        private final LinearLayout wasteTypeContainer;
        private final TextView wasteTypeTextView;
        private final MaterialButton viewDetailsButton;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.report_image);
            descriptionTextView = itemView.findViewById(R.id.report_description);
            locationTextView = itemView.findViewById(R.id.report_location);
            dateTextView = itemView.findViewById(R.id.report_date);
            statusChip = itemView.findViewById(R.id.report_status);
            wasteTypeContainer = itemView.findViewById(R.id.waste_type_container);
            wasteTypeTextView = itemView.findViewById(R.id.waste_type);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);

            // Устанавливаем обработчик клика на весь элемент
            itemView.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onReportClick(reports.get(position));
                }
            });

            // Устанавливаем обработчик на кнопку "Подробнее"
            viewDetailsButton.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onReportClick(reports.get(position));
                }
            });
        }

        void bind(TrashReport report) {
            // Устанавливаем описание
            String description = report.getDescription();
            if (description != null && !description.isEmpty()) {
                descriptionTextView.setText(description);
            } else {
                descriptionTextView.setText("Без описания");
            }
            
            // Отображаем адрес, если он есть, иначе координаты
            if (report.getAddress() != null && !report.getAddress().isEmpty()) {
                locationTextView.setText(report.getAddress());
            } else {
                locationTextView.setText(String.format(Locale.getDefault(), "%.6f, %.6f", 
                    report.getLatitude(), report.getLongitude()));
            }
            
            // Форматирование даты
            try {
                String createdAt = report.getCreatedAt();
                if (createdAt != null && !createdAt.isEmpty()) {
                    Date date = INPUT_FORMAT.parse(createdAt);
                    if (date != null) {
                        dateTextView.setText(OUTPUT_FORMAT.format(date));
                    } else {
                        dateTextView.setText(createdAt); // Показываем исходную строку, если парсинг не удался
                    }
                } else {
                    dateTextView.setText(R.string.date_not_available);
                }
            } catch (ParseException e) {
                // В случае ошибки парсинга показываем исходную строку
                dateTextView.setText(report.getCreatedAt());
            }
            
            // Устанавливаем текст и цвет статуса
            statusChip.setText(getStatusText(report.getStatus()));
            statusChip.setChipBackgroundColorResource(getStatusColorResource(report.getStatus()));

            // Показываем тип отходов, если он указан
            String wasteType = report.getWasteType();
            if (wasteType != null && !wasteType.isEmpty()) {
                wasteTypeContainer.setVisibility(View.VISIBLE);
                wasteTypeTextView.setText(String.format("Тип отходов: %s", wasteType));
            } else {
                wasteTypeContainer.setVisibility(View.GONE);
            }

            // Загружаем изображение с помощью Glide
            String imageUrl = report.getImageUrl();
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = report.getPhotoUrl(); // Проверяем альтернативное поле
            }
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Если URL не начинается с http, добавляем базовый URL
                if (!imageUrl.startsWith("http")) {
                    // Проверка для эмулятора или реального устройства
                    if (imageUrl.startsWith("/")) {
                        imageUrl = "http://10.0.2.2:8000" + imageUrl;
                    } else {
                        imageUrl = "http://10.0.2.2:8000/" + imageUrl;
                    }
                }
                
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_error)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        private String getStatusText(String status) {
            switch (status) {
                case "new":
                    return itemView.getContext().getString(R.string.status_new);
                case "in_progress":
                    return itemView.getContext().getString(R.string.status_in_progress);
                case "completed":
                    return itemView.getContext().getString(R.string.status_completed);
                case "rejected":
                    return itemView.getContext().getString(R.string.status_rejected);
                default:
                    return status;
            }
        }
        
        private int getStatusColorResource(String status) {
            switch (status) {
                case "completed":
                    return R.color.status_completed;
                case "in_progress":
                    return R.color.status_in_progress;
                case "rejected":
                    return R.color.status_rejected;
                case "new":
                default:
                    return R.color.status_new;
            }
        }
    }
} 