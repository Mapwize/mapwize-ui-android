package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.mapwize.mapwizeui.R;

import static io.mapwize.mapwizeui.details.OpeningHours.formatHour;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.MyViewHolder> {

    private final List<List<Map<String, Object>>> openingHours;
    Context context;
    int startingDay;

    public DaysAdapter(Context context, List<Map<String, Object>> openingHoursDups, int startingDay) {
        this.context = context;
        this.startingDay = startingDay;
        this.openingHours = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            this.openingHours.add(null);
        }
        for (Map<String, Object> day : openingHoursDups) {
            int dayNumber = ((int) day.get("day") - startingDay  + 7) % 7;
            if (this.openingHours.get(dayNumber) == null) {
                this.openingHours.set(dayNumber, new ArrayList<>());
            }
            this.openingHours.get(dayNumber).add(day);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout constraintLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mapwize_details_day_recycler_item, parent, false);
        return new MyViewHolder(constraintLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.loadDay(openingHours.get(position), position);
    }

    @Override
    public int getItemCount() {
        return openingHours.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LinearLayout linearLayout;
        TextView dayName;
        TextView dayHours;

        public MyViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            this.linearLayout = linearLayout;
            dayName = linearLayout.findViewById(R.id.dayName);
            dayHours = linearLayout.findViewById(R.id.dayHours);
        }

        public void loadDay(List<Map<String, Object>> day, int position) {
            String weekday = new DateFormatSymbols().getWeekdays()[(position + startingDay ) % 7 + 1];
            dayName.setText(weekday);
            if (day == null) {
                dayHours.setText(context.getString(R.string.mapwize_details_closed));
                return;
            }
            String openings = "";
            for (Map<String, Object> span : day) {
                String open = "0000";
                Object openAtObject = span.get("open");
                if (openAtObject instanceof String) {
                    open = (String) openAtObject;
                }
                String close = "2359";
                Object closeAtObject = span.get("close");
                if (closeAtObject instanceof String) {
                    close = (String) closeAtObject;
                }
                if (open.equals("0000") && close.equals("2359")) {
                    openings += context.getString(R.string.mapwize_details_open24hours) + "\n";
                    break;
                }
                String openLabel = open.equals("1200") ? context.getString(R.string.mapwize_details_midday) : formatHour(open);
                String closeLabel = close.equals("1200") ? context.getString(R.string.mapwize_details_midday) : formatHour(close);
                if (close.equals("2359")) {
                    openings += "" + openLabel;
                    openings += " - ";
                    openings += context.getString(R.string.mapwize_details_midnight) + "\n";
                    break;
                }else if (open.equals("2359")) {
                    openings += "" + context.getString(R.string.mapwize_details_midnight);
                    openings += " - ";
                    openings += closeLabel + "\n";
                    break;
                }else {
                    openings += "" + openLabel;
                    openings += " - ";
                    openings += "" + closeLabel;
                    openings += "\n";
                }
            }
            openings = openings.substring(0, openings.length() - 1);
            dayHours.setText(openings);
        }
    }

}