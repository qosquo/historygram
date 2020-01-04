package com.qosquo.historygram.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qosquo.historygram.R;
import com.qosquo.historygram.fragments.MediaFragment;
import com.qosquo.historygram.util.Global;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class HashtagsAdapter extends RecyclerView.Adapter<HashtagsAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;

    private final int[] mIds = new int[]{
            3,
            4
    };
    private final String[] mHashtags = new String[]{
            "ussr",
            "first_launch"
    };

    public HashtagsAdapter(Context context) {
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_theme, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int id = mIds[position];
        String name = "#" + mHashtags[position];

        holder.button.setText(name);
        holder.button.setOnClickListener(v -> {
            MediaFragment fragment = new MediaFragment(MediaFragment.MediaView.HASHTAG_RECENT);
            Bundle args = new Bundle();
            args.putInt(MediaFragment.ARG_ITEMID, id);
            Global.handleReplaceFragment(holder.itemView, fragment, args);
        });
    }

    @Override
    public int getItemCount() {
        return mHashtags.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button_theme);
        }
    }
}
