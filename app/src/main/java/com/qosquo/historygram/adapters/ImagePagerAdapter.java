package com.qosquo.historygram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.ablanco.zoomy.Zoomy;
import com.qosquo.historygram.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.santeri.wvp.WrappingViewPager;

public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private List<String> media;

    private int mCurrentPosition = -1;

    public ImagePagerAdapter(Context context, List<String> media) {
        this.context = context;
        this.media = media;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, null);
        ImageView imageView = view.findViewById(R.id.imageView_media);
        Picasso.get()
                .load(media.get(position))
                .into(imageView);

        Zoomy.Builder builder = new Zoomy.Builder(((AppCompatActivity) context)).target(imageView);
        builder.register();

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Zoomy.unregister(((View) object));
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return media != null ? media.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);

        if (!(container instanceof WrappingViewPager)) {
            return;
        }

        if (position != mCurrentPosition) {
            WrappingViewPager pager = ((WrappingViewPager) container);
            mCurrentPosition = position;
            pager.onPageChanged((View) object);
        }
    }
}
