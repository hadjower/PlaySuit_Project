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
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shawasama.playsuit.R;
import com.shawasama.playsuit.activity.MainActivity;
import com.shawasama.playsuit.albums_fragment.AlbumsManager;
import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.music_playback.MusicController;
import com.shawasama.playsuit.songs_fragment.SongsManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SongControlPanelFragment extends Fragment implements MediaController.MediaPlayerControl {
    private static final int LAYOUT = R.layout.song_control_panel;
    private Context mContext;
    private View view;
    private ControlPanelViewHolder holder;

    private MusicController musicController;

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
            setSongOnPanel(song, false);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            view.setVisibility(View.GONE);
        }
    }

    public void setSongOnPanel(Song song, boolean isPlay) {
        holder.title.setText(song.getTitle());
        holder.subtitle.setText(song.getArtist());
        Glide.with(mContext)
                .load(AlbumsManager.getInstance().getAlbumArtPathForSong(song))
                .error(R.mipmap.ic_album)
                .into(holder.songArt);
        holder.playPause.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(),
                isPlay ? R.mipmap.ic_pause_white_36dp  : R.mipmap.ic_play_arrow_white_36dp));
    }

    @Override
    public void start() {
        ((MainActivity) getActivity()).getMusicSrv().go();
    }

    @Override
    public void pause() {
        ((MainActivity) getActivity()).getMusicSrv().pausePlayer();
    }

    @Override
    public int getDuration() {
        if (((MainActivity) getActivity()).getMusicSrv() != null && ((MainActivity) getActivity()).isMusicBound() &&
                ((MainActivity) getActivity()).getMusicSrv().isPng())
            return ((MainActivity) getActivity()).getMusicSrv().getDur();
        else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (((MainActivity) getActivity()).getMusicSrv() != null && ((MainActivity) getActivity()).isMusicBound() &&
                ((MainActivity) getActivity()).getMusicSrv().isPng())
            return ((MainActivity) getActivity()).getMusicSrv().getPosn();
        else
            return 0;
    }

    @Override
    public void seekTo(int pos) {
        ((MainActivity) getActivity()).getMusicSrv().seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (((MainActivity) getActivity()).getMusicSrv() != null && ((MainActivity) getActivity()).isMusicBound())
            return ((MainActivity) getActivity()).getMusicSrv().isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void setController(MusicController controller) {
        this.musicController = controller;
        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(getActivity().findViewById(R.id.recycler_view));
        controller.setEnabled(true);
    }

    private void playPrev() {
        ((MainActivity) getActivity()).getMusicSrv().playPrev();
        musicController.show(0);
    }

    private void playNext() {
        ((MainActivity) getActivity()).getMusicSrv().playNext();
        musicController.show(0);
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

            setListeners(mFragment);

            trackProgressBar.setProgress(80);
        }

        private void setListeners(final SongControlPanelFragment mFragment) {
            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id;
                    if (mFragment.isPlaying()){
                        id = R.mipmap.ic_play_arrow_white_36dp;
                        mFragment.pause();
                    } else {
                        id = R.mipmap.ic_pause_white_36dp;
                        mFragment.start();
                    }
                    playPause.setImageDrawable(ContextCompat.getDrawable(mFragment.getActivity().getApplicationContext(), id));
                }
            });

            prevTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragment.playPrev();
                    mFragment.setSongOnPanel(((MainActivity) mFragment.getActivity()).getMusicSrv().getCurrSong(), true);                }
            });

            nextTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragment.playNext();
                    mFragment.setSongOnPanel(((MainActivity) mFragment.getActivity()).getMusicSrv().getCurrSong(), true);
                }
            });
        }

    }
}
