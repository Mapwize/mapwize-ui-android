package io.mapwize.mapwizecomponents.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizecomponents.R;
import io.mapwize.mapwizeformapbox.api.Universe;
import io.mapwize.mapwizeformapbox.api.Venue;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;

/**
 * Universe button allows user to change the displayed universe
 * This button will be shown if we are in a venue which has more than one available universe.
 */
@SuppressWarnings("unused")
public class UniversesButton extends AppCompatImageButton {

    private AlertDialog alertDialog = null;
    private MapwizePlugin mapwizePlugin;
    private List<Universe> universes = new ArrayList<>();

    public UniversesButton(Context context) {
        super(context);
        initialize();
    }

    public UniversesButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public UniversesButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        setVisibility(View.GONE);
        // On click, an alert is shown that allow user to select a universe
        this.setOnClickListener(view -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater == null) {
                return;
            }
            final View dialogView = inflater.inflate(R.layout.mapwize_universes_alert, null);

            RecyclerView universesList = dialogView.findViewById(R.id.mapwizeUniversesList);
            UniversesAdapter universesAdapter = new UniversesAdapter(getContext());
            universesList.setAdapter(universesAdapter);
            universesAdapter.swapData(universes);
            universesAdapter.setListener(item -> {
                if (mapwizePlugin.getVenue() != null) {
                    mapwizePlugin.setUniverseForVenue(item, mapwizePlugin.getVenue());
                }
                alertDialog.dismiss();
            });
            dialogBuilder.setView(dialogView);

            alertDialog = dialogBuilder.create();
            alertDialog.show();
        });
    }

    /**
     * Set the mapwize plugin.
     * @param mapwizePlugin used to listen enter and exit event
     */
    public void setMapwizePlugin(@Nullable MapwizePlugin mapwizePlugin) {
        this.mapwizePlugin = mapwizePlugin;
        if (this.mapwizePlugin == null) {
            return;
        }
        this.mapwizePlugin.addOnVenueEnterListener(new MapwizePlugin.OnVenueEnterListener() {
            @Override
            public void onVenueEnter(@NonNull Venue venue) {
                universes = venue.getUniverses();
                showIfNeeded();
            }

            @Override
            public void willEnterInVenue(@NonNull Venue venue) {

            }
        });
        this.mapwizePlugin.addOnVenueExitListener(venue -> {
            this.universes = new ArrayList<>();
            setVisibility(View.INVISIBLE);
        });
    }

    /**
     * Show the button if it is useful
     */
    public void showIfNeeded() {
        if (universes.size() > 1) {
            setVisibility(View.VISIBLE);
        }
        else {
            setVisibility(View.GONE);
        }
    }

    /**
     * Refresh the universe
     * Useful after a access request
     */
    public void refreshVenue(Venue venue) {
        this.universes = venue.getUniverses();
        showIfNeeded();
    }

    /**
     * Hide the button
     */
    public void hide() {
        setVisibility(View.GONE);
    }

    /**
     * Adapter to display the list of universes in a recycler view
     */
    public class UniversesAdapter extends RecyclerView.Adapter<UniversesAdapter.UniverseItemViewHolder> {

        private List<Universe> mUniverses = new ArrayList<>();
        private Context mContext;
        private OnItemClickListener mListener;

        UniversesAdapter(Context context) {
            this.mContext = context;
        }

        void swapData(List<Universe> universes) {
            mUniverses = universes;
            notifyDataSetChanged();
        }

        void setListener(OnItemClickListener listener) {
            mListener = listener;
        }

        @Override
        public UniverseItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mapwize_text_item, parent, false);

            return new UniverseItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UniverseItemViewHolder holder, int position) {
            holder.itemView.setClickable(true);
            Universe universe = mUniverses.get(position);
            holder.textView.setText(universe.getName());
        }

        @Override
        public int getItemCount() {
            return mUniverses != null ? mUniverses.size() : 0;
        }

        /**
         * View holder to display each universe
         */
        class UniverseItemViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            UniverseItemViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.text_view);

                itemView.setOnClickListener(view -> {
                    int adapterPosition = getAdapterPosition();
                    if (mListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(mUniverses.get(adapterPosition));
                    }
                });
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(Universe item);
    }

}
