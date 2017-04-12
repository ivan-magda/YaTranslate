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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.model.SelectLangListItem;
import com.ivanmagda.yatranslate.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class SelectLangAdapter extends RecyclerView.Adapter<SelectLangAdapter.LangViewHolder> {

    private static final String TAG = SelectLangAdapter.class.getSimpleName();

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(SelectLangListItem selectedListItem);
    }

    /*
     * An on-click handler that makes it easy for an Activity to interface with
     * our RecyclerView
     */
    final private ListItemClickListener mOnClickListener;

    private List<SelectLangListItem> mLangItems;
    private String mSelectedLangKey;

    /**
     * Constructor for SelectLangAdapter that accepts a list of items to display and the specification
     * for the ListItemClickListener.
     */
    public SelectLangAdapter(@Nullable List<SelectLangListItem> items,
                             @NonNull final ListItemClickListener listener) {
        mOnClickListener = listener;
        if (ArrayUtils.isEmpty(items)) {
            mLangItems = new ArrayList<>(30);
        } else {
            mLangItems = items;
        }
    }

    public SelectLangAdapter(@NonNull final ListItemClickListener listener) {
        mOnClickListener = listener;
        mLangItems = new ArrayList<>(30);
    }

    public void setSelectedLangKey(String selectedLangKey) {
        this.mSelectedLangKey = selectedLangKey;
        notifyDataSetChanged();
    }

    public List<SelectLangListItem> getLangItems() {
        return new ArrayList<>(mLangItems);
    }

    @Override
    public LangViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View listView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.select_lang_list_item, viewGroup, false);
        listView.setFocusable(true);

        return new LangViewHolder(listView);
    }

    @Override
    public void onBindViewHolder(LangViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mLangItems.size();
    }

    public void updateWithNewData(List<SelectLangListItem> newItems) {
        mLangItems.clear();

        if (!ArrayUtils.isEmpty(newItems)) {
            mLangItems = newItems;
        }

        notifyDataSetChanged();
    }

    class LangViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_lang)
        TextView mLangTextView;

        @BindView(R.id.iv_check)
        ImageView mCheckImageView;

        LangViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(mLangItems.get(clickedPosition));
        }

        /**
         * This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         *
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            SelectLangListItem item = mLangItems.get(listIndex);
            mLangTextView.setText(item.getLangName());

            if (!TextUtils.isEmpty(mSelectedLangKey)) {
                if (mSelectedLangKey.equals(item.getLangKey())) {
                    mCheckImageView.setVisibility(View.VISIBLE);
                } else {
                    mCheckImageView.setVisibility(View.GONE);
                }
            }
        }
    }
}
