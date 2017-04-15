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
package com.ivanmagda.yatranslate.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.model.core.TranslateItem;
import com.ivanmagda.yatranslate.model.core.TranslateLangItem;
import com.ivanmagda.yatranslate.utils.database.TranslateItemDbUtils;
import com.ivanmagda.yatranslate.viewmodel.TranslateItemViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ivanmagda.yatranslate.data.TranslateContract.HistoryEntry;

public class TranslateHistoryAdapter
        extends RecyclerView.Adapter<TranslateHistoryAdapter.TranslateHistoryAdapterViewHolder> {

    /**
     * The interface that receives on click's messages.
     */
    public interface TranslateHistoryAdapterOnClickListener {

        /**
         * @param position Index of the selected item.
         * @param selectedItem Selected TranslateItem.
         */
        void onRowClick(final int position, @NonNull final TranslateItem selectedItem);

        /**
         * @param selectedItem Selected TranslateItem.
         */
        void onToggleFavoriteClick(@NonNull final TranslateItem selectedItem);
    }

    private Cursor mCursor;
    private TranslateHistoryAdapterOnClickListener mClickListener;

    /**
     * Creates a TranslateHistoryAdapter.
     */
    public TranslateHistoryAdapter() {
        this.mCursor = null;
        this.mClickListener = null;
    }

    /**
     * Creates a TranslateHistoryAdapter.
     *
     * @param cursor        The cursor.
     * @param clickListener The on-click handler for this adapter. This single handler is called
     *                      when an item is clicked.
     */
    public TranslateHistoryAdapter(@Nullable final Cursor cursor,
                                   @Nullable final TranslateHistoryAdapterOnClickListener clickListener) {
        this.mCursor = cursor;
        this.mClickListener = clickListener;
    }

    public void setListItemClickListener(TranslateHistoryAdapterOnClickListener onClickListener) {
        this.mClickListener = onClickListener;
    }

    @Override
    public TranslateHistoryAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View listItem = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_bookmark_item, viewGroup, false);
        listItem.setFocusable(true);

        return new TranslateHistoryAdapterViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(final TranslateHistoryAdapterViewHolder viewHolder, int position) {
        viewHolder.bindAt(position);
    }

    @Override
    public int getItemCount() {
        return (mCursor == null ? 0 : mCursor.getCount());
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    class TranslateHistoryAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @BindView(R.id.bt_toggle_favorite) ImageButton mToggleFavoriteButton;
        @BindView(R.id.tv_to_translate) TextView mTextToTranslateTextView;
        @BindView(R.id.tv_translated) TextView mTranslatedTextView;
        @BindView(R.id.tv_translate_langs) TextView mTranslateLangsTextView;

        TranslateHistoryAdapterViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
            mToggleFavoriteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener == null) {
                return;
            }

            int position = getAdapterPosition();

            if (mCursor.moveToPosition(position)) {
                TranslateItem translateItem = TranslateItemDbUtils.buildFromCursor(mCursor);
                assert translateItem != null;

                if (view.getId() == R.id.bt_toggle_favorite) {
                    mClickListener.onToggleFavoriteClick(translateItem);
                } else {
                    mClickListener.onRowClick(position, translateItem);
                }
            }
        }

        void bindAt(final int position) {
            mCursor.moveToPosition(position);

            final Context context = itemView.getContext();
            TranslateItem translateItem = TranslateItemDbUtils.buildFromCursor(mCursor);

            assert translateItem != null;

            TranslateItemViewModel viewModel = new TranslateItemViewModel(translateItem, context);

            mToggleFavoriteButton.setColorFilter(viewModel.getFavoriteColor());
            mTextToTranslateTextView.setText(translateItem.getTextToTranslate());
            mTranslatedTextView.setText(translateItem.getTranslatedText());
            mTranslateLangsTextView.setText(viewModel.getFormattedLangKeys());
        }
    }
}
