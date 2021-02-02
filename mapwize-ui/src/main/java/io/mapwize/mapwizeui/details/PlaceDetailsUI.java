package io.mapwize.mapwizeui.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.mapwize.mapwizeui.R;

import static io.mapwize.mapwizeui.details.OpeningHours.changeTimezoneOfDate;

public class PlaceDetailsUI extends ConstraintLayout implements SheetFull.ScrollRequestListener, View.OnLayoutChangeListener {

    public static final float bigImageRatio = 0.3f;
    public static final float smallImageRatio = 0.15f;
    public static float halfExpandedRatio = 0.3f;
    public static float peekRatio = 0.3f;
    SheetContent sheetContent;
    RecyclerView.LayoutManager photosLayoutManager;
    private float dp;
    private SheetFull sheetFull;
    private NestedScrollView nestedScrollView;
    private BottomSheetBehavior bottomSheetBehavior;
    private float screenHeight;
    private TextView placeTitle;
    private DetailsReadyListener initalDetailsReadyListener;
    private ImageViewAdapter imageViewAdapter;
    private boolean hasImages = false;
    private Context context;
    private SlideListener slideListener;
    private ImageButton hideBottomSheetButton;
    private DetailsStateListener detailsStateListener;
    private View bottomSheet;
    private boolean didImageExpand = false;
    private boolean placeListSelected = false;
    private CardView cardView;

    public PlaceDetailsUI(@NonNull Context context) {
        super(context);
        initLayout(context);
    }

