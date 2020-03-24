package io.mapwize.mapwizeui.modeview;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizeui.R;

public class ModeViewAdapter extends RecyclerView.Adapter<ModeViewAdapter.ModeItemViewHolder> {

    private List<DirectionMode> modes;
    private DirectionMode selectedMode;
    private OnModeChangeListener listener;

    void swapData(List<DirectionMode> modes) {
        this.modes = modes;
        if (selectedMode == null || !modes.contains(selectedMode)) {
            setSelectedMode(modes.get(0));
        }
        else {
            notifyDataSetChanged();
        }
    }

    void setSelectedMode(DirectionMode mode) {
        selectedMode = mode;
        listener.onModeChange(selectedMode);
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public ModeItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mapwize_mode_item, parent, false);
        ViewTreeObserver vto = parent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width  = parent.getMeasuredWidth();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                int divider = modes.size() < 5 ? modes.size() : 4;
                params.width = (width / divider);
                view.setLayoutParams(params);
            }
        });
        return new ModeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ModeItemViewHolder holder, int position) {
        DirectionMode mode = modes.get(position);
        holder.imageView.setImageDrawable(holder.itemView.getResources().getDrawable(mode.getDrawableId()));
        holder.mode = mode;
        holder.setSelected(mode == selectedMode);
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }

    public void setListener(OnModeChangeListener listener) {
        this.listener = listener;
    }

    class ModeItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RelativeLayout layout;
        DirectionMode mode;

        ModeItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mode_image);
            layout = itemView.findViewById(R.id.mapwize_mode_layout);
            layout.setOnClickListener(v->{ setSelectedMode(mode);});
        }

        void setSelected(boolean selected) {
            if (selected) {
                imageView.getDrawable().setColorFilter(itemView.getResources().getColor(R.color.mapwize_main_color), PorterDuff.Mode.SRC_ATOP);
                imageView.setBackground(itemView.getResources().getDrawable(R.drawable.mapwize_rounded_pink_selected_view));
            }
            else {
                imageView.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                imageView.setBackground(itemView.getResources().getDrawable(R.drawable.mapwize_rounded_selected_view));
            }

        }
    }

    public interface OnModeChangeListener {
        void onModeChange(DirectionMode mode);
    }

}
