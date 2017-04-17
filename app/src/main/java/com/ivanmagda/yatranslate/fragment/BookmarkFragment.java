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

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.adapter.BookmarkPagerAdapter;
import com.ivanmagda.yatranslate.utilities.FragmentUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarkFragment extends Fragment {

    public static final String TAG = BookmarkFragment.class.getSimpleName();

    @BindView(R.id.vpPager) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout mTabLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookmarkFragment() {
    }

    @SuppressWarnings("unused")
    public static BookmarkFragment newInstance() {
        return new BookmarkFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentUtils.setElevationForActionBar(getActivity(), 0f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        ButterKnife.bind(this, view);

        FragmentUtils.setTitle(getActivity(), R.string.fragment_bookmark_title);

        // getChildFragmentManager() allows to use fragments hosted by BookmarkFragment,
        // rather than ones hosted by the activity as a whole.
        FragmentPagerAdapter adapter = new BookmarkPagerAdapter(getActivity(),
                getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        FragmentUtils.restoreDefaultElevationForActionBar(getActivity());
    }
}
