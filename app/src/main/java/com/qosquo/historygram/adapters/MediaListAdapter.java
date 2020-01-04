package com.qosquo.historygram.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ablanco.zoomy.Zoomy;
import com.androidessence.lib.RichTextView;
import com.google.gson.Gson;
import com.historygram.api.Api;
import com.qosquo.historygram.ArticleActivity;
import com.qosquo.historygram.CreateArticleActivity;
import com.qosquo.historygram.MediaActivity;
import com.qosquo.historygram.R;
import com.qosquo.historygram.fragments.CollectionsModalBottomSheet;
import com.qosquo.historygram.fragments.MediaFragment;
import com.qosquo.historygram.fragments.UserFragment;
import com.qosquo.historygram.models.Media;
import com.qosquo.historygram.repositories.UserRepository;
import com.qosquo.historygram.util.Global;
import com.qosquo.historygram.viewmodels.MediaViewModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import me.relex.circleindicator.CircleIndicator;

public class MediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.ViewHolder> {

    private MediaViewModel viewModel;

    private List<Media> mMedia;
    private LayoutInflater mInflater;

    public MediaListAdapter(Context context, List<Media> media) {
        this.mInflater = LayoutInflater.from(context);
        this.mMedia = media;

        viewModel = ViewModelProviders.of((FragmentActivity) context).get(MediaViewModel.class);
    }

