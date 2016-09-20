package com.krux.activity.main;

import java.util.List;
import java.util.Vector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;


import com.example.brent.helloworld.R;

public class HomeActivity extends FragmentActivity{
    private PagerAdapter mPagerAdapter;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.home_layout);
        this.initialisePaging();
    }

    /**
     * Initialise the fragments to be paged
     */
    private void initialisePaging() {

        fragments = new Vector<Fragment>();

        fragments.add(Fragment.instantiate(this, LimbsQueryFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, LimbsFilterFragment.class.getName()));

        this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        //
        ViewPager pager = (ViewPager)super.findViewById(R.id.viewpager);
        pager.setAdapter(this.mPagerAdapter);
        pager.setCurrentItem(0);
    }
}