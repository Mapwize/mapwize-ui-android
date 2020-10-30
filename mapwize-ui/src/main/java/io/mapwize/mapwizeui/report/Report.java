package io.mapwize.mapwizeui.report;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.mapwize.mapwizesdk.api.IssueType;
import io.mapwize.mapwizeui.R;


public class Report extends LinearLayout {
    Context context;
    private TextView mapwize_issue_placeLabel;
    private ImageView mapwize_issue_backIcon;
    private GridLayout mapwize_issue_gridLayout;
    private float dp;
    private IssueTypeView selectedIssueType;

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
        this.dp = getResources().getDisplayMetrics().density;
        EditText editText = findViewById(R.id.mapwize_issue_descriptionEditText);
        mapwize_issue_gridLayout = findViewById(R.id.mapwize_issue_gridLayout);
        mapwize_issue_placeLabel = findViewById(R.id.mapwize_issue_placeLabel);
        mapwize_issue_backIcon = findViewById(R.id.mapwize_issue_backIcon);
        mapwize_issue_backIcon.setOnClickListener(v -> {
            if (selectedIssueType != null) {
                Toast.makeText(context, "selected IssueType : " + selectedIssueType.getIssueTypeViewId(), Toast.LENGTH_SHORT).show();
            }
            setVisibility(GONE);
            this.mapwize_issue_gridLayout.removeAllViews();
        }
        );
    }

    public void setPlaceName(String name) {
        if (mapwize_issue_placeLabel != null) {
            mapwize_issue_placeLabel.setText(name);
        }
    }

    public void setIssuesTypes(List<IssueType> issuesList, String language) {
        int i = 0;
        for (IssueType issueType : issuesList) {
            IssueTypeView issueTypeView = new IssueTypeView(context, issueType.getId(), issueType.getTranslation(language).getTitle(), i == 0, v -> {
                if (selectedIssueType != null ) {
                    selectedIssueType.setSelected(false);
                }
                selectedIssueType = (IssueTypeView) v;
                selectedIssueType.setSelected(true);
            });
            mapwize_issue_gridLayout.addView(issueTypeView);
            if (i == 0) {
                selectedIssueType = issueTypeView;
                selectedIssueType.setSelected(true);
            }
            i++;
        }
    }

}
