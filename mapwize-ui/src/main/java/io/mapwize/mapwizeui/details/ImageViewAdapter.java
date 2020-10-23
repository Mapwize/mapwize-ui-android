package io.mapwize.mapwizeui.details;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import io.mapwize.mapwizeui.R;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.MyViewHolder> {

    private List<String> urls = new ArrayList<>();

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mapwize_details_recycler_item, parent, false);
        return new MyViewHolder(constraintLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.chargeImage(urls.get(position));
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public void setPhotos(List<String> urls) {
        this.urls = urls;
        notifyDataSetChanged();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ConstraintLayout constraintLayout;
        ImageView imageView;
        AsyncTask<String, Void, Bitmap> downloadImageTask;

        public MyViewHolder(ConstraintLayout constraintLayout) {
            super(constraintLayout);
            this.constraintLayout = constraintLayout;
            imageView = constraintLayout.findViewById(R.id.slideImage);
        }

        void chargeImage(String url) {
            if (downloadImageTask != null) {
                downloadImageTask.cancel(true);
            }
            if (url.equals("")) {
                imageView.setImageResource(R.drawable.mapwize_details_placeholder);
                return;
            }
            downloadImageTask = new DownloadImageTask(imageView).execute(url);
        }
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}