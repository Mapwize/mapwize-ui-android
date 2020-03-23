package io.mapwize.mapwizeui.modeview;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizeui.R;

public class ModeViewAdapter extends RecyclerView.Adapter<ModeViewAdapter.ModeItemViewHolder> {

    List<DirectionMode> modes;

    void swapData(List<DirectionMode> modes) {
        this.modes = modes;
        this.modes.addAll(modes);
        this.modes.addAll(modes);
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
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }

    class ModeItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        DirectionMode mode;

        ModeItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mode_image);
        }
    }

}
