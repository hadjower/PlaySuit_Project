package com.shawasama.playsuit.songs_management;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.pojo.Song;
import com.shawasama.playsuit.pojo.Util;

import java.util.List;


public class SongAdapter extends BaseAdapter {

    private List<Song> songList;
    private LayoutInflater songInf;

    public SongAdapter(Context c, List<Song> songList) {
        this.songList = songList;
        songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        //map to song layout
        final LinearLayout songLay = (LinearLayout) songInf.inflate(R.layout.item_song, parent, false);

        //get info
        TextView artist = (TextView) songLay.findViewById(R.id.subtitle);
        TextView title = (TextView) songLay.findViewById(R.id.title);
        TextView duration = (TextView) songLay.findViewById(R.id.duration);
        final ImageButton btnMenu = (ImageButton) songLay.findViewById(R.id.item_btn_menu);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(songLay.getContext(), btnMenu);
                popupMenu.inflate(R.menu.songlist_item_menu);
                popupMenu.show();
            }
        });

        //get current song
        Song currentSong = songList.get(position);

        artist.setText(currentSong.getArtist());
        title.setText(currentSong.getTitle());
        duration.setText(Util.getSongDuration(currentSong.getDuration()));

        //set tag
        songLay.setTag(position);
        return songLay;
    }
}
