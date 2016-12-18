package com.shawasama.playsuit.music_playback;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shawasama.playsuit.activity.MainActivity;
import com.shawasama.playsuit.media_class.Song;

import java.util.List;
import java.util.Random;


public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private List<Song> songs;
    //current position
    private int songPosn;

    private final IBinder musicBind = new MusicBinder();
    private boolean shittyFlag;
    private MainActivity activity;

    private boolean isRepeat;
    private boolean isShuffle;
    private boolean isSongChanged;

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setListAndPrepareSong(List<Song> theSongs) {
        setList(theSongs);
        prepareSong();
    }

    private void prepareSong() {
        player.reset();
        //get song
        Song playSong = songs.get(songPosn);
        //get id
        long currSong = playSong.getId();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public void playSong() {
        prepareSong();

        player.prepareAsync();
    }

    public void playSong(List<Song> songList, int songPos) {
        setSong(songPos);
        setListAndPrepareSong(songList);
        player.prepareAsync();
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong();
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            songPosn = rand.nextInt((songs.size() - 1) - 0 + 1) + 0;
            playSong();
            activity.getPanelFragment().setSongOnPanel(songs.get(songPosn), true);
        } else{
            // no repeat or shuffle ON - play next song
            playNext();
            activity.getPanelFragment().setSongOnPanel(songs.get(songPosn), true);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        if (!shittyFlag && !isPng()) {
            player.prepareAsync();
            shittyFlag = true;
        } else {
            player.start();
        }
    }

    public void playPrev() {
        songPosn--;
        if (songPosn < 0)
            songPosn = songs.size() - 1;
        playSong();
    }

    //skip to next
    public void playNext() {
        songPosn++;
        if (songPosn == songs.size())
            songPosn = 0;
        playSong();
    }

    public Song getCurrSong() {
        return songs.get(songPosn);
    }

    public void setList(List<Song> list) {
        this.songs = list;
    }

    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
