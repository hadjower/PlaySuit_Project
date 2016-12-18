package com.shawasama.playsuit.artists_fragment;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.media_class.Artist;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private List<Artist> artists;
    private RecyclerView recyclerView;
    private ArtistFragment mFragment;
    private Context mContext;

    public ArtistAdapter(List<Artist> artists, ArtistFragment mFragment) {
        this.artists = artists;
        this.mFragment = mFragment;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_artist, parent, false);
        mContext = mFragment.getActivity().getApplicationContext();
        recyclerView = (RecyclerView) parent;
        return new ArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.name.setText(artist.getName());
        holder.countAlbums.setText(String.valueOf(artist.getAlbumsCount()) + " " + mContext.getString(R.string.albums));
        holder.countSongs.setText(String.valueOf(artist.getSongCount()) + " " + mContext.getString(R.string.songs));
    }

    @Override
    public int getItemCount() {
        return artists != null ? artists.size() : 0;
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name;
        TextView countAlbums;
        TextView countSongs;
        ImageView artistArt;


        public ArtistViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.aa_cardview_container);
            name = (TextView) itemView.findViewById(R.id.aa_title);
            countAlbums = (TextView) itemView.findViewById(R.id.aa_subtitle);
            countSongs = (TextView) itemView.findViewById(R.id.aa_subsubtitle);
            artistArt = (ImageView) itemView.findViewById(R.id.aa_art_image);
        }
    }
}
