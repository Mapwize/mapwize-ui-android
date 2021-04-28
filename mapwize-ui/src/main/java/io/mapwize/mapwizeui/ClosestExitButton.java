package io.mapwize.mapwizeui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

/**
 * Follow user button allows user to change the follow user mode
 */
public class ClosestExitButton extends AppCompatImageButton {

    private ClosestExitButtonListener listener;
    private int closestExitImageResource = R.drawable.baseline_exit_to_app_24;
    private int defaultColor = Color.BLACK;
    private int activeColor = R.color.mapwize_main_color;

    public ClosestExitButton(Context context) {
        super(context);
        initialize(context);
    }

    public ClosestExitButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ClosestExitButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setBackgroundResource(R.drawable.mapwize_circle_view);
        setImageResource(closestExitImageResource);
        setColorFilter(Color.BLACK);
        setOnClickListener(view -> {
            listener.onClosestSortieClick();
        });
    }

    public void setListener(ClosestExitButtonListener listener) {
        this.listener = listener;
    }

    public interface ClosestExitButtonListener {
        void onClosestSortieClick();
    }
}
