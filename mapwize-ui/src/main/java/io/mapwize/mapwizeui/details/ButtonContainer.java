package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.mapwize.mapwizeui.R;


public class ButtonContainer extends HorizontalScrollView {

    LinearLayout container;
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
        container = findViewById(R.id.bigButtonContainerInsideScroll);
    }

    void setBigButtons(List<ButtonBig> buttonsButtons) {
        this.bigButtons = buttonsButtons;
        container.removeAllViews();
        for (ButtonBig buttonBig : buttonsButtons) {
            container.addView(buttonBig);
        }
        if (buttonsButtons.size() == 4) {
            container.setGravity(Gravity.CENTER_HORIZONTAL);
        } else {
            container.setGravity(Gravity.START);
        }
    }

    public List<ButtonBig> getBigButtons() {
        return bigButtons;
    }

}
