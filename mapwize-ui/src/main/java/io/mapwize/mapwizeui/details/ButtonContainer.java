package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.mapwize.mapwizeui.R;


public class ButtonContainer extends LinearLayout {

    List<ButtonBig> bigButtons = new ArrayList<>();

    public ButtonContainer(@NonNull Context context) {
        super(context);
        initLayout(context);
    }

    public ButtonContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public ButtonContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    void initLayout(final Context context) {
        inflate(context, R.layout.mapwize_details_button_container, this);
    }

    void setBigButtons(List<ButtonBig> buttonsButtons) {
        this.bigButtons = buttonsButtons;
        this.removeAllViews();
        for (ButtonBig buttonBig : buttonsButtons) {
            this.addView(buttonBig);
        }
        if (buttonsButtons.size() == 4) {
            this.setGravity(Gravity.CENTER_HORIZONTAL);
        } else {
            this.setGravity(Gravity.START);
        }
    }

    public List<ButtonBig> getBigButtons() {
        return bigButtons;
    }

}