    @NonNull
    @Override
    public MediaListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        View view = mInflater.inflate(R.layout.item_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // - get element from your dataset at this position
        String json = new Gson().toJson(mMedia.get(position));
        Media media = new Gson().fromJson(json, Media.class);

        // Set click event for user linear layout
        holder.userLinearLayout.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt(UserFragment.ARGUMENT_USERID, media.getOwner().getId());
            Global.handleReplaceFragment(holder.itemView, new UserFragment(), args);
        });

        // Load profile picture
        Picasso.get()
                .load(media.getOwner().getProfilePicture())
                .into(holder.profileImageView);

        // Set username
        holder.usernameTextView.setText(media.getOwner().getUsername());

        // Set if user is verified
        if (media.getOwner().getRole() < 2) {
            holder.verifiedImageView.setVisibility(View.VISIBLE);
            if (media.getOwner().getRole() == 0) {
                holder.verifiedImageView.setColorFilter(mInflater.getContext()
                        .getResources()
                        .getColor(R.color.colorAccent, mInflater.getContext().getTheme()));
            }
            if (media.getOwner().getRole() == 1) {
                holder.verifiedImageView.setColorFilter(mInflater.getContext()
                        .getResources()
                        .getColor(R.color.blue, mInflater.getContext().getTheme()));
            }
        }

        // Check if media has more than one child
        if (media.getChildren().size() == 1) {
            // Set media image
            Picasso.get()
                    .load(media.getMediaUrl())
                    .into(holder.mediaImageView);

            Zoomy.Builder builder = new Zoomy.Builder((AppCompatActivity) mInflater.getContext()).target(holder.mediaImageView);
            builder.register();
        } else if (media.getChildren().size() > 1) {
            // Hide default media image view
            holder.mediaImageView.setVisibility(View.GONE);

            // Show viewpager
            holder.mediaViewPager.setVisibility(View.VISIBLE);
            holder.circleIndicator.setVisibility(View.VISIBLE);

            // Get media children from repository
            viewModel.children(media.getChildren());
            // Update viewpager
            viewModel.getChildren().observe((LifecycleOwner) mInflater.getContext(), strings -> {
                ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(mInflater.getContext(), strings);
                holder.mediaViewPager.setAdapter(pagerAdapter);
                holder.circleIndicator.setViewPager(holder.mediaViewPager);
            });
        }

        // Check if user liked this post
        if (media.isUserLiked()) {
            holder.likeButtonView.setBackgroundResource(R.drawable.ic_like_24);
            holder.likeButtonView.getBackground().setTint(mInflater.getContext()
                    .getColor(R.color.red));
        } else {
            holder.likeButtonView.setBackgroundResource(R.drawable.ic_like_outline_24);
            holder.likeButtonView.getBackground().setTint(mInflater.getContext()
                    .getColor(R.color.black));
        }

        holder.likeButtonView.setOnClickListener(v -> {
            viewModel.like(media.getOwner().getId(), media.getId());

            if (media.isUserLiked()) {
                holder.likeButtonView.setBackgroundResource(R.drawable.ic_like_outline_24);
                holder.likeButtonView.getBackground().setTint(mInflater.getContext()
                        .getColor(R.color.black));
                media.setUserLiked(false);

                int count = (int) (media.getLikesCount() - 1) < 0 ? 0 : (int) media.getLikesCount() - 1;
                String likesString = mInflater
                        .getContext()
                        .getString(R.string.media_likes) + ": " + count;
                holder.likesTextView.setText(likesString);
            } else {
                holder.likeButtonView.setBackgroundResource(R.drawable.ic_like_24);
                holder.likeButtonView.getBackground().setTint(mInflater.getContext()
                        .getColor(R.color.red));
                media.setUserLiked(true);

                int count = (int) (media.getLikesCount() == 0 ? 1 : media.getLikesCount());
                String likesString = mInflater
                        .getContext()
                        .getString(R.string.media_likes) + ": " + count;
                holder.likesTextView.setText(likesString);

            }
        });

        // Check if user has saved this media to a colleciton
        if (media.isUserSaved()) {
            holder.saveButtonView.setBackgroundResource(R.drawable.ic_bookmark_24);
        } else {
            holder.saveButtonView.setBackgroundResource(R.drawable.ic_bookmark_border_24);
        }

        // Set Save view click listener
        holder.saveButtonView.setOnClickListener(v -> {
            if (media.isUserSaved()) {
                // If media is already saved, then remove it from collections
                viewModel.remove(null, media.getId());

                holder.saveButtonView.setBackgroundResource(R.drawable.ic_bookmark_border_24);
                media.setUserSaved(false);
            } else {
                // If media is not save, then add it to a default collection
                viewModel.save(null, media.getId());

                holder.saveButtonView.setBackgroundResource(R.drawable.ic_bookmark_24);
                media.setUserSaved(true);
            }
        });
        holder.saveButtonView.setOnLongClickListener(v -> {
            CollectionsModalBottomSheet modalBottomSheet = new CollectionsModalBottomSheet();
            modalBottomSheet.setItemId(media.getId());
            FragmentManager manager = ((AppCompatActivity) mInflater.getContext()).getSupportFragmentManager();
            modalBottomSheet.show(manager, "saveModalMenu");
            return true;
        });

        // Set likes
        String likesString = mInflater
                .getContext()
                .getString(R.string.media_likes) + ": " + media.getLikesCount();
        holder.likesTextView.setText(likesString);

        // Set caption
        holder.setCaption(media.getOwner().getUsername(), media.getCaption());

        SimpleDateFormat dateFormat;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dateFormat = new SimpleDateFormat("dd MMM yyyy G",
                    mInflater.getContext().getResources().getConfiguration().getLocales().get(0));
        } else {
            dateFormat = new SimpleDateFormat("dd MMM yyyy G",
                    mInflater.getContext().getResources().getConfiguration().locale);
        }
        String date = dateFormat.format(new Date(media.getTimestamp()));
        holder.timestamp.setText(date);

        holder.moreButton.setOnClickListener(v -> buildDialog(media));

        // Check if this post has Article
        if (media.getArticle() != null) {
            holder.articleButton.setVisibility(View.VISIBLE);
            holder.articleButton.setOnClickListener(v -> {
                Intent intent = new Intent(mInflater.getContext(), ArticleActivity.class);
                intent.putExtra(ArticleActivity.EXTRA_ARTICLEID, media.getArticle());
                mInflater.getContext().startActivity(intent);
            });
        } else {
            holder.articleButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mMedia != null ? mMedia.size() : 0;
    }

    public void update(List<Media> media) {
        mMedia = media;
        notifyDataSetChanged();
    }

    private void buildDialog(final Media media) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());

        if (media.getOwner().getId().equals(Api.getConfig().getUserId())) {
            if (media.getArticle() != null) {
                builder.setItems(R.array.media_popup_hasarticle, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // 'Edit' item
                            edit(media);
                            break;
                        case 1:
                            // 'Remove Article' item
                            removeArticle(media.getId());
                            break;
                        case 2:
                            // 'Delete' item
                            delete(media.getId());
                            break;
                    }
                });
            } else {
                builder.setItems(R.array.media_popup_noarticle, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // 'Edit' item
                            edit(media);
                            break;
                        case 1:
                            // 'Add Article' item
                            addArticle(media.getId());
                            break;
                        case 2:
                            // 'Delete' item
                            delete(media.getId());
                            break;
                    }
                });
            }

        } else {
            if (media.getOwner().isUserFollowed()) {
                builder.setItems(R.array.media_popup_subscribed, (dialog, which) -> {
                    if (which == 0) {
                        // 'Unfollow' item
                        follow(media.getOwner().getId());
                    }
                });
            } else {
                builder.setItems(R.array.media_popup_unsubscribed, (dialog, which) -> {
                    if (which == 0) {
                        // 'Follow' item
                        follow(media.getOwner().getId());
                    }
                });
            }
        }

        builder.create().show();
    }

    private void edit(final Media media) {
        Intent intent = new Intent(mInflater.getContext(), MediaActivity.class);
        intent.putExtra(MediaActivity.EXTRA_MEDIA, media);
        mInflater.getContext().startActivity(intent);
    }

    private void addArticle(final int id) {
        Intent intent = new Intent(mInflater.getContext(), CreateArticleActivity.class);
        intent.putExtra(CreateArticleActivity.EXTRA_MEDIAID, id);
        ((AppCompatActivity) mInflater.getContext())
                .startActivityForResult(intent, MediaFragment.ADD_ARTICLE_REQUEST_CODE);
    }

    private void removeArticle(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
        builder.setMessage(R.string.message_delete_article)
               .setPositiveButton(R.string.message_yes, (dialog, which) ->
                       viewModel.removeArticle(id))
               .setNegativeButton(R.string.message_no, (dialog, which) -> {});

        builder.create().show();
        //viewModel.removeArticle(id);
    }

    private void delete(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
        builder.setMessage(R.string.message_delete_media)
                .setPositiveButton(R.string.message_yes, (dialog, which) ->
                        viewModel.delete(id))
                .setNegativeButton(R.string.message_no, (dialog, which) -> {});

        builder.create().show();
        //viewModel.delete(id);
    }

    private void follow(final int userId) {
        UserRepository.getInstance().follow(userId);
    }

    // Stores and recycles view as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout userLinearLayout;
        ImageView profileImageView;
        TextView usernameTextView;
        ImageView verifiedImageView;

        ImageView mediaImageView;
        ViewPager mediaViewPager;
        CircleIndicator circleIndicator;

        View likeButtonView;
        View commentButtonView;
        View saveButtonView;

        TextView likesTextView;
        RichTextView captionTextView;
        TextView timestamp;

        ImageButton moreButton;
        Button articleButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userLinearLayout = itemView.findViewById(R.id.linearLayout_user);
            profileImageView = itemView.findViewById(R.id.imageView_profile);
            usernameTextView = itemView.findViewById(R.id.textView_username);
            verifiedImageView = itemView.findViewById(R.id.imageView_verified);

            mediaImageView = itemView.findViewById(R.id.imageView_media);
            mediaViewPager = itemView.findViewById(R.id.viewPager_media);
            circleIndicator = itemView.findViewById(R.id.circleIndicator);

            likeButtonView = itemView.findViewById(R.id.view_Like);
            commentButtonView = itemView.findViewById(R.id.view_Comment);
            saveButtonView = itemView.findViewById(R.id.view_Save);

            likesTextView = itemView.findViewById(R.id.textView_likes);
            captionTextView = itemView.findViewById(R.id.textView_caption);

            timestamp = itemView.findViewById(R.id.textView_timestamp);

            moreButton = itemView.findViewById(R.id.button_more);
            articleButton = itemView.findViewById(R.id.button_article);
        }

        void setCaption(String owner, String caption) {
            String text = getFullCaption(owner, caption);
            captionTextView.setText(text);
            int randomEndIndex = new Random().nextInt((100-75) + 1) + 100;

            if (captionTextView.getText().length() > randomEndIndex) {
                String moreString = mInflater.getContext().getString(R.string.read_more);
                String suffix = "... " + moreString;
                String shortCaption = text.substring(0, randomEndIndex - suffix.length() - 3) + suffix;
                captionTextView.setText(shortCaption);
                captionTextView.setOnClickListener(v -> {
                    captionTextView.setText(getFullCaption(owner, caption));
                    captionTextView.formatSpan(0, owner.length(), RichTextView.FormatType.BOLD);
                });
            }

            captionTextView.formatSpan(0, owner.length(), RichTextView.FormatType.BOLD);
        }

        private String getFullCaption(String owner, String caption) {
            return owner + " " + caption;
        }
    }
}

