/**
 * Copyright (c) 2017 Ivan Magda
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ivanmagda.yatranslate.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ivanmagda.network.core.Resource;
import com.ivanmagda.network.helper.GenericAsyncTaskLoader;
import com.ivanmagda.network.helper.GenericAsyncTaskLoader.OnStartLoadingCondition;
import com.ivanmagda.network.utils.Utils;
import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.activity.SelectLanguageActivity;
import com.ivanmagda.yatranslate.api.YandexTranslateApi;
import com.ivanmagda.yatranslate.data.TranslateFragmentState;
import com.ivanmagda.yatranslate.data.model.TranslateItem;
import com.ivanmagda.yatranslate.data.model.TranslateLangItem;
import com.ivanmagda.yatranslate.utils.AlertUtils;
import com.ivanmagda.yatranslate.utils.ArrayUtils;
import com.ivanmagda.yatranslate.utils.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static com.ivanmagda.yatranslate.Extras.EXTRA_CURRENT_LANGUAGE_ITEM_TRANSFER;
import static com.ivanmagda.yatranslate.Extras.EXTRA_SELECT_LANGUAGE_ACTIVITY_MODE_KEY_TRANSFER;
import static com.ivanmagda.yatranslate.Extras.EXTRA_SELECT_LANGUAGE_RESULT;

public class TranslateFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<TranslateItem>> {

    /**
     * TranslateFragment interface that helps to save and then restore
     * TranslateFragment state.
     */
    public interface OnTranslateFragmentStateListener {
        void onSaveState(@NonNull final TranslateFragmentState fragmentState);
    }

    public static final String TAG = TranslateFragment.class.getSimpleName();

    /**
     * Identifies a particular Loader being used in this component.
     */
    private static final int TRANSLATE_LOADER_ID = 101;

    /**
     * The request code for language selection.
     */
    private static final int SELECT_LANGUAGE_REQUEST = 1;

    private static final String ARG_TRANSLATE_FRAGMENT_STATE = "arg-translate-fragment-state";
    private static final String TRANSLATE_FRAGMENT_STATE_KEY = "state-translate";

    @BindView(R.id.btn_from_lang) Button mFromLangButton;
    @BindView(R.id.btn_swap_langs) ImageButton mSwapLangsButton;
    @BindView(R.id.btn_to_lang) Button mToLangButton;

    @BindView(R.id.et_translate_input) EditText mTranslateInput;
    @BindView(R.id.bt_translate) ImageButton mTranslateButton;

    @BindView(R.id.cv_translate_result_container) CardView mTranslateResultsContainer;
    @BindView(R.id.tv_translate_result) TextView mTranslateResultTextView;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;

    /**
     * An wrapper around translate flow items:
     * - text to translate;
     * - translate languages (source and destination);
     * - translate query results;
     */
    private TranslateFragmentState mState;

    /**
     * Listener that helps save TranslateFragment state when it's
     * about to be destroyed.
     */
    private OnTranslateFragmentStateListener mListener;

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

    @SuppressWarnings("unused")
    public static TranslateFragment newInstance(@NonNull TranslateFragmentState fragmentState) {
        TranslateFragment fragment = new TranslateFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_TRANSLATE_FRAGMENT_STATE, fragmentState);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTranslateFragmentStateListener) {
            mListener = (OnTranslateFragmentStateListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mState = savedInstanceState.getParcelable(TRANSLATE_FRAGMENT_STATE_KEY);
        } else if (getArguments() != null) {
            mState = getArguments().getParcelable(ARG_TRANSLATE_FRAGMENT_STATE);
        } else {
            mState = new TranslateFragmentState(null, new ArrayList<TranslateItem>(),
                    TranslateLangItem.defaultItem);
        }

        getLoaderManager().initLoader(TRANSLATE_LOADER_ID, null, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_LANGUAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(EXTRA_SELECT_LANGUAGE_RESULT)) {
                mState.setTranslateLangs((TranslateLangItem)
                        data.getParcelableExtra(EXTRA_SELECT_LANGUAGE_RESULT));
                updateLangButtons();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        ButterKnife.bind(this, view);

        setup();

        return view;
    }

    private void setup() {
        mTranslateInput.requestFocus();
        mTranslateInput.setText(mState.getTextToTranslate());
        mTranslateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mState.setTextToTranslate(s.toString());
            }
        });

        mSwapLangsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mState.getTranslateLangs().swap();
                updateLangButtons();
            }
        });

        mTranslateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.hideSoftKeyboard(getActivity());
                queryForTranslate();
            }
        });

        mFromLangButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLang(true);
            }
        });
        mToLangButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLang(false);
            }
        });

        updateLangButtons();
        updateResultsMessage();
    }

    /**
     * Helper function for starting SelectLanguageActivity for a result.
     *
     * @param selectFromLang defines what language we want to select.
     *                       If true => select source lang (from what we want translate).
     *                       If false => select destination lang (to what we want translate).
     */
    private void selectLang(boolean selectFromLang) {
        Intent selectLangIntent = new Intent(getActivity(), SelectLanguageActivity.class);

        selectLangIntent.putExtra(EXTRA_CURRENT_LANGUAGE_ITEM_TRANSFER, mState.getTranslateLangs());

        int selectionMode = selectFromLang
                ? SelectLanguageActivity.SELECT_FROM_LANG_MODE
                : SelectLanguageActivity.SELECT_TO_LANG_MODE;
        selectLangIntent.putExtra(EXTRA_SELECT_LANGUAGE_ACTIVITY_MODE_KEY_TRANSFER, selectionMode);

        startActivityForResult(selectLangIntent, SELECT_LANGUAGE_REQUEST);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRANSLATE_FRAGMENT_STATE_KEY, mState);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mListener != null){
            mListener.onSaveState(mState);
        }

        mListener = null;
    }

    @Override
    public Loader<List<TranslateItem>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TRANSLATE_LOADER_ID:
                return new GenericAsyncTaskLoader<>(
                        getActivity(),
                        YandexTranslateApi.getTranslation(
                                mState.getTextToTranslate(),
                                mState.getTranslateLangs()
                        ),
                        new OnStartLoadingCondition() {
                            @Override
                            public boolean isMeetConditions(Resource<?> resource) {
                                return !TextUtils.isEmpty(mState.getTextToTranslate());
                            }
                        }
                );
            default:
                throw new IllegalArgumentException("Unsupported loader with id: " + String.valueOf(id));
        }
    }

    @Override
    public void onLoadFinished(Loader<List<TranslateItem>> loader, List<TranslateItem> translateItems) {
        setLoadingIndicatorVisible(false);
        onTranslateResults(translateItems);
    }

    @Override
    public void onLoaderReset(Loader<List<TranslateItem>> loader) {
        mState.setTranslateResults(null);
        updateResultsMessage();
    }

    // Private helpers.

    private void queryForTranslate() {
        if (!Utils.isOnline(getContext())) {
            AlertUtils.showToast(getActivity(), R.string.msg_no_internet_connection);
        } else if (!mState.getTranslateLangs().isValid()) {
            AlertUtils.showToast(getActivity(), R.string.msg_language_translate_invalid);
        } else if (TextUtils.isEmpty(mState.getTextToTranslate())) {
            AlertUtils.showToast(getActivity(), R.string.msg_empty_text);
        } else {
            setLoadingIndicatorVisible(true);
            getLoaderManager().restartLoader(TRANSLATE_LOADER_ID, null, this);
        }
    }

    private void onTranslateResults(@Nullable List<TranslateItem> translateItems) {
        mState.setTranslateResults(translateItems);
        updateResultsMessage();

        if (ArrayUtils.isEmpty(mState.getTranslateResults())) {
            AlertUtils.showToast(getActivity(), R.string.msg_failed_translate);
        }
    }

    private void updateLangButtons() {
        TranslateLangItem langItem = mState.getTranslateLangs();
        mFromLangButton.setText(langItem.getFromLangName());
        mToLangButton.setText(langItem.getToLangName());
    }

    private void updateResultsMessage() {
        if (!ArrayUtils.isEmpty(mState.getTranslateResults())) {
            mTranslateResultsContainer.setVisibility(View.VISIBLE);

            StringBuilder stringBuilder = new StringBuilder(100);
            for (TranslateItem translateItem : mState.getTranslateResults()) {
                stringBuilder.append(translateItem.getTranslatedText()).append("\n");
            }

            mTranslateResultTextView.setText(stringBuilder.toString().trim());
        } else {
            mTranslateResultsContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void setLoadingIndicatorVisible(boolean visible) {
        if (visible) {
            mProgressBar.setVisibility(VISIBLE);
            mTranslateButton.setVisibility(INVISIBLE);
        } else {
            mProgressBar.setVisibility(GONE);
            mTranslateButton.setVisibility(VISIBLE);
        }
    }
}
