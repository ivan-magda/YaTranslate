package com.ivanmagda.yatranslate.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.fragment.BookmarkListFragment;

public final class BookmarkPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGES_COUNT = 2;
    private static final int HISTORY_PAGE = 0;
    private static final int FAVORITES_PAGE = 1;

    private Context mContext;

    public BookmarkPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

    // Returns the fragment to display for that page.
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case HISTORY_PAGE:
                return BookmarkListFragment.newInstance(BookmarkListFragment.ContentFilter.ALL);
            case FAVORITES_PAGE:
                return BookmarkListFragment.newInstance(BookmarkListFragment.ContentFilter.FAVORITE);
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case HISTORY_PAGE:
                return mContext.getString(R.string.vp_title_history);
            case FAVORITES_PAGE:
                return mContext.getString(R.string.vp_title_favorites);
            default:
                return null;
        }
    }

}
