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
import com.ivanmagda.yatranslate.fragment.dummy.DummyContent;
import com.ivanmagda.yatranslate.utils.FragmentUtils;

public class BookmarkFragment extends Fragment
        implements BookmarkListFragment.OnListFragmentInteractionListener {

    public static final String TAG = BookmarkFragment.class.getSimpleName();

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
    public void onAttach(Context context) {
        super.onAttach(context);
        FragmentUtils.setActionBarVisible(getActivity(), false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vpPager);

        // getChildFragmentManager() allows to use fragments hosted by BookmarkFragment,
        // rather than ones hosted by the activity as a whole.
        FragmentPagerAdapter adapter = new BookmarkPagerAdapter(getActivity(),
                getChildFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FragmentUtils.setActionBarVisible(getActivity(), true);
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
