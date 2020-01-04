package com.qosquo.historygram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.qosquo.historygram.fragments.GalleryFragment;

public class PublishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        fragmentTransaction.replace(R.id.frameLayout_main, new GalleryFragment());
        fragmentTransaction.commit();
    }
}
