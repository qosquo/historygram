package com.qosquo.historygram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qosquo.historygram.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryGridAdapter extends RecyclerView.Adapter<GalleryGridAdapter.ViewHolder> {

    private List<String> mUrls;
    private LayoutInflater mLayoutInflater;
    //private View.OnClickListener mOnClickListener;
    private GalleryCallback mOnClickListener;
    private boolean isMultipleSelected;

    public GalleryGridAdapter(Context context, List<String> urls, GalleryCallback onClickListener, boolean isMultipleSelected) {
        this.mUrls = urls;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mOnClickListener = onClickListener;
        this.isMultipleSelected = isMultipleSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_media_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = mUrls.get(position);

        Picasso.get()
                .load("file://" + url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .centerCrop()
                .fit()
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v -> {
            mOnClickListener.onClick("file://" + url);

            if (isMultipleSelected) {
                holder.isSelected = !holder.isSelected;

                if (holder.isSelected) {
                    holder.selectedImageView
                            .setForeground(mLayoutInflater.getContext()
                                    .getDrawable(R.drawable.ic_check_circle_16));
                } else {
                    holder.selectedImageView
                            .setForeground(mLayoutInflater.getContext()
                                    .getDrawable(R.drawable.ic_check_circle_outline_16));
                }
            }
        });

        if (isMultipleSelected) {
            holder.selectedImageView.setVisibility(View.VISIBLE);
        } else {
            holder.selectedImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mUrls != null ? mUrls.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView selectedImageView;

        boolean isSelected;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_media);
            selectedImageView = itemView.findViewById(R.id.imageView_selected);

            isSelected = false;
        }
    }

    public interface GalleryCallback {
        void onClick(@NonNull String url);
    }
}
