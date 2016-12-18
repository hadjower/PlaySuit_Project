package com.shawasama.playsuit.albums_fragment;

import android.content.Context;
import android.support.v7.widget.CardView;
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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<Album> albums;
    private final AlbumsFragment mFragment;
    private RecyclerView recyclerView;
    private Context mContext;

    public AlbumAdapter(List<Album> albums, AlbumsFragment mFragment) {
        this.albums = albums;
        this.mFragment = mFragment;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_artist, parent, false);
        mContext = mFragment.getActivity().getApplicationContext();
        recyclerView = (RecyclerView) parent;
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
                .placeholder(R.mipmap.img_album)
                .centerCrop()
                .into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return albums != null ? albums.size(): 0;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        TextView artist;
        TextView countSong;
        ImageView albumArt;


        public AlbumViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.aa_cardview_container);
            title = (TextView) itemView.findViewById(R.id.aa_title);
            artist = (TextView) itemView.findViewById(R.id.aa_subtitle);
            countSong = (TextView) itemView.findViewById(R.id.aa_subsubtitle);
            albumArt = (ImageView) itemView.findViewById(R.id.aa_art_image);
        }
    }
}
