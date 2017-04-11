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
import android.os.Parcelable;
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
import android.widget.Toast;

import com.ivanmagda.network.core.Resource;
import com.ivanmagda.network.helper.GenericAsyncTaskLoader;
import com.ivanmagda.network.helper.GenericAsyncTaskLoader.OnStartLoadingCondition;
import com.ivanmagda.network.utils.Utils;
import com.ivanmagda.yatranslate.Extras;
import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.activity.SelectLanguageActivity;
import com.ivanmagda.yatranslate.api.YandexTranslateApi;
import com.ivanmagda.yatranslate.data.model.TranslateItem;
import com.ivanmagda.yatranslate.data.model.TranslateLangItem;
import com.ivanmagda.yatranslate.utils.ArrayUtils;
import com.ivanmagda.yatranslate.utils.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

public class TranslateFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<TranslateItem>> {

    public interface OnTranslateFragmentResultsListener {
        void onTranslateResult(@Nullable List<TranslateItem> translateItem);
    }

    public static final String TAG = TranslateFragment.class.getSimpleName();

    /**
     * Identifies a particular Loader being used in this component.
     */
    private static final int TRANSLATE_LOADER_ID = 101;

    private static final String ARG_TRANSLATE_RESULT = "ARG_TRANSLATE_RESULT";
    private static final String TRANSLATE_INPUT_STATE_KEY = "TRANSLATE_INPUT_STATE_KEY";
    private static final String TRANSLATE_RESULT_STATE_KEY = "TRANSLATE_RESULT_STATE_KEY";
    private static final String TRANSLATE_LANG_STATE_KEY = "TRANSLATE_LANG_STATE_KEY";

    @BindView(R.id.btn_from_lang)
    Button mFromLangButton;
    @BindView(R.id.btn_swap_langs)
    ImageButton mSwapLangsButton;
    @BindView(R.id.btn_to_lang)
    Button mToLangButton;

    @BindView(R.id.et_translate_input)
    EditText mTranslateInput;
    @BindView(R.id.bt_translate)
    ImageButton mTranslateButton;

    @BindView(R.id.cv_translate_result_container)
    CardView mTranslateResultsContainer;
    @BindView(R.id.tv_translate_result)
    TextView mTranslateResultTextView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private String mTextToTranslate;
    private List<TranslateItem> mTranslateResults;
    private TranslateLangItem mTranslateLang = new TranslateLangItem("en", "ru", "English", "Russian");

    private OnTranslateFragmentResultsListener mListener;

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
    public static TranslateFragment newInstance(@Nullable List<TranslateItem> translateItems) {
        TranslateFragment fragment = new TranslateFragment();

        if (!ArrayUtils.isEmpty(translateItems)) {
            Bundle args = new Bundle();
            args.putParcelableArrayList(ARG_TRANSLATE_RESULT, new ArrayList<Parcelable>(translateItems));
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTranslateFragmentResultsListener) {
            mListener = (OnTranslateFragmentResultsListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mTextToTranslate = savedInstanceState.getString(TRANSLATE_INPUT_STATE_KEY);
            mTranslateResults = savedInstanceState.getParcelableArrayList(TRANSLATE_RESULT_STATE_KEY);
            mTranslateLang = savedInstanceState.getParcelable(TRANSLATE_LANG_STATE_KEY);
        } else if (getArguments() != null) {
            mTranslateResults = getArguments().getParcelableArrayList(ARG_TRANSLATE_RESULT);
            if (!ArrayUtils.isEmpty(mTranslateResults)) {
                TranslateItem translateItem = mTranslateResults.get(0);
                mTextToTranslate = translateItem.getTextToTranslate();
                mTranslateLang = translateItem.getTranslateLangItem();
            }
        }

        getLoaderManager().initLoader(TRANSLATE_LOADER_ID, null, this);
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
        mTranslateInput.setText(mTextToTranslate);
        mTranslateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mTextToTranslate = s.toString();
            }
        });

        mSwapLangsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTranslateLang.swap();
                updateLangButtons();
            }
        });

        mTranslateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.hideSoftKeyboard(getActivity());
                if (!TextUtils.isEmpty(mTextToTranslate)) queryForTranslate();
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

    private void selectLang(boolean selectFromLang) {
        Intent intent = new Intent(getActivity(), SelectLanguageActivity.class);

        if (selectFromLang) {
            intent.putExtra(Extras.EXTRA_SELECTED_LANGUAGE_KEY_TRANSFER, mTranslateLang.getFromLang());
        } else {
            intent.putExtra(Extras.EXTRA_SELECTED_LANGUAGE_KEY_TRANSFER, mTranslateLang.getToLang());
        }

        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(TRANSLATE_INPUT_STATE_KEY, mTextToTranslate);
        outState.putParcelableArrayList(TRANSLATE_RESULT_STATE_KEY,
                mTranslateResults != null ? new ArrayList<Parcelable>(mTranslateResults) : null);
        outState.putParcelable(TRANSLATE_LANG_STATE_KEY, mTranslateLang);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<List<TranslateItem>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TRANSLATE_LOADER_ID:
                return new GenericAsyncTaskLoader<>(
                        getActivity(),
                        YandexTranslateApi.getTranslation(mTextToTranslate, mTranslateLang),
                        new OnStartLoadingCondition() {
                            @Override
                            public boolean isMeetConditions(Resource<?> resource) {
                                return !TextUtils.isEmpty(mTextToTranslate);
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
        mTranslateResults = null;
        updateResultsMessage();
    }

    // Private helpers.

    private void queryForTranslate() {
        if (!Utils.isOnline(getContext())) {
            showToastWithMessageId(R.string.msg_no_internet_connection);
        } else {
            setLoadingIndicatorVisible(true);
            getLoaderManager().restartLoader(TRANSLATE_LOADER_ID, null, this);
        }
    }

    private void onTranslateResults(@Nullable List<TranslateItem> translateItems) {
        mTranslateResults = translateItems;
        updateResultsMessage();

        if (mListener != null) mListener.onTranslateResult(translateItems);
        if (ArrayUtils.isEmpty(translateItems))
            showToastWithMessageId(R.string.msg_failed_translate);
    }

    private void updateLangButtons() {
        mFromLangButton.setText(mTranslateLang.getFromLangName());
        mToLangButton.setText(mTranslateLang.getToLangName());
    }

    private void updateResultsMessage() {
        if (mTranslateResults != null) {
            mTranslateResultsContainer.setVisibility(View.VISIBLE);

            StringBuilder stringBuilder = new StringBuilder(100);
            for (TranslateItem translateItem : mTranslateResults) {
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

    private void showToastWithMessageId(final int stringResourceId) {
        Toast.makeText(getActivity(), stringResourceId, Toast.LENGTH_SHORT).show();
    }
}
