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
import com.ivanmagda.yatranslate.fragment.TranslateFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            setCurrentFragment(TranslateFragment.newInstance(), TranslateFragment.TAG);
        }

        initNavigation();
    }

    private void initNavigation() {
        OnNavigationItemSelectedListener listener = new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_translate:
                        setCurrentFragment(TranslateFragment.newInstance(), TranslateFragment.TAG);
                        return true;
                    case R.id.navigation_bookmark:
                        setCurrentFragment(BookmarkFragment.newInstance(), BookmarkFragment.TAG);
                        return true;
                    default:
                        return false;
                }
            }
        };

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(listener);
    }

    private void setCurrentFragment(Fragment fragment, String fragmentTag) {
        Fragment addedFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addedFragment != null) {
            transaction.replace(R.id.main_container, addedFragment, addedFragment.getTag());
        } else {
            transaction.add(R.id.main_container, fragment, fragmentTag);
        }

        transaction.commit();
    }
}
