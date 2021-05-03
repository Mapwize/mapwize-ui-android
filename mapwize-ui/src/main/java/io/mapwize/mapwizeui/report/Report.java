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
import io.mapwize.mapwizesdk.api.IssueError;
import io.mapwize.mapwizesdk.api.IssueType;
import io.mapwize.mapwizeui.R;

import static io.mapwize.mapwizesdk.api.IssueError.IssueFieldError.ERROR_CODE_REQUIRED;
import static io.mapwize.mapwizesdk.api.IssueError.IssueFieldError.ERROR_FIELD_DESCRIPTION;
import static io.mapwize.mapwizesdk.api.IssueError.IssueFieldError.ERROR_FIELD_ISSUE_TYPE_ID;
import static io.mapwize.mapwizesdk.api.IssueError.IssueFieldError.ERROR_FIELD_REPORTER_EMAIL;
import static io.mapwize.mapwizesdk.api.IssueError.IssueFieldError.ERROR_FIELD_SUMMARY;


public class Report extends LinearLayout {
    Context context;
    private TextView mapwize_issue_venueLabel;
    private TextView mapwize_issue_placeLabel;
    private ImageView mapwize_issue_backIcon;
    private GridLayout mapwize_issue_gridLayout;
    private float dp;
    private IssueTypeView selectedIssueType;
    private ImageView mapwize_issue_sendIcon;
    private ReportIssueListener reportIssueListener;
    private EditText
            mapwize_issue_emailEditText,
            mapwize_issue_summaryEditText,
            mapwize_issue_descriptionEditText;
    private TextView
            mapwize_issue_email_warning,
            mapwize_issue_issueType_warning,
            mapwize_issue_summary_warning,
            mapwize_issue_description_warning;

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
        mapwize_issue_summaryEditText = findViewById(R.id.mapwize_issue_summaryEditText);
        mapwize_issue_descriptionEditText = findViewById(R.id.mapwize_issue_descriptionEditText);
        mapwize_issue_gridLayout = findViewById(R.id.mapwize_issue_gridLayout);
        mapwize_issue_emailEditText = findViewById(R.id.mapwize_issue_emailEditText);

        mapwize_issue_email_warning = findViewById(R.id.mapwize_issue_email_warning);
        mapwize_issue_issueType_warning = findViewById(R.id.mapwize_issue_issueType_warning);
        mapwize_issue_summary_warning = findViewById(R.id.mapwize_issue_summary_warning);
        mapwize_issue_description_warning = findViewById(R.id.mapwize_issue_description_warning);

        mapwize_issue_venueLabel = findViewById(R.id.mapwize_issue_venueLabel);
        mapwize_issue_placeLabel = findViewById(R.id.mapwize_issue_placeLabel);
        mapwize_issue_backIcon = findViewById(R.id.mapwize_issue_backIcon);
        mapwize_issue_backIcon.setOnClickListener(v -> dismiss());
        mapwize_issue_sendIcon = findViewById(R.id.mapwize_issue_sendIcon);
        mapwize_issue_sendIcon.setOnClickListener(v -> {
            String email = mapwize_issue_emailEditText.getText().toString();

            String issueTypeId = null;
            if (selectedIssueType != null) {
                issueTypeId = selectedIssueType.getIssueTypeViewId();
            }
            String summary = mapwize_issue_summaryEditText.getText().toString();
            String description = mapwize_issue_descriptionEditText.getText().toString();
            reportIssueListener.reportIssue(email, summary, description, issueTypeId);

        });
        resetErrors();
    }

    public void dismiss() {
        this.mapwize_issue_gridLayout.removeAllViews();
        setVisibility(GONE);
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
            i++;
        }
    }

    public void setReportListener(ReportIssueListener reportIssueListener) {
        this.reportIssueListener = reportIssueListener;
    }

    public void setVenueName(String title) {
        if (mapwize_issue_venueLabel != null) {
            mapwize_issue_venueLabel.setText(title);
        }
    }

    public void setEmail(String email) {
        if (mapwize_issue_emailEditText != null) {
            mapwize_issue_emailEditText.setText(email);
        }
    }

    public void handleIssueError(IssueError issueError) {
        resetErrors();
        for (IssueError.IssueFieldError issueFieldError: issueError.getErrors()) {
            switch (issueFieldError.getErrorField()) {
                case ERROR_FIELD_SUMMARY:
                    handleError(issueFieldError, mapwize_issue_summary_warning);
                    break;
                case ERROR_FIELD_DESCRIPTION:
                    handleError(issueFieldError, mapwize_issue_description_warning);
                    break;
                case ERROR_FIELD_REPORTER_EMAIL:
                    handleError(issueFieldError, mapwize_issue_email_warning);
                    break;
                case ERROR_FIELD_ISSUE_TYPE_ID:
                    handleError(issueFieldError, mapwize_issue_issueType_warning);
                    break;
                default:
                    Toast.makeText(getContext(), issueError.getMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void handleError(IssueError.IssueFieldError issueFieldError, TextView warningTextView) {
        warningTextView.setVisibility(VISIBLE);
        if (issueFieldError.getErrorCode().equals(ERROR_CODE_REQUIRED)) {
            warningTextView.setText(getContext().getString(R.string.this_field_is_required));
        } else {
            warningTextView.setText(issueFieldError.getMessage());
        }
    }

    private void resetErrors() {
        mapwize_issue_email_warning.setVisibility(GONE);
        mapwize_issue_issueType_warning.setVisibility(GONE);
        mapwize_issue_summary_warning.setVisibility(GONE);
        mapwize_issue_description_warning.setVisibility(GONE);
    }

    public void clearViews() {
        mapwize_issue_emailEditText.setText("");
        mapwize_issue_summaryEditText.setText("");
        mapwize_issue_descriptionEditText.setText("");
        if (selectedIssueType != null) {
            selectedIssueType.setSelected(false);
        }
        selectedIssueType = null;
    }

    public interface ReportIssueListener {
        void reportIssue(String email, String summary, String description, String issueTypeId);
    }

}
