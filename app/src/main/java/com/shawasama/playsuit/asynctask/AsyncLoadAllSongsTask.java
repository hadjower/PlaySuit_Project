package com.shawasama.playsuit.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.songs_fragment.SongsManager;

import java.util.List;


public class AsyncLoadAllSongsTask extends AsyncTask<Void, Void, List<Song>> {
    private Context mContext;

    public AsyncLoadAllSongsTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected List<Song> doInBackground(Void... params) {
        SongsManager.getInstance().setSongList(mContext);
        return SongsManager.getInstance().getSongList();
    }

    @Override
    protected void onPostExecute(List<Song> songList) {
        super.onPostExecute(songList);
    }
}
