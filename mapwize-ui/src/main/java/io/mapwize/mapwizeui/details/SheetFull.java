package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import io.mapwize.mapwizeui.R;

public class SheetFull extends ConstraintLayout {
    NestedScrollView nestedScrollView;
    LinearLayout childNestedScrollView;
    TextView placeTitleDetails;
    ButtonContainer buttonContainer;
    RowContainer rowContainer;
    TextView detailsTextView;
    PagerAdapter pagerAdapter;
    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private WebView detailsWebview;
    private boolean forceActivateScrolling = false;
    private boolean hasDetails;
    private ScrollRequestListener scrollRequestListener;

    public SheetFull(@NonNull Context context) {
        super(context);
        initLayout(context);
    }

    public SheetFull(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public SheetFull(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void initLayout(@NonNull Context context) {
        View.inflate(getContext(), R.layout.mapwize_details_content_full, this);
        buttonContainer = findViewById(R.id.buttonContainer);
        rowContainer = findViewById(R.id.rowContainer);
        nestedScrollView = findViewById(R.id.contentNestedScrollView);
        childNestedScrollView = findViewById(R.id.childNestedScrollView);
        detailsTextView = findViewById(R.id.detailsTextView);
        detailsWebview = findViewById(R.id.details_webview);
        WebSettings webSettings = detailsWebview.getSettings();
        webSettings.setJavaScriptEnabled(false);
        placeTitleDetails = findViewById(R.id.placeTitleDetails);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new PagerAdapter(context);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                forceActivateScrolling = hasDetails && tab.equals(tabLayout.getTabAt(1));
                scrollRequestListener.onScrollRequest();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setScrollEnabled(boolean enabled) {
        if (this.nestedScrollView != null) {
            if (!enabled) {
                this.nestedScrollView.scrollTo(0, 0);
            }
            this.nestedScrollView.setNestedScrollingEnabled(enabled);

        }
        if (this.detailsWebview != null) {
            detailsWebview.setNestedScrollingEnabled(enabled);
        }
    }

    public boolean needToScroll() {
        if (forceActivateScrolling) {
            return true;
        }
        if (this.childNestedScrollView != null) {
            int overallHeight = this.getHeight();
            int height1 = this.childNestedScrollView.getHeight() + placeTitleDetails.getHeight();
            int height2 = this.detailsWebview.getHeight() + placeTitleDetails.getHeight();
            return height1 > overallHeight || height2 > overallHeight;
        }
        return false;
    }

    public List<ButtonBig> getBigButtons() {
        return this.buttonContainer.getBigButtons();
    }

    public void setBigButtons(List<ButtonBig> buttons) {
        this.buttonContainer.setBigButtons(buttons);
    }

    public List<Row> getRows() {
        return this.rowContainer.getRows();
    }

    public void setRows(List<Row> rows) {
        this.rowContainer.setRows(rows);
    }

    public void setSubTitle(String subTitle) {
        if (this.placeTitleDetails == null) {
            return;
        }
        if (subTitle == null || subTitle.equals("")) {
            if (this.placeTitleDetails.getVisibility() != GONE) {
                this.placeTitleDetails.setVisibility(GONE);
            }
        } else {
            this.placeTitleDetails.setText(subTitle);
            if (this.placeTitleDetails.getVisibility() != VISIBLE) {
                this.placeTitleDetails.setVisibility(VISIBLE);
            }
        }
    }

    public void reset() {
        if (rowContainer != null) {
            rowContainer.setRows(new ArrayList<>());
        }
        if (buttonContainer != null) {
            buttonContainer.setBigButtons(new ArrayList<>());
        }
        if (this.viewPager != null) {
            viewPager.scrollTo(0, 0);
        }
        if (tabLayout != null) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if (tab != null) {
                tab.select();
            }
        }

    }

    public void setDetailsText(String details) {
        if (details.equals("")) {
            tabLayout.setVisibility(GONE);
            detailsTextView.setVisibility(GONE);
            viewPager.setPageScrollEnabled(false);
            hasDetails = false;
        } else {
            tabLayout.setVisibility(VISIBLE);
            viewPager.setPageScrollEnabled(true);
            detailsTextView.setVisibility(GONE);
            WebSettings webSettings = detailsWebview.getSettings();
            webSettings.setJavaScriptEnabled(false);
            detailsWebview.loadData("<div>" + details + "</div>", "text/html; charset=utf-8", "UTF-8");
            hasDetails = true;
        }
    }

    public void setScrollListener(ScrollRequestListener scrollRequestListener) {
        this.scrollRequestListener = scrollRequestListener;
    }

    interface ScrollRequestListener {
        void onScrollRequest();
    }
}
