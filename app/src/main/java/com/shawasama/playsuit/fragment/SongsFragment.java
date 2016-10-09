package com.shawasama.playsuit.fragment;

import android.content.ContentResolver;
import android.content.Context;
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
import com.shawasama.playsuit.adapter.SongAdapter;
import com.shawasama.playsuit.pojo.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SongsFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.listview_layout;

    private ArrayList<Song> songList;
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

        setSongList();
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

    private void setSongList() {
        songList = new ArrayList<>();

        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long thisID = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisDuration = musicCursor.getLong(durationColumn);
                songList.add(new Song(thisID, thisArtist, thisTitle, thisDuration));
            } while (musicCursor.moveToNext());
        }
    }

    private void connectSongList() {
        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        SongAdapter songAdapter = new SongAdapter(getActivity(), songList);
        songView.setAdapter(songAdapter);
    }

}
