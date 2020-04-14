package io.mapwize.mapwizeui;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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
    private List<SearchResponse> searchResponses = new ArrayList<>();
    private String language = "en";
    private OnItemClickListener mListener;
    private Context context;

    void setUniverses(List<Universe> universes, Universe activeUniverse) {
        this.universes = universes;
        if (this.universes.indexOf(activeUniverse) != 0) {
            this.universes.remove(activeUniverse);
            this.universes.add(0, activeUniverse);
        }
        this.searchResponses = new ArrayList<>();
        for (Universe universe : this.universes) {
            this.searchResponses.add(new SearchResponse(universe, new ArrayList<>()));
        }
        notifyDataSetChanged();
    }

    synchronized void setData(Universe universe, List<MapwizeObject> results) {
        this.searchResponses.get(this.universes.indexOf(universe)).setResults(results);
        notifyDataSetChanged();
    }

    void clearData() {
        searchResponses = new ArrayList<>();
        notifyDataSetChanged();
    }

    void setLanguage(String language) {
        this.language = language;
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

        SearchResponse searchResponse = searchResponses.get(position);

        holder.container.setResults(searchResponse.getUniverse(), searchResponse.getResults());

    }

    @Override
    public int getItemCount() {
        return searchResponses != null ? searchResponses.size() : 0;
    }

    class SearchItemViewHolder extends RecyclerView.ViewHolder {

        SearchUniverseContainer container;

        SearchItemViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.mwz_search_item_container);
        }
    }

    public interface OnItemClickListener {
        void onSearchResult(Place place, Universe universe);

        void onSearchResult(Placelist placelist);

        void onSearchResult(Venue venue);
    }
}
