package com.shawasama.playsuit.albums_fragment;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shawasama.playsuit.R;
import com.shawasama.playsuit.media_class.Album;

import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder> {

    private List<Album> albums;
    private final AlbumsFragment mFragment;
    private GridLayoutManager manager;
    private RecyclerView viewGroup;
    private final Context mContext;

    public AlbumListAdapter(Context mContext, List<Album> albums, AlbumsFragment mFragment) {
        this.mContext = mContext;
        this.albums = albums;
        this.mFragment = mFragment;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        viewGroup = (RecyclerView) parent;
        manager = (GridLayoutManager) viewGroup.getLayoutManager();

        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.title.setText(album.getTitle());
        holder.artist.setText(album.getArtist());
        String songs = String.valueOf(album.getCountSongs()) + " " + mContext.getString(R.string.songs);
        holder.countSong.setText(songs);
        Glide.with(mContext)
                .load(album.getArtPath())
                .centerCrop()
                .into(holder.albumArt);
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
        ImageView albumArt;


        public AlbumViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            artist = (TextView) itemView.findViewById(R.id.artist);
            countSong = (TextView) itemView.findViewById(R.id.countSong);
            albumArt = (ImageView) itemView.findViewById(R.id.album_art_image);
        }
    }
}
