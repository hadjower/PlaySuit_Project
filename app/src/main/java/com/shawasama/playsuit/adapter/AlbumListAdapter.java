package com.shawasama.playsuit.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.pojo.Album;

import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder>{

    private List<Album> albums;

    public AlbumListAdapter(List<Album> albums) {
        this.albums = albums;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        holder.title.setText(albums.get(position).getTitle());
        holder.artist.setText(albums.get(position).getArtist());
        holder.countSong.setText(albums.get(position).getCountSongs());
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        TextView artist;
        TextView countSong;


        public AlbumViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            artist = (TextView) itemView.findViewById(R.id.artist);
            countSong = (TextView) itemView.findViewById(R.id.countSong);
        }
    }
}
