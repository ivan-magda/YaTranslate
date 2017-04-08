package com.ivanmagda.yatranslate.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.fragment.BookmarkFragment;
import com.ivanmagda.yatranslate.fragment.TranslateFragment;
import com.ivanmagda.yatranslate.fragment.dummy.DummyContent;

public class MainActivity extends AppCompatActivity
        implements TranslateFragment.OnTranslateFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentTransaction()
                    .add(R.id.main_container, TranslateFragment.newInstance(), TranslateFragment.TAG)
                    .commit();
        }

        initNavigation();
    }

    private void initNavigation() {
        OnNavigationItemSelectedListener listener = new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        replaceCurrentFragment(TranslateFragment.newInstance(), TranslateFragment.TAG);
                        return true;
                    case R.id.navigation_dashboard:
                        replaceCurrentFragment(BookmarkFragment.newInstance(), BookmarkFragment.TAG);
                        return true;
                    default:
                        return false;
                }
            }
        };

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(listener);
    }

    @SuppressLint("CommitTransaction")
    private FragmentTransaction getFragmentTransaction() {
        return getSupportFragmentManager().beginTransaction();
    }

    private void replaceCurrentFragment(Fragment fragment, String fragmentTag) {
        if (getSupportFragmentManager().findFragmentByTag(fragmentTag) != null) return;

        getFragmentTransaction()
                .replace(R.id.main_container, fragment, fragmentTag)
                .commit();
    }

    @Override
    public void onListItemInteraction(DummyContent.DummyItem item) {
        Log.d(TAG, "onListItemInteraction: " + item);
    }
}
