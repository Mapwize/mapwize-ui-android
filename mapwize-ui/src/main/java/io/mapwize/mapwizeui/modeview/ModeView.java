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

public class ModeView extends FrameLayout {

    private RecyclerView recyclerView;
    private View selectionView;
    private ModeViewAdapter modeViewAdapter;

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
        recyclerView.setAdapter(modeViewAdapter);
    }

    public void setModes(List<DirectionMode> modes) {
        modeViewAdapter.swapData(modes);
    }

}
