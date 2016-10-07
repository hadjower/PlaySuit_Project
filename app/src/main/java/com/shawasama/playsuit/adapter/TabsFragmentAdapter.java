package com.shawasama.playsuit.adapter;

//import android.app.Fragment;
//import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
//import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.shawasama.playsuit.fragment.AbstractTabFragment;
import com.shawasama.playsuit.fragment.AlbumsFragment;
import com.shawasama.playsuit.fragment.ArtistFragment;
import com.shawasama.playsuit.fragment.FoldersFragment;
import com.shawasama.playsuit.fragment.PlaylistsFragment;
import com.shawasama.playsuit.fragment.SongsFragment;

import java.util.HashMap;
import java.util.Map;

public class TabsFragmentAdapter extends FragmentPagerAdapter {

    private SparseArray<AbstractTabFragment> tabs;
    private Context context;

    public TabsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        initTabsMap(context);
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabsMap(Context context) {
        tabs = new SparseArray<>();
        tabs.put(0, AlbumsFragment.getInstance(context));
        tabs.put(1, ArtistFragment.getInstance(context));
        tabs.put(2, FoldersFragment.getInstance(context));
        tabs.put(3, PlaylistsFragment.getInstance(context));
        tabs.put(4, SongsFragment.getInstance(context));
    }
}
