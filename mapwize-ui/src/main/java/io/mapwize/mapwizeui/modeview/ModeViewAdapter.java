package io.mapwize.mapwizeui.modeview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import io.mapwize.mapwizeui.R;

public class ModeViewAdapter extends RecyclerView.Adapter<ModeViewAdapter.ModeItemViewHolder> {

    @Override
    public ModeItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mapwize_mode_item, parent, false);

        return new ModeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ModeItemViewHolder holder, int position) {



    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class ModeItemViewHolder extends RecyclerView.ViewHolder {

        ModeItemViewHolder(View itemView) {
            super(itemView);
        }
    }

}
