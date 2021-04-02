package io.mapwize.mapwizeui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Translation;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;

/**
 * Adapter used to fulfill result list on search.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchItemViewHolder> {

    private List<Universe> universes;
    private List<Integer> indexForUniverses;
    private Map<String, Universe> universeById;
    private List mSearchSuggestions = new ArrayList<>();
    private OnItemClickListener mListener;
    private String language = "en";
    private Context context;

    void swapData(List searchSuggestions) {
        mSearchSuggestions = searchSuggestions;
        notifyDataSetChanged();
    }

    void swapData(List searchSuggestions, List<Universe> universes, Universe currentUniverse) {
        Map<Universe, List<Object>> mapObjectByUniverse = new HashMap<>();
        universeById = new HashMap<>();
        this.universes = universes;
        this.indexForUniverses = new ArrayList<>();
        for (Universe u : universes) {
            universeById.put(u.getId(), u);
            mapObjectByUniverse.put(u, new ArrayList<>());
        }
        for (Object o : searchSuggestions) {
            MapwizeObject mapwizeObject = (MapwizeObject) o;
            List<Universe> mapwizeObjectUniverses = mapwizeObject.getUniverses();
            if (mapwizeObjectUniverses != null) {
                for (Universe mapwizeObjectUniverse : mapwizeObjectUniverses) {
                    if (universeById.containsKey(mapwizeObjectUniverse.getId()) &&
                            mapObjectByUniverse.containsKey(universeById.get(mapwizeObjectUniverse.getId()))) {
                        mapObjectByUniverse.get(universeById.get(mapwizeObjectUniverse.getId())).add(mapwizeObject);
                    }
                }
            } else {
                mapObjectByUniverse.get(universeById.get(universes.get(0).getId())).add(mapwizeObject);
            }
        }

        List<Universe> presentUniverse = new ArrayList<>();
        for (Map.Entry<Universe, List<Object>> entry : mapObjectByUniverse.entrySet()) {
            if (entry.getValue().size() > 0) {
                presentUniverse.add(entry.getKey());
            }
        }

        boolean shouldDisplayHeader = false;
        if (presentUniverse.size() > 1) {
            shouldDisplayHeader = true;
        }
        if (presentUniverse.size() == 1 && !presentUniverse.get(0).getId().equals(currentUniverse.getId())) {
            shouldDisplayHeader = true;
        }

        if (!shouldDisplayHeader) {
            swapData(searchSuggestions);
            return;
        }

        List displayResult = new ArrayList();
        displayResult.addAll(mapObjectByUniverse.get(currentUniverse));
        indexForUniverses.add(displayResult.size());
        universes.remove(universes.indexOf(currentUniverse));
        universes.add(0, currentUniverse);
        for (Universe u : universes) {
            List objectForCurrentUniverse = mapObjectByUniverse.get(u);
            if (objectForCurrentUniverse.size() > 0) {
                if (!currentUniverse.getId().equals(u.getId())) {
                    displayResult.add(u.getName());
                    displayResult.addAll(objectForCurrentUniverse);
                    indexForUniverses.add(displayResult.size());
                }
            }
        }

        swapData(displayResult);
    }

    void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public List<Universe> getUniverses() {
        return universes;
    }

    public void setUniverses(List<Universe> universes) {
        this.universes = universes;
    }

    public List<Integer> getIndexForUniverses() {
        return indexForUniverses;
    }

    public void setIndexForUniverses(List<Integer> indexForUniverses) {
        this.indexForUniverses = indexForUniverses;
    }

    public Map<String, Universe> getUniverseById() {
        return universeById;
    }

    public void setUniverseById(Map<String, Universe> universeById) {
        this.universeById = universeById;
    }

    public List getmSearchSuggestions() {
        return mSearchSuggestions;
    }

    void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public SearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mapwize_search_result_item, parent, false);

        return new SearchItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchItemViewHolder holder, int position) {

        Object suggestionItem = mSearchSuggestions.get(position);

        if (suggestionItem instanceof Venue) {
            Venue venue = (Venue) suggestionItem;
            Translation translation = venue.getTranslation(language);
            holder.titleView.setText(translation.getTitle());
            holder.subtitleView.setVisibility(View.GONE);
            holder.leftIcon.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.mapwize_ic_domain_black_24dp));
            holder.floorView.setVisibility(View.GONE);
            holder.leftIcon.setVisibility(View.VISIBLE);
            holder.itemView.setClickable(true);
        }

        if (suggestionItem instanceof Place) {
            Place place = (Place) suggestionItem;
            Translation translation = place.getTranslation(language);
            holder.titleView.setText(translation.getTitle());
            holder.leftIcon.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.mapwize_ic_location_on_black_24dp));
            holder.floorView.setVisibility(View.VISIBLE);
            holder.leftIcon.setVisibility(View.VISIBLE);
            if (place.getFloor() != null) {
                NumberFormat nf = new DecimalFormat("###.###");
                holder.floorView.setText(String.format(context.getResources().getString(R.string.mapwize_floor_placeholder), nf.format(place.getFloor())));
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
            holder.leftIcon.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.mapwize_ic_menu_black_24dp));
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

        if (suggestionItem instanceof String) {
            String universeName = (String) suggestionItem;
            holder.floorView.setVisibility(View.GONE);
            holder.subtitleView.setVisibility(View.GONE);
            holder.leftIcon.setVisibility(View.GONE);
            holder.titleView.setText(universeName);
            holder.itemView.setClickable(false);
        }

    }

    @Override
    public int getItemCount() {
        return mSearchSuggestions != null ? mSearchSuggestions.size() : 0;
    }

    class SearchItemViewHolder extends RecyclerView.ViewHolder {

        ImageView leftIcon;
        TextView titleView;
        TextView subtitleView;
        TextView floorView;

        SearchItemViewHolder(View itemView) {
            super(itemView);
            leftIcon = itemView.findViewById(R.id.suggestions_item_icon);
            titleView = itemView.findViewById(R.id.suggestions_item_title);
            subtitleView = itemView.findViewById(R.id.suggestions_item_subtitle);
            floorView = itemView.findViewById(R.id.suggestions_item_floor);

            itemView.setOnClickListener(view -> {
                int adapterPosition = getAdapterPosition();
                if (mListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    Object object = mSearchSuggestions.get(adapterPosition);
                    if (object instanceof Place) {
                        Universe universe = null;
                        if (indexForUniverses != null) {
                            for (int i = indexForUniverses.size() - 1; i >= 0; i--) {
                                Integer index = indexForUniverses.get(i);
                                if (adapterPosition <= index) {
                                    universe = universes.get(i);
                                }
                            }
                        }
                        if (universe == null && universeById != null) {
                            universe = universeById.get(((Place) object).getUniverses().get(0).getId());
                        }
                        mListener.onSearchResult((Place) object, universe);
                    }
                    if (object instanceof Placelist) {
                        mListener.onSearchResult((Placelist) object);
                    }
                    if (object instanceof Venue) {
                        mListener.onSearchResult((Venue) object);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onSearchResult(Place place, Universe universe);

        void onSearchResult(Placelist placelist);

        void onSearchResult(Venue venue);
    }
}
