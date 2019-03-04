package io.mapwize.mapwizecomponents.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.mapwize.mapwizecomponents.R;

public class FloorControllerAdapter extends RecyclerView.Adapter<FloorControllerAdapter.FloorItemViewHolder> {

    private List<Floor> floors;
    private FloorControllerAdapter.OnFloorClickListener listener;
    private Context context;

    void swapData(List<Floor> floors) {
        this.floors = floors;
        notifyDataSetChanged();
    }

    void setListener(FloorControllerAdapter.OnFloorClickListener listener) {
        this.listener = listener;
    }

    @Override
    public FloorControllerAdapter.FloorItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mapwize_floor_item, parent, false);
        context = view.getContext();
        return new FloorControllerAdapter.FloorItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FloorControllerAdapter.FloorItemViewHolder holder, int position) {

        Floor floor = floors.get(position);
        holder.titleView.setText(floor.getDisplayName());
        holder.itemView.setClickable(true);
        if (!floor.isSelected()) {
            holder.titleView.setBackground(context.getResources().getDrawable(R.drawable.mapwize_semi_rounded_view));
        }
        else {
            holder.titleView.setBackground(context.getResources().getDrawable(R.drawable.mapwize_semi_rounded_selected_view));
        }
    }

    @Override
    public int getItemCount() {
        return floors != null ? floors.size() : 0;
    }

    class FloorItemViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;

        FloorItemViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.floorLabel);

            itemView.setOnClickListener(view -> {
                int adapterPosition = getAdapterPosition();
                if (listener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onFloorClick(floors.get(adapterPosition));
                }
            });
        }
    }

    public interface OnFloorClickListener {
        void onFloorClick(Floor floor);
    }
}