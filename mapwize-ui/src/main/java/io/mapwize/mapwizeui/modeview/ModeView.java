package io.mapwize.mapwizeui.modeview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizeui.R;

public class ModeView extends FrameLayout implements ModeViewAdapter.OnModeChangeListener {

    private RecyclerView recyclerView;
    private View selectionView;
    private ModeViewAdapter modeViewAdapter;
    ModeViewAdapter.OnModeChangeListener listener;

    public ModeView(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public ModeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ModeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(@NonNull Context context) {
        inflate(context, R.layout.mapwize_mode_view, this);
        recyclerView = findViewById(R.id.mapwize_mode_recycler_view);
        LinearLayoutManager lm = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);

        recyclerView.setLayoutManager(lm);
        modeViewAdapter = new ModeViewAdapter();
        modeViewAdapter.setListener(this);
        recyclerView.setAdapter(modeViewAdapter);
    }

    public void centerOnActiveMode() {
        recyclerView.scrollToPosition(modeViewAdapter.getSelectedItemIndex());
    }

    public void setListener(ModeViewAdapter.OnModeChangeListener listener) {
        this.listener = listener;
    }

    public void setMode(DirectionMode mode) {
        modeViewAdapter.setSelectedMode(mode, false);
    }

    public void setModes(List<DirectionMode> modes) {
        modeViewAdapter.swapData(modes);
    }

    @Override
    public void onModeChange(DirectionMode mode) {
        listener.onModeChange(mode);
    }
}
