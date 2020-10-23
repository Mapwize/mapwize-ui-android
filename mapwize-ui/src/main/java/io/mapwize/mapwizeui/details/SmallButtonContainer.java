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


public class SmallButtonContainer extends LinearLayout {

    List<ButtonSmall> smallButtons = new ArrayList<>();

    public SmallButtonContainer(@NonNull Context context) {
        super(context);
        initLayout(context);
    }

    public SmallButtonContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public SmallButtonContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    void initLayout(final Context context) {
        inflate(context, R.layout.mapwize_details_small_button_container, this);
    }

    public List<ButtonSmall> getSmallButtons() {
        return smallButtons;
    }

    void setSmallButtons(List<ButtonSmall> smallButtons) {
        this.smallButtons = smallButtons;
        this.removeAllViews();
        for (ButtonSmall buttonBig : this.smallButtons) {
            this.addView(buttonBig);
        }
        if (this.smallButtons.size() == 4) {
            this.setGravity(Gravity.CENTER_HORIZONTAL);
        } else {
            this.setGravity(Gravity.START);
        }
    }

}
