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

package com.ivanmagda.yatranslate.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.fragment.BookmarkFragment;
import com.ivanmagda.yatranslate.fragment.BookmarkListFragment;
import com.ivanmagda.yatranslate.fragment.TranslateFragment;
import com.ivanmagda.yatranslate.model.TranslateFragmentState;
import com.ivanmagda.yatranslate.model.core.TranslateItem;
import com.ivanmagda.yatranslate.sync.YaTranslateSyncTask;
import com.ivanmagda.yatranslate.sync.YaTranslateSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements TranslateFragment.OnTranslateFragmentStateListener,
        BookmarkListFragment.OnListFragmentInteractionListener {

    private static final String TRANSLATE_FRAGMENT_STATE_KEY = "TRANSLATE_FRAGMENT_STATE_KEY";

    @BindView(R.id.navigation) BottomNavigationView mBottomNavigationView;

    /**
     * Helps to save and restore TranslateFragment state.
     */
    private TranslateFragmentState mTranslateFragmentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            setCurrentFragment(TranslateFragment.newInstance(), TranslateFragment.TAG);
        } else {
            mTranslateFragmentState = savedInstanceState.getParcelable(TRANSLATE_FRAGMENT_STATE_KEY);
        }

        initNavigation();

        YaTranslateSyncUtils.initialize(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRANSLATE_FRAGMENT_STATE_KEY, mTranslateFragmentState);
    }

    private void initNavigation() {
        OnNavigationItemSelectedListener listener = new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_translate:
                        setCurrentFragment(TranslateFragment.newInstance(mTranslateFragmentState),
                                TranslateFragment.TAG);
                        return true;
                    case R.id.navigation_bookmark:
                        setCurrentFragment(BookmarkFragment.newInstance(), BookmarkFragment.TAG);
                        return true;
                    default:
                        return false;
                }
            }
        };

        mBottomNavigationView.setOnNavigationItemSelectedListener(listener);
    }

    private void setCurrentFragment(Fragment fragment, String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main_container, fragment, fragmentTag)
                .commit();
    }

    @Override
    public void onSaveState(@NonNull TranslateFragmentState fragmentState) {
        mTranslateFragmentState = fragmentState;
    }

    @Override
    public void onListFragmentInteraction(TranslateItem selectedItem) {
        // Update state and select TranslateFragment page.
        mTranslateFragmentState = new TranslateFragmentState(selectedItem);
        mBottomNavigationView.findViewById(R.id.navigation_translate).performClick();
    }
}
