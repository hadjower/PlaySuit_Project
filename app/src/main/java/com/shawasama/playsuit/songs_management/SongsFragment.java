package com.shawasama.playsuit.songs_management;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.fragment.AbstractTabFragment;
import com.shawasama.playsuit.pojo.Song;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        AsyncTask<Void, Void, List<Song>> task = new AsyncTask<Void, Void, List<Song>>() {
            @Override
            protected List<Song> doInBackground(Void... params) {
                List<Song> list = null;
                do {
                    list = AudioContainer.getInstance().getSongList();
                } while (list == null);
                return list;
            }

            @Override
            protected void onPostExecute(List<Song> songs) {
                super.onPostExecute(songs);
            }
        }.execute();

        try {
            songList = task.get(1, TimeUnit.SECONDS);
            if (songList == null || songList.size()==0) {
                //TODO Write on background that there are no music
//                Toast.makeText(context, "There is no music", Toast.LENGTH_SHORT).show();
            } else {
                SongAdapter songAdapter = new SongAdapter(getActivity(), songList);
                songView.setAdapter(songAdapter);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(context, "Unable to load music", Toast.LENGTH_SHORT).show();
        }
    }

}