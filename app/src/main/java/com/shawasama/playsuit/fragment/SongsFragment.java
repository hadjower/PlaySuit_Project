package com.shawasama.playsuit.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.activity.MainActivity;
import com.shawasama.playsuit.adapter.SongAdapter;
import com.shawasama.playsuit.pojo.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SongsFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.listview_layout;

    private List<Song> songList;
    private ListView songView;

    public static SongsFragment getInstance(Context context) {
        SongsFragment fragment;
        Bundle args = new Bundle();
        fragment = new SongsFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.navigation_menu_and_tab_item_songs));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        songView = (ListView) view.findViewById(R.id.listview);
        initListeners();

        connectSongList();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context = null;
        songView = null;
    }

    private void initListeners() {
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }

        });
    }

    public void setContext(Context context) {
        this.context = context;
    }



    private void connectSongList() {
        songList = MainActivity.getSongList();
        SongAdapter songAdapter = new SongAdapter(getActivity(), songList);
        songView.setAdapter(songAdapter);
    }

}
