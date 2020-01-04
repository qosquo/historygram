package com.qosquo.historygram.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qosquo.historygram.R;
import com.qosquo.historygram.fragments.MediaFragment;
import com.qosquo.historygram.fragments.NewCollectionModalBottomSheet;
import com.qosquo.historygram.models.Collection;
import com.qosquo.historygram.repositories.CollectionsRepository;
import com.qosquo.historygram.util.Global;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class CollectionsGridAdapter extends RecyclerView.Adapter<CollectionsGridAdapter.ViewHolder> {

    private List<Collection> mCollections;
    private LayoutInflater mLayoutInflater;

    private OnClickListener mOnClickListener;

    public CollectionsGridAdapter(Context context, List<Collection> collections, @Nullable OnClickListener mOnClickListener) {
        this.mCollections = collections;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_collection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Collection collection = mCollections.get(position);

        Picasso.get()
                .load(collection.getLastMediaUrl())
                .centerCrop()
                .fit()
                .into(holder.mediaImageView);

        if (position == 0) {
            String allPostsLoc = mLayoutInflater.getContext()
                    .getResources()
                    .getString(R.string.collections_all_posts);
            holder.nameTextView.setText(allPostsLoc);
        } else {
            holder.nameTextView.setText(collection.getName());
        }

        holder.linearLayout.setOnClickListener(v -> {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(collection.getId());
            } else {
                Bundle args = new Bundle();
                Fragment fragment = new MediaFragment(MediaFragment.MediaView.COLLECTION);
                args.putInt(MediaFragment.ARG_ITEMID, collection.getId());
                Global.handleReplaceFragment(v, fragment, args);
            }
        });
        holder.linearLayout.setOnLongClickListener(v -> {
            buildDialog(collection.getId());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mCollections != null ? mCollections.size() : 0;
    }

    private void buildDialog(final int collectionId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mLayoutInflater.getContext());

        builder.setItems(R.array.collection_array, (dialog, which) -> {
            switch (which) {
                case 0:
                    showModalBottomSheet(collectionId);
                    break;
                case 1:
                    deleteCollection(collectionId);
                    break;
            }
        });

        builder.create().show();
    }

    private void showModalBottomSheet(final int collectionId) {
        NewCollectionModalBottomSheet modalBottomSheet = new NewCollectionModalBottomSheet();
        modalBottomSheet.setClickListener(name -> {
            CollectionsRepository repository = CollectionsRepository.getInstance();
            repository.renameCollection(collectionId, name);
        });

        FragmentManager manager = ((AppCompatActivity) mLayoutInflater.getContext()).getSupportFragmentManager();
        modalBottomSheet.show(manager, "renameModalSheet");
    }

    private void deleteCollection(final int collectionId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mLayoutInflater.getContext());
        builder.setMessage(R.string.message_delete_collection)
                .setPositiveButton(R.string.message_yes, (dialog, which) -> {
                    CollectionsRepository repository = CollectionsRepository.getInstance();
                    repository.deleteCollection(collectionId);
                })
                .setNegativeButton(R.string.message_no, (dialog, which) -> {});

        builder.create().show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        ImageView mediaImageView;
        TextView nameTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout_collection);

            mediaImageView = itemView.findViewById(R.id.imageView_media);
            nameTextView = itemView.findViewById(R.id.textView_collection);
        }
    }

    public interface OnClickListener {
        void onClick(final int collectionId);
    }
}
