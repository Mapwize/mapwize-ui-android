package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.mapwize.mapwizeui.R;

public class SheetContent extends ConstraintLayout {
    ConstraintLayout constraintLayout;
    private TextView placeSubTitle;
    private SmallButtonContainer smallButtonContainer;
    private ProgressBar progress_loader;
    private TextView placeOpeningLabelTextView;
    private View linearContentSheet;
    private DistancesAdapter distanceAdapter;
    private GridView gridView;

    public SheetContent(@NonNull Context context) {
        super(context);
        initLayout(context);
    }

    public SheetContent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public SheetContent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    public void setPlaceOpeningLabel(String placeOpeningLabel) {
        if (placeOpeningLabelTextView != null) {
            placeOpeningLabelTextView.setText(placeOpeningLabel);
            placeOpeningLabelTextView.setVisibility(VISIBLE);
        }
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.mapwize_details_content_basic, this);
        constraintLayout = findViewById(R.id.contentBasicLayout);
        placeSubTitle = findViewById(R.id.placeSubTitle);
        smallButtonContainer = findViewById(R.id.smallButtonContainer);
        progress_loader = findViewById(R.id.progress_loader);
        placeOpeningLabelTextView = findViewById(R.id.placeOpeningLabel);
        linearContentSheet = findViewById(R.id.linearContentSheet);
        gridView = findViewById(R.id.gridView);
        distanceAdapter = new DistancesAdapter(context);
        gridView.setAdapter(distanceAdapter);
    }

    public int getLayoutHeight() {
        if (this.constraintLayout == null) {
            return 0;
        }
        return this.constraintLayout.getHeight();
    }

    public void setSubTitle(String subTitle) {
        if (this.placeSubTitle == null) {
            return;
        }
        this.placeSubTitle.setText(subTitle);
    }

    public void setSubTitleVisibility(boolean visible) {
        if (this.placeSubTitle == null) {
            return;
        }
        placeSubTitle.setVisibility(visible ? VISIBLE : GONE);
    }

    public List<ButtonSmall> getSmallButtons() {
        return smallButtonContainer.getSmallButtons();
    }

    public void setSmallButtons(List<ButtonSmall> buttons) {
        smallButtonContainer.setSmallButtons(buttons);
    }

    public void setLoading(boolean loading) {
        progress_loader.setVisibility(loading ? VISIBLE : GONE);
        if (loading) {
            setSmallButtonsVisibility(false);
            if (placeOpeningLabelTextView.getVisibility() == VISIBLE) {
                placeOpeningLabelTextView.setVisibility(INVISIBLE);
            }
            if (placeSubTitle.getVisibility() == VISIBLE) {
                placeSubTitle.setVisibility(INVISIBLE);
            }
        }
    }

    public void setSmallButtonsVisibility(boolean visible) {
        this.smallButtonContainer.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    public void setOpeningLabelVisiblity(boolean b) {
        placeOpeningLabelTextView.setVisibility(b ? VISIBLE : GONE);
    }

    public void addConstraintLayoutChangeListener(OnLayoutChangeListener layoutChangeListener) {
        if (this.linearContentSheet != null) {
            linearContentSheet.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                if (gridView.getVisibility() == VISIBLE) {
                    return;
                }
                layoutChangeListener.onLayoutChange(null, -1, -1, -1, -1, -1, -1, -1, -1);
            });
            linearContentSheet.addOnLayoutChangeListener(layoutChangeListener);
        }
    }

    public void showPlacelist(List<Map<String, Object>> distances, PlaceDetailsUI.DistanceItemClickListener distanceItemClickListener) {
        if (distanceAdapter != null) {
            smallButtonContainer.setVisibility(GONE);
            setOpeningLabelVisiblity(false);
            setSubTitle("");
            setSubTitleVisibility(false);
            distanceAdapter.showPlacelist(distances, distanceItemClickListener);
        }
    }

    void setDistancesVisibility(boolean visible) {
        gridView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void reset() {
        setDistancesVisibility(false);
    }
}
