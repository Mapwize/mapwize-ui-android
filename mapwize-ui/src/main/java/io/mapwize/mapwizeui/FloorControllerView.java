package io.mapwize.mapwizeui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.mapwize.mapwizesdk.api.Floor;

/**
 * Floor controller
 */
public class FloorControllerView extends ScrollView {

    private LinearLayout linearLayout;
    private int viewSize = 0;
    private OnFloorClickListener listener;

    public FloorControllerView(@NonNull Context context) {
        super(context);
        initLayout();
    }

    public FloorControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public FloorControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout();
    }

    public void setListener(OnFloorClickListener listener) {
        this.listener = listener;
    }

    private void initLayout() {
        this.setVerticalScrollBarEnabled(false);
        viewSize = (int)getContext().getResources().getDimension(R.dimen.mapwize_ui_floor_button_size);
        linearLayout = new LinearLayout(this.getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                viewSize,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);
        linearLayout.setVerticalGravity(Gravity.BOTTOM);
        /*linearLayout.setLayoutTransition(new LayoutTransition());
        setLayoutTransition(new LayoutTransition());
        getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);*/
        this.addView(linearLayout);
    }


    public void setFloors(@NonNull List<Floor> floors) {
        linearLayout.removeAllViews();
        List<Floor> reversedFloor = new ArrayList<>(floors);
        Collections.reverse(reversedFloor);
        for (Floor floor : reversedFloor) {
            FloorView floorView = new FloorView(getContext(), floor);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    viewSize,viewSize
            );
            params.setMargins(0,5,0,5);
            floorView.setElevation(4);
            floorView.setLayoutParams(params);
            floorView.setBackgroundResource(R.drawable.mapwize_circle_view);
            floorView.setOnClickListener(v -> {
                FloorView tv = (FloorView) v;
                Floor selectedFloor = tv.getFloor();
                this.listener.onFloorClick(selectedFloor);
            });
            linearLayout.addView(floorView);
        }
    }

    public void setLoadingFloor(@Nullable Floor floor) {
        for (int i = 0; i< linearLayout.getChildCount(); i++) {
            FloorView tv  = (FloorView) linearLayout.getChildAt(i);
            Double tvValue = tv.getFloor().getNumber();
            if (floor != null && floor.getNumber().equals(tvValue)) {
                tv.setLoading();
            }
            else {
                tv.setSelected(false);
            }
        }
    }

    public void setFloor(@Nullable Floor floor) {
        for (int i = 0; i< linearLayout.getChildCount(); i++) {
            FloorView tv  = (FloorView) linearLayout.getChildAt(i);
            Double tvValue = tv.getFloor().getNumber();
            if (floor != null && floor.getNumber().equals(tvValue)) {
                if (!isViewVisible(tv)) {
                    post(() -> smoothScrollTo(0, tv.getTop()));
                }
                tv.setSelected(true);
            } else {
                tv.setSelected(false);
            }
        }
    }

    private boolean isViewVisible(View view) {
        Rect scrollBounds = new Rect();
        getDrawingRect(scrollBounds);

        float top = view.getY();
        float bottom = top + view.getHeight();

        return scrollBounds.top < top && scrollBounds.bottom > bottom;
    }

    public interface OnFloorClickListener {
        void onFloorClick(Floor floor);
    }
}