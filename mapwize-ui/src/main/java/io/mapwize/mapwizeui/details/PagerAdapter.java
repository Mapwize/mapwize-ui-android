package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import io.mapwize.mapwizeui.R;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {

    int count = 2;

    Context context;

    public PagerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        int resId = 0;
        if (position == 0) {
            resId = R.id.contentNestedScrollView;
        } else {
            resId = R.id.detailsView;
        }
        return collection.findViewById(resId);
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.mapwize_details_overview);
        } else {
            return context.getString(R.string.mapwize_details_details);
        }
    }
}
