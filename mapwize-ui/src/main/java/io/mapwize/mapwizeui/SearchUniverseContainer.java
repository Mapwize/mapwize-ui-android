package io.mapwize.mapwizeui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizesdk.api.MapwizeApi;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Translation;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;

public class SearchUniverseContainer extends LinearLayout {

    private RecyclerView recyclerView;
    private TextView titleView;
    private Adapter adapter;
    private Universe universe;

    public SearchUniverseContainer(Context context) {
        super(context);
        initialize(context);
    }

    public SearchUniverseContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SearchUniverseContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.mapwize_search_universe_results_container, this);
        titleView = findViewById(R.id.mwz_universe_container_title);
        recyclerView = findViewById(R.id.mwz_universe_container_recycler);
        recyclerView.setLayoutManager(new UnscrollableLayoutManager(context));
        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
    }

    public void setResults(Universe universe, List<MapwizeObject> results) {
        this.universe = universe;
        if (universe == null) {
            titleView.setVisibility(GONE);
        }
        else {
            titleView.setVisibility(VISIBLE);
            titleView.setText(universe.getName());
        }
        this.adapter.swapResult(results);
    }

    class Adapter extends RecyclerView.Adapter<ItemViewHolder> {

        private List<MapwizeObject> results = new ArrayList<>();
        private String language = "en";
        private Context context;

        void swapResult(List<MapwizeObject> results) {
            this.results = results;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.mapwize_search_result_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            Object suggestionItem = results.get(position);

            if (suggestionItem instanceof Venue) {
                Venue venue = (Venue) suggestionItem;
                Translation translation = venue.getTranslation(language);
                holder.titleView.setText(translation.getTitle());
                holder.subtitleView.setVisibility(View.GONE);
                holder.leftIcon.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_domain_black_24dp));
                holder.floorView.setVisibility(View.GONE);
                holder.leftIcon.setVisibility(View.VISIBLE);
                holder.itemView.setClickable(true);
            }

            if (suggestionItem instanceof Place) {
                Place place = (Place) suggestionItem;
                Translation translation = place.getTranslation(language);
                holder.titleView.setText(translation.getTitle());
                holder.leftIcon.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_location_on_black_24dp));
                holder.floorView.setVisibility(View.VISIBLE);
                holder.leftIcon.setVisibility(View.VISIBLE);
                if (place.getFloor() != null) {
                    NumberFormat nf = new DecimalFormat("###.###");
                    holder.floorView.setText(String.format(context.getResources().getString(R.string.floor_placeholder), nf.format(place.getFloor())));
                    holder.floorView.setVisibility(View.VISIBLE);
                }
                else {
                    holder.floorView.setVisibility(View.GONE);
                }
                if (place.getTranslation(language).getSubtitle() != null && place.getTranslation(language).getSubtitle().length() > 0) {
                    holder.subtitleView.setText(place.getTranslation(language).getSubtitle());
                    holder.subtitleView.setVisibility(View.VISIBLE);
                }
                else {
                    holder.subtitleView.setVisibility(View.GONE);
                }
                holder.itemView.setClickable(true);
            }

            if (suggestionItem instanceof Placelist) {
                Placelist placeList = (Placelist) suggestionItem;
                Translation translation = placeList.getTranslation(language);
                holder.titleView.setText(translation.getTitle());
                holder.leftIcon.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_menu_black_24dp));
                holder.floorView.setVisibility(View.GONE);
                holder.leftIcon.setVisibility(View.VISIBLE);
                if (placeList.getTranslation(language).getSubtitle() != null && placeList.getTranslation(language).getSubtitle().length() > 0) {
                    holder.subtitleView.setText(placeList.getTranslation(language).getSubtitle());
                    holder.subtitleView.setVisibility(View.VISIBLE);
                }
                else {
                    holder.subtitleView.setVisibility(View.GONE);
                }
                holder.itemView.setClickable(true);
            }
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView leftIcon;
        TextView titleView;
        TextView subtitleView;
        TextView floorView;

        ItemViewHolder(View itemView) {
            super(itemView);
            leftIcon = itemView.findViewById(R.id.suggestions_item_icon);
            titleView = itemView.findViewById(R.id.suggestions_item_title);
            subtitleView = itemView.findViewById(R.id.suggestions_item_subtitle);
            floorView = itemView.findViewById(R.id.suggestions_item_floor);
        }
    }

}
