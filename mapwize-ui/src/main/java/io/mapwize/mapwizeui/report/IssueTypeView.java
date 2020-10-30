package io.mapwize.mapwizeui.report;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.mapwize.mapwizeui.R;


public class IssueTypeView extends LinearLayout {
    Context context;
    private TextView mapwize_issue_issueType;
    private boolean selected = false;
    private String id;

    public IssueTypeView(@NonNull Context context, String id, String name, boolean selected, OnClickListener onClickListener) {
        super(context);
        initLayout(context, id, name, selected, onClickListener);
    }

    public IssueTypeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, "", "", false, null);
    }

    public IssueTypeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context, "", "", false, null);
    }

    public String getIssueTypeViewId() {
        return id;
    }

    void initLayout(final Context context, String id, String name, boolean selected, OnClickListener onClickListener) {
        inflate(context, R.layout.mapwize_issue_issuetype_view, this);
        this.context = context;
        this.id = id;
        mapwize_issue_issueType = findViewById(R.id.mapwize_issue_issueType);
        mapwize_issue_issueType.setText(name);
        mapwize_issue_issueType.setOnClickListener(v -> {
            onClickListener.onClick(this);
        });
        setActivated(selected);
    }

    public void setIssueTypeName(String name) {
        if (mapwize_issue_issueType != null) {
            mapwize_issue_issueType.setText(name);
        }
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (!selected) {
            mapwize_issue_issueType.setBackgroundResource(R.drawable.mapwize_details_ripple_button_voided);
            mapwize_issue_issueType.setTextColor(getResources().getColor(R.color.mapwize_details_row_text_color));
        } else {
            mapwize_issue_issueType.setBackgroundResource(R.drawable.mapwize_details_ripple_button);
            mapwize_issue_issueType.setTextColor(Color.WHITE);
        }
    }
}
