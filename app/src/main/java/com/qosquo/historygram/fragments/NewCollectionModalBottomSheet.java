package com.qosquo.historygram.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qosquo.historygram.R;
import com.qosquo.historygram.repositories.CollectionsRepository;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewCollectionModalBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "NewCollectionModalBottom";

    public static final String ARGUMENT_ITEMID = "ARGUMENT_ITEMID";

    private EditText mNameEditText;
    private Button mDoneButton;

    private OnClickListener mOnClickListener;
    private int mItemId;

    public NewCollectionModalBottomSheet() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_collection_modal_bottom_sheet_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mDoneButton = view.findViewById(R.id.button_createNew);
        mDoneButton.setOnClickListener(v -> {
            if (mOnClickListener == null) {
                createNewCollection();
                this.dismiss();
            } else {
                mOnClickListener.onClick(mNameEditText.getText().toString());
                this.dismiss();
            }
        });

        mNameEditText = view.findViewById(R.id.editText_name);
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mDoneButton.setEnabled(true);
                } else {
                    mDoneButton.setEnabled(false);
                }
            }
        });
    }

    private void createNewCollection() {
        CollectionsRepository repository = CollectionsRepository.getInstance();
        String name = mNameEditText.getText().toString();

        repository.newCollection(name, mItemId);
    }

    public void setClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setItemId(int itemId) {
        this.mItemId = itemId;
    }

    public interface OnClickListener {
        void onClick(@NonNull final String name);
    }
}
