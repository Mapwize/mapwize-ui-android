package io.mapwize.mapwizeui.report;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.mapwize.mapwizeui.R;


public class Report extends LinearLayout {
    Context context;

    public Report(@NonNull Context context) {
        super(context);
        initLayout(context);
    }

    public Report(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public Report(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initLayout(context);
    }

    void initLayout(final Context context) {
        inflate(context, R.layout.mapwize_report_issue, this);
        this.context = context;

    }

}
