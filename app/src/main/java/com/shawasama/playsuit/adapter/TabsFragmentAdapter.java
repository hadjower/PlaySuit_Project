package com.shawasama.playsuit.adapter;

//import android.app.Fragment;
//import android.app.FragmentManager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.shawasama.playsuit.albums_fragment.AlbumsFragment;
import com.shawasama.playsuit.folders_fragment.FoldersFragment;
import com.shawasama.playsuit.fragment.AbstractTabFragment;
import com.shawasama.playsuit.fragment.ArtistFragment;
import com.shawasama.playsuit.fragment.PlaylistsFragment;
import com.shawasama.playsuit.songs_fragment.SongsFragment;
import com.shawasama.playsuit.util.Constants;

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
        tabs.put(Constants.TAB_ALBUMS, AlbumsFragment.getInstance(context));
        tabs.put(Constants.TAB_ARTIST, ArtistFragment.getInstance(context));
        tabs.put(Constants.TAB_FOLDERS, FoldersFragment.getInstance(context));
        tabs.put(Constants.TAB_PLAYLISTS, PlaylistsFragment.getInstance(context));
        tabs.put(Constants.TAB_SONGS, SongsFragment.getInstance(context));
    }
}
