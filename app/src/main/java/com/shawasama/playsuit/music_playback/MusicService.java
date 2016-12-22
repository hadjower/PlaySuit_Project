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
import com.shawasama.playsuit.activity.ManageSongActivity;
import com.shawasama.playsuit.media_class.Song;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class MusicService extends Service implements
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private List<Song> songs;
    //current position
    private int songPosn;

    private final IBinder musicBind = new MusicBinder();
    private MainActivity mainActivity;
    private ManageSongActivity manageActivity;

    private boolean isRepeat = false;
    private boolean isShuffle = false;
    private Deque<Integer> shuffleSongHistory;

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();

        shuffleSongHistory = new LinkedList<>();
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
        Log.i("MUSIC", "Prepare: index[" + songPosn + "] + song[" + playSong.getTitle() + "]");
        //get id
        long currSong = playSong.getId();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        Log.i("MUSIC", "Prepare Uri: " + trackUri);

        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
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
        player.start();
    }

    public void playSong(List<Song> songList, int songPos) {
        setSong(songPos);
        setListAndPrepareSong(songList);
        player.start();
        Log.i("MUSIC", "Service: index[" + songPos + "] + song[" + songList.get(songPos).getTitle() + "]");
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // check for repeat is ON or OFF
        if (isRepeat) {
            // repeat is on play same song again
            playSong();
        } else if (isShuffle) {
            // shuffle is on - play a random song
            songPosn = getShuffleSong();

            playSong();
            if (mainActivity.getSharedPreferences("OURINFO", MODE_PRIVATE).getBoolean("main_active", false)) {
                mainActivity.getPanelFragment().setSongOnPanel(songs.get(songPosn), true);
            }
            if (mainActivity.getSharedPreferences("OURINFO", MODE_PRIVATE).getBoolean("manage_active", false)) {
                manageActivity.setSongCharacteristics(songs.get(songPosn));
            }
        } else {
            // no repeat or shuffle ON - play next song
            playNext();
            mainActivity.getPanelFragment().setSongOnPanel(songs.get(songPosn), true);
        }
    }

    private int getShuffleSong() {
        Random rand = new Random();
        int newSong = songPosn;
        while (newSong == songPosn) {
            newSong = rand.nextInt(songs.size());
        }
        shuffleSongHistory.add(songPosn);
        return newSong;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
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
        player.start();
    }

    public void playPrev() {
        if (isShuffle) {
            if (!shuffleSongHistory.isEmpty())
                songPosn = shuffleSongHistory.pop();
        } else {
            songPosn--;
            if (songPosn < 0)
                songPosn = songs.size() - 1;
            playSong();
        }
    }

    //skip to next
    public void playNext() {
        if (isShuffle) {
            songPosn = getShuffleSong();
        } else {
            songPosn++;
            if (songPosn == songs.size())
                songPosn = 0;
        }
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

    public boolean isShuffle() {
        return isShuffle;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        if (repeat)
            setShuffle(false);
        isRepeat = repeat;
    }

    public void setShuffle(boolean shuffle) {
        if (shuffle)
            setRepeat(false);
        else
            shuffleSongHistory.clear();
        isShuffle = shuffle;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
