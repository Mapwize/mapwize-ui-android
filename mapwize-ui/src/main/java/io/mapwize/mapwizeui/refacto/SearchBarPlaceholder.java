package io.mapwize.mapwizeui.refacto;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import io.mapwize.mapwizeui.R;

public class SearchBarPlaceholder extends ConstraintLayout {

    private FrameLayout menuButton;
    private FrameLayout directionButton;
    private TextView titleView;
    private Listener listener;

    public SearchBarPlaceholder(Context context) {
        super(context);
        initialize(context);
    }

    public SearchBarPlaceholder(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SearchBarPlaceholder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.mwz_search_bar_placeholder, this);
        menuButton = findViewById(R.id.mwz_search_bar_menu_button);
        directionButton = findViewById(R.id.mwz_search_bar_direction_button);
        titleView = findViewById(R.id.mwz_search_bar_text_view);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
        menuButton.setOnClickListener(v -> listener.onMenuButtonClick());
        directionButton.setOnClickListener(v -> listener.onDirectionButtonClick());
    }

    public void setMenuButtonVisible(boolean visible) {
        menuButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setDirectionButtonVisible(boolean visible) {
        directionButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setText(String text) {
        titleView.setText(text);
    }

    public interface Listener {
        void onMenuButtonClick();
        void onDirectionButtonClick();
    }

}
