package com.qosquo.historygram.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.qosquo.historygram.R;
import com.qosquo.historygram.fragments.MediaFragment;
import com.qosquo.historygram.models.Media;
import com.qosquo.historygram.util.Global;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MediaGridAdapter extends RecyclerView.Adapter<MediaGridAdapter.ViewHolder> {

    private List<Media> mMedia;
    private LayoutInflater mInflater;
    private Integer mItemId;
    private MediaFragment.MediaView mediaView;

    /**
     * Media Grid Adapter base constructor which takes Context and List of Media
     * @param media List of Media
     */
    public MediaGridAdapter(Context context, List<Media> media) {
        this.mInflater = LayoutInflater.from(context);
        this.mMedia = media;
    }

    /**
     * Media Grid Adapter constructor specifically for hashtag feed
     * Takes Context and List of Media
     * @param hashtagId Hashtag ID
     * @param mediaView Hashtag feed view. Choose 'RECENT' or 'TOP' based on hashtag sorting
     */
    public MediaGridAdapter(Context context, List<Media> media, int hashtagId, MediaFragment.MediaView mediaView) {
        this.mInflater = LayoutInflater.from(context);
        this.mMedia = media;
        this.mItemId = hashtagId;
        this.mediaView = mediaView;
    }

    public MediaGridAdapter(Context context, List<Media> media, MediaFragment.MediaView mediaView) {
        this.mInflater = LayoutInflater.from(context);
        this.mMedia = media;
        this.mediaView = mediaView;
    }


    @NonNull
    @Override
    public MediaGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        View view = mInflater.inflate(R.layout.item_media_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        String json = new Gson().toJson(mMedia.get(position));
        Media media = new Gson().fromJson(json, Media.class);

        // Set media image
        Picasso.get()
                .load(media.getMediaUrl())
                .into(holder.mediaImageView);

        // Override click event listener
        holder.mediaImageView.setOnClickListener(v -> {
            Fragment fragment;
            Bundle args = new Bundle();
            args.putInt(MediaFragment.ARG_POSITION, position);
            if (mItemId != null) {
                fragment = new MediaFragment(mediaView);
                args.putInt(MediaFragment.ARG_ITEMID, mItemId);
            } else if (mediaView != null) {
                fragment = new MediaFragment(mediaView);
            } else {
                fragment = new MediaFragment();
                args.putInt(MediaFragment.ARG_ITEMID, media.getOwner().getId());
            }

            Global.handleReplaceFragment(v, fragment, args);
        });
    }

    @Override
    public int getItemCount() {
        return mMedia != null ? mMedia.size() : 0;
    }

    // Stores and recycles view as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mediaImageView = itemView.findViewById(R.id.imageView_media);
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
//            itemView.setLayoutParams(params);
        }
    }
}

