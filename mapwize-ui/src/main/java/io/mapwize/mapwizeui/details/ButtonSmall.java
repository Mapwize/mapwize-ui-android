package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import io.mapwize.mapwizeui.R;


public class ButtonSmall extends LinearLayout {
    public static final int OTHER = -1;
    public static final int DIRECTION_BUTTON = 0;
    public static final int CALL_BUTTON = 1;
    public static final int WEBSITE_BUTTON = 2;
    public static final int SHARE_BUTTON = 3;
    public static final int INFORMATION_BUTTON = 4;
    private Context context;
    private int iconId;
    private int buttonType = OTHER;
    private ImageView iconImageView;
    private TextView labelText;
    private LinearLayout smallButton;
    private boolean highlighted;
    private String label;

    public ButtonSmall(@NonNull Context context, String label, int icon, boolean highlighted, int buttonType, OnClickListener clickListener) {
        super(context);
        initLayout(context, label, icon, highlighted, buttonType, clickListener);
    }

    public ButtonSmall(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(
                context,
                "Direction",
                R.drawable.mapwize_details_ic_baseline_directions_24,
                false,
                OTHER,
                view -> Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
        );
    }

    public ButtonSmall(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(
                context,
                "Direction",
                R.drawable.mapwize_details_ic_baseline_directions_24,
                false,
                OTHER,
                view -> Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
        );
    }

    public int getButtonType() {
        return buttonType;
    }

    void initLayout(final Context context, String label, int iconId, boolean highlighted, int buttonType, OnClickListener clickListener) {
        inflate(context, R.layout.mapwize_details_button_small, this);
        this.context = context;
        this.buttonType = buttonType;
        smallButton = findViewById(R.id.smallButton);
        labelText = findViewById(R.id.smallButtonLabel);
        iconImageView = findViewById(R.id.smallButtonIcon);
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
            smallButton.setBackground(ContextCompat.getDrawable(context, R.drawable.mapwize_details_ripple_button));
            labelText.setTextColor(Color.WHITE);
        } else {
            iconImageView.setColorFilter(getResources().getColor(R.color.mapwize_main_color));
            smallButton.setBackground(ContextCompat.getDrawable(context, R.drawable.mapwize_details_ripple_button_voided));
            labelText.setTextColor(getResources().getColor(R.color.mapwize_main_color));
        }
    }
}
