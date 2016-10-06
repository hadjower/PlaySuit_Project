package com.shawasama.playsuit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.pojo.Song;
import com.shawasama.playsuit.pojo.Util;

import java.util.ArrayList;


public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songList;
    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<Song> songList) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout) songInf.inflate(R.layout.item_songlist, parent, false);
        //get info
        TextView artist = (TextView) songLay.findViewById(R.id.artist);
        TextView title = (TextView) songLay.findViewById(R.id.title);
        TextView duration = (TextView) songLay.findViewById(R.id.duration);
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
