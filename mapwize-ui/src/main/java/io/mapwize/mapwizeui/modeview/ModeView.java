package io.mapwize.mapwizeui.modeview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.mapwize.mapwizeui.R;

public class ModeView extends FrameLayout {

    private RecyclerView recyclerView;
    private View selectionView;

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
        //selectionView = findViewById(R.id.mapwize_mode_selection_view);
        recyclerView = findViewById(R.id.mapwize_mode_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(new ModeViewAdapter());

    }

}
