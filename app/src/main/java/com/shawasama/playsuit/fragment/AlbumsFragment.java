package com.shawasama.playsuit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.adapter.AlbumListAdapter;
import com.shawasama.playsuit.pojo.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumsFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.fragment_album;

    public static AlbumsFragment getInstance(Context context) {
        Bundle args = new Bundle();
        AlbumsFragment fragment = new AlbumsFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.navigation_menu_and_tab_item_albums));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(new AlbumListAdapter(onCreateData()));
        return view;
    }

    private List<Album> onCreateData() {
        List<Album> albumList = new ArrayList<>();
        albumList.add(new Album("Album1", "Artist", "Songs 1"));
        albumList.add(new Album("Album2", "Artist", "Songs 2"));
        albumList.add(new Album("Album3", "Artist", "Songs 3"));
        albumList.add(new Album("Album4", "Artist", "Songs 4"));
        albumList.add(new Album("Album5", "Artist", "Songs 5"));
        albumList.add(new Album("Album6", "Artist", "Songs 6"));
        albumList.add(new Album("Album7", "Artist", "Songs 7"));
        albumList.add(new Album("Album8", "Artist", "Songs 8"));
        return albumList;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
