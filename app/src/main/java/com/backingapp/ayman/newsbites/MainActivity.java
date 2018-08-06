package com.backingapp.ayman.newsbites;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.backingapp.ayman.newsbites.Interfaces.PagerAdapterInterface;
import com.backingapp.ayman.newsbites.Models.Category;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import butterknife.BindView;
import butterknife.ButterKnife;
import iammert.com.library.ConnectionStatusView;
import iammert.com.library.Status;

public class MainActivity extends AppCompatActivity implements NewsFragment.OnFragmentInteractionListener,
        SearchResultsFragment.OnFragmentInteractionListener,
        SavedArticlesFragment.OnFragmentInteractionListener,
        ConnectivityChangeListener, PagerAdapterInterface {

    @BindView(R.id.viewPager) public ViewPager viewPager;
    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.adView) AdView adView;
    @BindView(R.id.statusView) ConnectionStatusView statusView;
    @BindView(R.id.floating_search_view) FloatingSearchView floatingSearchView;
    @BindView(R.id.contentLayout) FrameLayout contentLayout;

    FragmentManager fragmentManager;

    SearchResultsFragment searchResultsFragment;

    private static boolean isMoveToHeadLines = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        floatingSearchView.setOnQueryChangeListener((oldQuery, newQuery) -> {
            if (newQuery.length() > 0) {
                contentLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                tabs.setVisibility(View.GONE);
                floatingSearchView.setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_SHOW_HOME);
            } else {
                contentLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                tabs.setVisibility(View.VISIBLE);

                floatingSearchView.setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_NO_LEFT_ACTION);
            }
        });

        floatingSearchView.setOnHomeActionClickListener(this::onBackPressed);

        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                getSupportFragmentManager().popBackStackImmediate();
                fragmentManager = getSupportFragmentManager();
                searchResultsFragment = SearchResultsFragment.newInstance(currentQuery);
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.add(R.id.contentLayout, searchResultsFragment, Constants.SEARCH_FRAGMENT_TAG);
//                ft.addToBackStack(Constants.SEARCH_FRAGMENT_TAG);
                ft.commit();
            }
        });

        runOnUiThread(this::initViewPager);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        if (floatingSearchView.getQuery().length() > 0) {
            getSupportFragmentManager().popBackStackImmediate();

            floatingSearchView.setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_NO_LEFT_ACTION);

            floatingSearchView.setSearchText("");
            contentLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            tabs.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    void initViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setOffscreenPageLimit(Category.values().length);
        viewPager.setAdapter(adapter);

        tabs.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.VIEW_PAGER_POSITION, tabs.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(Constants.VIEW_PAGER_POSITION));
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        if (event.getState() == ConnectivityState.CONNECTED) {
            // device has active internet connection
            statusView.setStatus(Status.COMPLETE);
        } else {
            // there is no active internet connection on this device
            statusView.setStatus(Status.ERROR);
        }
    }

    @Override
    public void onFinishUpdate() {
        if (!isMoveToHeadLines) {
            viewPager.setCurrentItem(1, true);
            isMoveToHeadLines = true;
        }
    }

    public static class SectionPagerAdapter extends FragmentStatePagerAdapter {

        private int size = Category.values().length;
        private PagerAdapterInterface pagerAdapterInterface;

        public SectionPagerAdapter(FragmentManager fm, PagerAdapterInterface pagerAdapterInterface) {
            super(fm);
            this.pagerAdapterInterface = pagerAdapterInterface;
        }

        @Override
        public Fragment getItem(int position) {
            Category category = Category.values()[position];
            if (category == Category.Saved)
                return SavedArticlesFragment.newInstance();
            return NewsFragment.newInstance(category);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return Category.values()[position].name();
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            pagerAdapterInterface.onFinishUpdate();
        }
    }

}
