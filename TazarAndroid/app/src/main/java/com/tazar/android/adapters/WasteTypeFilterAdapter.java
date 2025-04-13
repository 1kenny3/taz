package com.tazar.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.tazar.android.R;
import java.util.Arrays;
import java.util.List;

public class WasteTypeFilterAdapter extends RecyclerView.Adapter<WasteTypeFilterAdapter.FilterViewHolder> {
    
    private final List<WasteTypeFilter> filters = Arrays.asList(
        new WasteTypeFilter("all", "Все"),
        new WasteTypeFilter("plastic", "Пластик"),
        new WasteTypeFilter("paper", "Бумага"),
        new WasteTypeFilter("metal", "Металл"),
        new WasteTypeFilter("glass", "Стекло"),
        new WasteTypeFilter("medical", "Медицинские отходы"),
        new WasteTypeFilter("construction", "Строительные отходы"),
        new WasteTypeFilter("agricultural", "Сельские отходы")
    );
    
    private int selectedPosition = 0;
    private final OnFilterSelectedListener listener;
    
    public WasteTypeFilterAdapter(OnFilterSelectedListener listener) {
        this.listener = listener;
    }
    
    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_waste_type_filter, parent, false);
        return new FilterViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(FilterViewHolder holder, int position) {
        WasteTypeFilter filter = filters.get(position);
        holder.bind(filter, position == selectedPosition);
    }
    
    @Override
    public int getItemCount() {
        return filters.size();
    }
    
    public interface OnFilterSelectedListener {
        void onFilterSelected(String wasteType);
    }
    
    class FilterViewHolder extends RecyclerView.ViewHolder {
        private final TextView filterText;
        private final View filterBackground;
        
        FilterViewHolder(View itemView) {
            super(itemView);
            filterText = itemView.findViewById(R.id.filterText);
            filterBackground = itemView.findViewById(R.id.filterBackground);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    int oldPosition = selectedPosition;
                    selectedPosition = position;
                    notifyItemChanged(oldPosition);
                    notifyItemChanged(selectedPosition);
                    listener.onFilterSelected(filters.get(position).getType());
                }
            });
        }
        
        void bind(WasteTypeFilter filter, boolean isSelected) {
            filterText.setText(filter.getDisplayName());
            filterBackground.setSelected(isSelected);
        }
    }
    
    private static class WasteTypeFilter {
        private final String type;
        private final String displayName;
        
        WasteTypeFilter(String type, String displayName) {
            this.type = type;
            this.displayName = displayName;
        }
        
        String getType() { return type; }
        String getDisplayName() { return displayName; }
    }
} 