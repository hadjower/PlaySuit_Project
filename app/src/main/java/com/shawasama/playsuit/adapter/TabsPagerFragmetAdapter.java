package com.shawasama.playsuit.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shawasama.playsuit.fragment.ExampleFragment;

public class TabsPagerFragmetAdapter extends FragmentPagerAdapter {

    private String[] tabs;

    public TabsPagerFragmetAdapter(FragmentManager fm) {
        super(fm);
        tabs = new String[]{
                "Albums", "Artist", "Folders", "Playlist", "Songs"
        };
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ExampleFragment.getInstance();
            case 1:
                return ExampleFragment.getInstance();
            case 2:
                return ExampleFragment.getInstance();
            case 3:
                return ExampleFragment.getInstance();
            case 4:
                return ExampleFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
