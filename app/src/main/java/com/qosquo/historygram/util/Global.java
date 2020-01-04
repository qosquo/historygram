package com.qosquo.historygram.util;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.qosquo.historygram.R;

public class Global {

    public static void handleReplaceFragment(View view, Fragment fragment, Bundle args) {
        // Get fragment activity
        FragmentActivity activity = (FragmentActivity) view.getContext();
        // Begin fragment transaction
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();

        // Add current fragment to back stack
        fragmentTransaction.addToBackStack(null);

        // Set fragment arguments
        fragment.setArguments(args);

        // Replace the current fragment
        fragmentTransaction.replace(R.id.frameLayout_main, fragment);
        // Commit changes
        fragmentTransaction.commit();
    }

}
