package com.summertaker.blog46;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.summertaker.blog46.common.DataManager;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private DataManager mDataManager;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        mDataManager = new DataManager();
    }

    @Override
    public Fragment getItem(int position) {
        return MainFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mDataManager.getBlogDataSize();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position >= mDataManager.getBlogDataSize()) {
            return null;
        } else {
            return mDataManager.getBlogData(position).getTitle();
        }
    }
}