    public PlaceDetailsUI(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public PlaceDetailsUI(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.mapwize_details_place_details, this);
        this.context = context;
        bottomSheet = findViewById(R.id.bottomSheet);
        cardView = findViewById(R.id.cardView);
        hideBottomSheetButton = findViewById(R.id.hideBottomSheetButton);
        RecyclerView photosRecyclerView = findViewById(R.id.recyclerView);
        final FrameLayout sheetFrame = findViewById(R.id.sheetFrame);
        placeTitle = findViewById(R.id.placeTitle);
        nestedScrollView = findViewById(R.id.nestedScrollView);


        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetFull = new SheetFull(context);
        sheetContent = new SheetContent(context);
        sheetFrame.addView(sheetContent);
        sheetFrame.addView(sheetFull);
        sheetFull.setAlpha(0f);

        hideBottomSheetButton.setAlpha(0f);
        hideBottomSheetButton.setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));

        dp = getResources().getDisplayMetrics().density;

        //Status bar
        int statusBarHeight = getStatusBarHeightAndSetHideButton(context);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels - statusBarHeight;

        SetPhotosRecyclerView(context, photosRecyclerView);


        View.OnLayoutChangeListener layoutChangeListener = this;
        sheetContent.addConstraintLayoutChangeListener(layoutChangeListener);
        sheetContent.addOnLayoutChangeListener(layoutChangeListener);
        placeTitle.addOnLayoutChangeListener(layoutChangeListener);
        sheetFull.addOnLayoutChangeListener(layoutChangeListener);

        ViewGroup.LayoutParams sheetFullLayoutParams = sheetFull.getLayoutParams();
        sheetFullLayoutParams.height = (int) (screenHeight - placeTitle.getHeight() - bigImageRatio * screenHeight);
        sheetFull.setLayoutParams(sheetFullLayoutParams);
        sheetFull.setScrollListener(this);

        setBottomSheetBehavior(photosRecyclerView);
        cardView.setOnClickListener(this::cardViewClickListener);
    }

    private void setBottomSheetBehavior(RecyclerView photosRecyclerView) {
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setHalfExpandedRatio(halfExpandedRatio);
        bottomSheetBehavior.setPeekHeight(sheetContent.getHeight());
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.setSaveFlags(BottomSheetBehavior.SAVE_ALL);
        bottomSheetBehavior.setUpdateImportantForAccessibilityOnSiblings(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if (hasImages && !didImageExpand) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                            didImageExpand = true;
                        }
                        cardView.setRadius(8 * dp);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        cardView.setRadius(8 * dp);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        cardView.setRadius(0);
                        updateShouldScroll();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if (detailsStateListener != null) {
                            detailsStateListener.onHide();
                        }
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
                invalidate();
                requestLayout();
            }

            float imageHeight(float x, float screenSize, float imageMaxHeight, float imageHeight) {
                float half = (1.0f - halfExpandedRatio) * screenSize;
                float threeQuarters = (1.0f - peekRatio) * screenSize;
                if (x >= half) {
                    return lagrange(x, half, threeQuarters, imageHeight, 0f);
                } else {
                    return lagrange(x, 0f, half, imageMaxHeight, imageHeight);
                }
            }

            float alphaFull(float x, float screenSize) {
                if (x < screenSize / 8) {
                    return 1f;
                }
                if (x < screenSize / 2.5f) {
                    return lagrange(x, 0f, screenSize / 2.5f, 1f, 0f);
                }
                return 0f;
            }

            float alphaContent(float x, float screenSize) {
                float half = (1.0f - halfExpandedRatio) * screenSize;
                if (x > half) {
                    return 1f;
                } else if (x > screenSize / 2) {
                    return lagrange(x, screenSize / 2, half, 0f, 1f);
                }
                return 0f;
            }

            float lagrange(float x, float x0, float x1, float f0, float f1) {
                return f0 * ((x - x1) / (x0 - x1)) + f1 * ((x - x0) / (x1 - x0));
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                onSlideNotify(screenHeight - bottomSheet.getY(), screenHeight * (smallImageRatio + peekRatio));
                float viewOffset = bottomSheet.getY();
                int height = (int) imageHeight(viewOffset, screenHeight, screenHeight * bigImageRatio, screenHeight * smallImageRatio);
                float fullAlpha = alphaFull(viewOffset, screenHeight);
                sheetFull.setAlpha(fullAlpha);
                hideBottomSheetButton.setAlpha(fullAlpha);
                sheetFull.setVisibility(fullAlpha == 0 ? INVISIBLE : VISIBLE);
                hideBottomSheetButton.setVisibility(fullAlpha == 0 ? INVISIBLE : VISIBLE);
                sheetContent.setAlpha(alphaContent(viewOffset, screenHeight));
                if (height < 1) {
                    if (photosRecyclerView.getVisibility() != View.GONE) {
                        photosRecyclerView.setVisibility(View.GONE);
                    }
                } else {
                    ViewGroup.LayoutParams recyclerViewLayoutParams = photosRecyclerView.getLayoutParams();
                    recyclerViewLayoutParams.height = height;
                    photosRecyclerView.setLayoutParams(recyclerViewLayoutParams);
                    if (photosRecyclerView.getVisibility() != View.VISIBLE) {
                        photosRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void cardViewClickListener(View view) {
        if (placeListSelected) return;
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED && hasImages) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED && !hasImages) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED && !hasImages) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED && hasImages) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }
    }

    private void SetPhotosRecyclerView(Context context, RecyclerView photosRecyclerView) {
        photosLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        photosRecyclerView.setLayoutManager(photosLayoutManager);
        imageViewAdapter = new ImageViewAdapter();
        photosRecyclerView.setAdapter(imageViewAdapter);
    }

    private int getStatusBarHeightAndSetHideButton(Context context) {
        int resource = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = 0;
        boolean statusBarHidden = isStatusBarHidden(context);
        if (resource > 0 && !statusBarHidden) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resource);
        }
        if (statusBarHidden) {
            statusBarHeight = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25;
            //Setting hide button margin
            LayoutParams layoutParams = (LayoutParams) hideBottomSheetButton.getLayoutParams();
            layoutParams.topMargin = (int) ((statusBarHeight + 16) * dp);
            hideBottomSheetButton.setLayoutParams(layoutParams);
        }
        return statusBarHeight;
    }

    private void onSlideNotify(float offset, float imageOffset) {
        if (slideListener != null) {
            slideListener.onSlide(offset, imageOffset);
        }
    }

    public boolean isStatusBarHidden(Context context) {
        Window window = ((Activity) context).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int flags = lp.flags;
        return (flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
    }

    private void updateShouldScroll() {
        int height = (int) (screenHeight - placeTitle.getHeight() - bigImageRatio * screenHeight);

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED && sheetFull.needToScroll()) {
            nestedScrollView.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams nestedScrollViewLayoutParams = nestedScrollView.getLayoutParams();
            nestedScrollViewLayoutParams.height = height;
            nestedScrollView.setLayoutParams(nestedScrollViewLayoutParams);

            nestedScrollView.setNestedScrollingEnabled(true);
            sheetFull.setScrollEnabled(true);
        } else {
            nestedScrollView.setNestedScrollingEnabled(false);
            nestedScrollView.setVisibility(View.GONE);
            sheetFull.setScrollEnabled(false);
        }

    }

    public List<ButtonBig> getBigButtons() {
        return sheetFull.getBigButtons();
    }

    public void setBigButtons(List<ButtonBig> buttons) {
        sheetFull.setBigButtons(buttons);
    }

    public List<ButtonSmall> getSmallButtons() {
        return sheetContent.getSmallButtons();
    }

    public void setSmallButtons(List<ButtonSmall> buttons) {
        sheetContent.setSmallButtons(buttons);
    }

    public List<Row> getRows() {
        return this.sheetFull.getRows();
    }

    public void setRows(List<Row> rows) {
        this.sheetFull.setRows(rows);
    }

    public void setInitalDetailsReadyListener(DetailsReadyListener initalDetailsReadyListener) {
        this.initalDetailsReadyListener = initalDetailsReadyListener;
    }

    public void show() {
        reset();
        if (this.bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HALF_EXPANDED
//                && this.bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED
        ) {
            this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        invalidate();
        requestLayout();
    }

    public void setSubTitle(String subTitle) {
        this.sheetFull.setSubTitle(subTitle);
        this.sheetContent.setSubTitleVisibility(!subTitle.equals(""));
        this.sheetContent.setSubTitle(subTitle);
    }

    public void setTitle(String title) {
        this.placeTitle.setText(title);
    }

    public void reset() {
        bottomSheetBehavior.setDraggable(true);
        placeListSelected = false;
        imageViewAdapter.setPhotos(getPlaceHolderImages(new ArrayList<>()));
        this.sheetFull.reset();
        this.sheetContent.reset();
        if (photosLayoutManager != null) {
            photosLayoutManager.scrollToPosition(0);
        }
    }

    public void setPhotos(List<String> photos) {
        List<String> urls = getPlaceHolderImages(photos);
        imageViewAdapter.setPhotos(urls);
        boolean hasImages = false;
        for (String url : urls) {
            if (!url.equals("")) {
                hasImages = true;
                break;
            }
        }
        this.hasImages = hasImages;
        if (!this.hasImages && this.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (this.hasImages) {
            didImageExpand = false;
            this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }
    }

    private List<String> getPlaceHolderImages(List<String> urls) {
        if (urls.size() < 3) {
            for (int i = urls.size(); i < 3; i++) {
                urls.add("");
            }
        }
        return urls;
    }

    public void hide() {
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void setOnSlideListener(SlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public void setLoading(boolean loading) {
        this.sheetContent.setLoading(loading);
    }

    @Override
    public void onScrollRequest() {
        updateShouldScroll();
    }

    public void showPlacelist(List<Map<String, Object>> distances, DistanceItemClickListener distanceItemClickListener) {
        show();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetContent.showPlacelist(distances, distanceItemClickListener);
        bottomSheetBehavior.setDraggable(false);
        placeListSelected = true;
        sheetContent.setDistancesVisibility(true);
        invalidate();
        requestLayout();
    }

    public void setSubTitleVisibility(boolean visible) {
        sheetContent.setSubTitleVisibility(visible);
    }

    public void setStateListener(DetailsStateListener detailsStateListener) {
        this.detailsStateListener = detailsStateListener;
    }

    public void showDetails(String title, String subTitle, String details, String floor, List<String> photos, List<Map<String, Object>> openingHours, String phone, String website, String sharingLink, String timezone, List<Map<String, Object>> events, Integer capacity, DetailsReadyListener detailsReadyListener) {
        setTitle(title);
        setSubTitle(subTitle);
        setDetails(details);
        setPhotos(photos);
        setOpeningLabel(openingHours, timezone);
        updateLayer(title, floor, photos, openingHours, timezone, phone, website, sharingLink, events, capacity, detailsReadyListener);
        invalidate();
        requestLayout();
    }

    public void showDetailsForPlacelist(String title, String subTitle, DetailsReadyListener detailsReadyListener) {
        setTitle(title);
        setSubTitle(subTitle);
        updateLayerPlaceList(detailsReadyListener);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setDraggable(false);
        placeListSelected = true;

        invalidate();
        requestLayout();
    }

    private void setOpeningLabel(List<Map<String, Object>> openingHours, String timezone) {
        if (openingHours == null || openingHours.size() < 1) {
            sheetContent.setOpeningLabelVisiblity(false);
            return;
        }

        Calendar calendar = changeTimezoneOfDate(Calendar.getInstance().getTime(), TimeZone.getDefault(), TimeZone.getTimeZone(timezone));
        String label = OpeningHours.getLabel(context, openingHours, OpeningHours.getTimeInWeek(calendar));

        sheetContent.setPlaceOpeningLabel(label);
        sheetContent.setOpeningLabelVisiblity(true);
    }

    private void setDetails(String details) {
        this.sheetFull.setDetailsText(details);
    }

    private void setSmallButtonsVisibility(boolean visible) {
        this.sheetContent.setSmallButtonsVisibility(visible);
    }

    public void updateLayerPlaceList(DetailsReadyListener detailsReadyListener) {
        List<Row> rows = new ArrayList<>();
        List<ButtonBig> bigButtons = new ArrayList<>();


        List<ButtonSmall> buttonSmalls = new ArrayList<>();
        ButtonSmall directionSmallButton = new ButtonSmall(context, context.getString(R.string.mapwize_details_direction), R.drawable.mapwize_details_ic_baseline_directions_24, true, ButtonBig.DIRECTION_BUTTON, null);
        buttonSmalls.add(directionSmallButton);

        if (this.initalDetailsReadyListener != null) {
            boolean change = initalDetailsReadyListener.onReady(bigButtons, rows, buttonSmalls);
            if (change) {
                setSmallButtons(buttonSmalls);
                setBigButtons(bigButtons);
                setRows(rows);
            }
        }
        if (detailsReadyListener != null) {
            boolean change = detailsReadyListener.onReady(bigButtons, rows, buttonSmalls);
            if (change) {
                setSmallButtons(buttonSmalls);
                setBigButtons(bigButtons);
                setRows(rows);
            }
        }


        setSmallButtonsVisibility(true);
    }

    public void updateLayer(String name, String floor, List<String> photos, List<Map<String, Object>> openingHours, String timezone, String phone, String website, String sharingLink, List<Map<String, Object>> events, @Nullable Integer capacity, DetailsReadyListener detailsReadyListener) {
//        View.OnClickListener clickListener = view -> Toast.makeText(context, "default", Toast.LENGTH_SHORT).show();
        List<Row> rows = new ArrayList<>();
        Row floorRow = new Row(context, floor, R.drawable.mapwize_details_ic_baseline_menu_24, !floor.equals(""), Row.FLOOR_ROW, null);
        rows.add(floorRow);

        Calendar calendar = changeTimezoneOfDate(Calendar.getInstance().getTime(), TimeZone.getDefault(), TimeZone.getTimeZone(timezone));
        Row openingHoursRow = new OpeningHours(context, openingHours, OpeningHours.getTimeInWeek(calendar), null);
        rows.add(openingHoursRow);
        Row phoneRow = new Row(context, phone, R.drawable.mapwize_details_ic_baseline_call_24, !phone.equals(""), Row.PHONE_NUMBER_ROW, null);
        rows.add(phoneRow);
        Row websiteRow = new Row(context, website, R.drawable.mapwize_details_ic_baseline_language_24, !website.equals(""), Row.WEBSITE_ROW, null);
        rows.add(websiteRow);
        Row capacityRow = new Row(context, capacity != null ? capacity.toString() : "", R.drawable.mapwize_details_ic_baseline_people_24, capacity != null, Row.CAPACITY_ROW, null);
        rows.add(capacityRow);
        Row mapwize_details_occupancy = new Occupancy(context, "Currently occupied", events, R.drawable.mapwize_details_ic_baseline_calendar_today_24, events != null && events.size() > 0, Row.OCCUPANCY_ROW, null);
        rows.add(mapwize_details_occupancy);

        ButtonBig directionButton = new ButtonBig(context, context.getString(R.string.mapwize_details_direction), R.drawable.mapwize_details_ic_baseline_directions_24, true, ButtonBig.DIRECTION_BUTTON, null);
        ButtonBig callButton = new ButtonBig(context, context.getString(R.string.mapwize_details_call), R.drawable.mapwize_details_ic_baseline_call_24, false, ButtonBig.CALL_BUTTON, null);
        ButtonBig websiteButton = new ButtonBig(context, context.getString(R.string.mapwize_details_website), R.drawable.mapwize_details_ic_baseline_language_24, false, ButtonBig.WEBSITE_BUTTON, null);
        List<ButtonBig> bigButtons = new ArrayList<>();
        bigButtons.add(directionButton);
        bigButtons.add(callButton);
        bigButtons.add(websiteButton);
        if (!sharingLink.equals("")) {
            ButtonBig sharingLinkButton = new ButtonBig(context, context.getString(R.string.mapwize_details_share), R.drawable.mapwize_details_ic_baseline_share_24, false, ButtonBig.SHARE_BUTTON, view-> {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                String message = context.getString(R.string.share_place_text, name);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message + " " + "https://maps.mapwize.io/#" + sharingLink);

                sendIntent.putExtra(Intent.EXTRA_TITLE, "Share this Mapwize place");
                sendIntent.setType("text/plain");
                sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Intent shareIntent = Intent.createChooser(sendIntent, "Mapwize share");
                context.startActivity(shareIntent);
            });
            bigButtons.add(sharingLinkButton);
        }

        List<ButtonSmall> buttonSmalls = new ArrayList<>();
        ButtonSmall directionSmallButton = new ButtonSmall(context, context.getString(R.string.mapwize_details_direction), R.drawable.mapwize_details_ic_baseline_directions_24, true, ButtonBig.DIRECTION_BUTTON, null);
        ButtonSmall callSmallButton = new ButtonSmall(context, context.getString(R.string.mapwize_details_call), R.drawable.mapwize_details_ic_baseline_call_24, false, ButtonBig.CALL_BUTTON, null);
        buttonSmalls.add(directionSmallButton);
        buttonSmalls.add(callSmallButton);

        if (this.initalDetailsReadyListener != null) {
            boolean change = initalDetailsReadyListener.onReady(bigButtons, rows, buttonSmalls);
            if (change) {
                setSmallButtons(buttonSmalls);
                setBigButtons(bigButtons);
                setRows(rows);
            }
        }
        if (detailsReadyListener != null) {
            boolean change = detailsReadyListener.onReady(bigButtons, rows, buttonSmalls);
            if (change) {
                setSmallButtons(buttonSmalls);
                setBigButtons(bigButtons);
                setRows(rows);
            }
        }


        setSmallButtonsVisibility(true);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        int height = (sheetContent.getLayoutHeight() + placeTitle.getHeight());
        peekRatio = height / screenHeight;
        peekRatio = peekRatio < 1 ? peekRatio : 0.9f;
        halfExpandedRatio = peekRatio + smallImageRatio;
        halfExpandedRatio = halfExpandedRatio < 1 ? halfExpandedRatio : 0.9f;
        bottomSheetBehavior.setPeekHeight(height, true);
        bottomSheetBehavior.setHalfExpandedRatio(halfExpandedRatio);
        updateShouldScroll();
        onSlideNotify(screenHeight - bottomSheet.getY(), screenHeight * (smallImageRatio + peekRatio));
    }

    public interface SlideListener {
        void onSlide(float offset, float halfExpandedOffset);
    }

    public interface DetailsReadyListener {
        default boolean onReady(List<ButtonBig> buttonBigs, List<Row> rows, List<ButtonSmall> smallButtons) {
            return false;
        }
    }

    public interface DetailsStateListener {
        void onHide();
    }

    public interface DistanceItemClickListener {
        void onClick(String placeId, String venueId);
    }
}
