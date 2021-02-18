package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.mapwize.mapwizeui.R;


public class RowContainer extends LinearLayout {

    List<Row> rows = new ArrayList<>();

    public RowContainer(@NonNull Context context) {
        super(context);
        inflate(getContext(), R.layout.mapwize_details_row_container, this);
        initLayout(context);
    }

    public RowContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.mapwize_details_row_container, this);
        initLayout(context);
    }

    public RowContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(getContext(), R.layout.mapwize_details_row_container, this);
        initLayout(context);
    }

    void initLayout(final Context context) {
        this.setOrientation(VERTICAL);
    }

    void setRows(List<Row> rows) {
        this.rows = rows;
        this.removeAllViews();
        for (Row buttonBig : rows) {
            this.addView(buttonBig);
        }
    }

    public List<Row> getRows() {
        return rows;
    }
}
