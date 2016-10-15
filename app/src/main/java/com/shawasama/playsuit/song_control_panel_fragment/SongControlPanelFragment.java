package com.shawasama.playsuit.song_control_panel_fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shawasama.playsuit.R;
import com.shawasama.playsuit.albums_fragment.AlbumsManager;
import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.songs_fragment.SongsManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SongControlPanelFragment extends Fragment {
    private static final int LAYOUT = R.layout.song_control_panel;
    private Context mContext;
    private View view;
    private ControlPanelViewHolder holder;

    private boolean isPlay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        mContext = getActivity().getApplicationContext();
        holder = new ControlPanelViewHolder(view, this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setFirstSongOnPanel();
    }

    private void setFirstSongOnPanel() {
        try {
            Song song = new AsyncTask<Void, Void, Song>() {
                @Override
                protected Song doInBackground(Void... params) {
                    Song song = null;
                    do {
                        song = SongsManager.getInstance().getFirsSong();
                    } while (song == null);
                    return song;
                }

                @Override
                protected void onPostExecute(Song song) {
                    super.onPostExecute(song);
                }
            }.execute().get(1, TimeUnit.SECONDS);
            setSongOnPanel(song);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            view.setVisibility(View.GONE);
        }
    }

    public void setSongOnPanel(Song song) {
        holder.title.setText(song.getTitle());
        holder.subtitle.setText(song.getArtist());
        Glide.with(mContext)
                .load(AlbumsManager.getInstance().getAlbumArtPathForSong(song))
                .error(R.mipmap.ic_album)
                .into(holder.songArt);
    }

    public boolean isPlay() {
        return isPlay;
    }

    private void reverseIsPlay() {
        isPlay = !isPlay;
    }

    static class ControlPanelViewHolder {
        RelativeLayout container;
        ImageView songArt;
        ImageButton prevTrack;
        ImageButton playPause;
        ImageButton nextTrack;
        ContentLoadingProgressBar trackProgressBar;
        TextView title;

        TextView subtitle;

        public ControlPanelViewHolder(View view, final SongControlPanelFragment mFragment) {
            container = (RelativeLayout) view.findViewById(R.id.rel_control_panel_container);
            songArt = (ImageView) view.findViewById(R.id.cp_song_art);
            title = (TextView) view.findViewById(R.id.cp_title);
            subtitle = (TextView) view.findViewById(R.id.cp_subtitle);
            playPause = (ImageButton) view.findViewById(R.id.cp_play_pause_track);
            prevTrack = (ImageButton) view.findViewById(R.id.cp_previous_track);
            nextTrack = (ImageButton) view.findViewById(R.id.cp_next_track);
            trackProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.song_progress_bar);

            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playPause.setImageDrawable(ContextCompat.getDrawable(mFragment.getActivity().getApplicationContext(),
                            mFragment.isPlay() ? R.mipmap.ic_play_arrow_white_36dp : R.mipmap.ic_pause_white_36dp));
                    mFragment.reverseIsPlay(); // reverse
                }
            });

            trackProgressBar.setProgress(80);
        }

    }
}
