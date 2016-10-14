package com.shawasama.playsuit.songs_fragment;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.util.Util;

import java.util.List;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songList;
    private Context mContext;
    private SongsFragment mFragment;
    private RecyclerView recyclerView;

    public SongAdapter(Context c, List<Song> songList, SongsFragment mFragment) {
        this.songList = songList;
        mContext = c;
        this.mFragment = mFragment;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        recyclerView = (RecyclerView) parent;
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        //get current song
        Song currentSong = songList.get(position);

        holder.artist.setText(currentSong.getArtist());
        holder.title.setText(currentSong.getTitle());
        holder.duration.setText(Util.getSongDuration(currentSong.getDuration()));
    }

    @Override
    public int getItemCount() {
        return songList!=null ? songList.size() : 0;
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView artist;
        TextView title;
        TextView duration;
        ImageButton btnMenu;

        public SongViewHolder(final View itemView) {
            super(itemView);
            artist = (TextView) itemView.findViewById(R.id.subtitle);
            title = (TextView) itemView.findViewById(R.id.title);
            duration = (TextView) itemView.findViewById(R.id.duration);
            btnMenu = (ImageButton) itemView.findViewById(R.id.item_btn_menu);

            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), btnMenu);
                    popupMenu.inflate(R.menu.songlist_item_menu);
                    popupMenu.show();
                }
            });
        }
    }
}
