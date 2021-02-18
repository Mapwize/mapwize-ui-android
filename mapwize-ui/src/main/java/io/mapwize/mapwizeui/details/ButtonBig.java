package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import io.mapwize.mapwizeui.R;


public class ButtonBig extends LinearLayout {
    public static final int OTHER = -1;
    public static final int DIRECTION_BUTTON = 0;
    public static final int CALL_BUTTON = 1;
    public static final int WEBSITE_BUTTON = 2;
    public static final int SHARE_BUTTON = 3;
    private ImageView iconImageView;
    private View iconCircle;
    private Context context;
    private int iconId;
    private int buttonType = OTHER;
    private String label;
    private boolean highlighted;
    private TextView labelText;

    public ButtonBig(@NonNull Context context, String label, int icon, boolean highlighted, int buttonType, OnClickListener clickListener) {
        super(context);
        initLayout(context, label, icon, highlighted, buttonType, clickListener);
    }

    public ButtonBig(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, "Direction", R.drawable.mapwize_details_ic_baseline_directions_24, false, OTHER, view -> {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        });
    }

    public ButtonBig(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context, "Direction", R.drawable.mapwize_details_ic_baseline_directions_24, false, OTHER, view -> {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        });
    }

    public int getButtonType() {
        return buttonType;
    }

    void initLayout(final Context context, String label, int iconId, boolean highlighted, int buttonType, OnClickListener clickListener) {
        inflate(context, R.layout.mapwize_details_button_big, this);
        this.context = context;
        this.buttonType = buttonType;
        this.labelText = findViewById(R.id.bigButtonLabel);
        iconImageView = findViewById(R.id.bigButtonIcon);
        iconCircle = findViewById(R.id.iconCircle);
        this.setOnClickListener(clickListener);
        setLabel(label);
        setIcon(iconId);
        setHighlighted(highlighted);
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
        this.labelText.setText(label);
    }

    public int getIcon() {
        return this.iconId;
    }

    public void setIcon(int iconId) {
        this.iconId = iconId;
        this.iconImageView.setImageResource(iconId);
    }

    public boolean getHighlighted() {
        return this.highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        if (highlighted) {
            iconImageView.setColorFilter(Color.WHITE);
            iconCircle.setBackground(ContextCompat.getDrawable(context, R.drawable.mapwize_details_circle_view));
        } else {
            iconImageView.clearColorFilter();
            iconCircle.setBackground(ContextCompat.getDrawable(context, R.drawable.mapwize_details_circle_view_voided));
        }
    }
}
