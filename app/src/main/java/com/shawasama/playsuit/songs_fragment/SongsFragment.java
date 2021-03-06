package com.shawasama.playsuit.songs_fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.activity.MainActivity;
import com.shawasama.playsuit.asynctask.AsyncLoadAllSongsTask;
import com.shawasama.playsuit.fragment.AbstractTabFragment;
import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.util.Constants;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SongsFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.recyclerview_layout;

    private List<Song> songList;
    private RecyclerView songView;
    private SongAdapter adapter;

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
        songView = (RecyclerView) view.findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(context);
        songView.setLayoutManager(manager);
        connectSongList();
        return view;
    }

    private Song getCurrSong() {
        return ((MainActivity) getActivity()).getMusicSrv().getCurrSong();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context = null;
        songView = null;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void connectSongList() {
        try {
            AsyncLoadAllSongsTask asyncTask = (AsyncLoadAllSongsTask) ((MainActivity) getActivity()).getAsyncTask(Constants.ASYNC_SONGS);
            songList = asyncTask.get(2, TimeUnit.SECONDS);
            if (songList == null || songList.size() == 0) {
                //TODO Write on background that there are no music
//                Toast.makeText(context, "There is no music", Toast.LENGTH_SHORT).show();
            } else {
                adapter = new SongAdapter(getActivity(), songList, this, setOnItemClickListener(), getCurrSong());
                songView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(context, "Unable to load music", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener setOnItemClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = songView.getChildAdapterPosition(v);
                removeSelection(((MainActivity) getActivity()).getMusicSrv().getSongPosn());
                ((MainActivity) getActivity()).playSong(songList, index);
                setSelected(v);
            }
        };
        return listener;
    }

    private void removeSelection(int pos) {
        View view = manager.findViewByPosition(pos);
        if (view == null)
            return;
        TextView title = (TextView) view.findViewById(R.id.aa_title);
        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
        title.setTextColor(Color.WHITE);
        subtitle.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
    }

    private void setSelected(View v) {
        TextView title = (TextView) v.findViewById(R.id.aa_title);
        TextView subtitle = (TextView) v.findViewById(R.id.subtitle);
        int color = ContextCompat.getColor(getActivity().getApplicationContext(),
                R.color.colorComplementary);
        title.setTextColor(color);
        subtitle.setTextColor(color);
    }

}
