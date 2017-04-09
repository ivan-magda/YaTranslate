package com.ivanmagda.yatranslate.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ivanmagda.yatranslate.R;

public class TranslateFragment extends Fragment {

    public static final String TAG = TranslateFragment.class.getSimpleName();

    private EditText mTranslateInput;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TranslateFragment() {
    }

    @SuppressWarnings("unused")
    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        mTranslateInput = (EditText) view.findViewById(R.id.et_translate_input);

        return view;
    }
}
