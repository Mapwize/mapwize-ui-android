package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    private boolean isPageScrollEnabled = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPageScrollEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPageScrollEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPageScrollEnabled(boolean isPageScrollEnabled) {
        this.isPageScrollEnabled = isPageScrollEnabled;
    }
}
