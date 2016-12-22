package com.shawasama.playsuit.songs_fragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
    private View.OnClickListener mListener;
    private Song currSong;

    public SongAdapter(Context c, List<Song> songList,
                       SongsFragment mFragment, View.OnClickListener onClickListener,
                       Song currSong) {
        this.songList = songList;
        mContext = c;
        this.mFragment = mFragment;
        this.mListener = onClickListener;
        this.currSong = currSong;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        recyclerView = (RecyclerView) parent;
        itemView.setOnClickListener(mListener);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        //get current song
        holder.setIsRecyclable(false);
        Song currentSong = songList.get(position);

        holder.artist.setText(currentSong.getArtist());
        holder.title.setText(currentSong.getTitle());
        holder.duration.setText(Util.getTimeInText(currentSong.getDuration()));
        if (this.currSong.getId() == currentSong.getId()) {
            holder.setSelected(ContextCompat.getColor(mFragment.getActivity().getApplicationContext(),
                    R.color.colorComplementary));
        }
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
            title = (TextView) itemView.findViewById(R.id.aa_title);
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

        public void setSelected(int color) {
            artist.setTextColor(color);
            title.setTextColor(color);
        }
    }
}
