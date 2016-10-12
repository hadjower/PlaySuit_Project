package com.shawasama.playsuit.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.shawasama.playsuit.songs_management.AudioContainer;


public class SongsLoadAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;

    public SongsLoadAsyncTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(Void... params) {
        AudioContainer.getInstance().setSongList(mContext);
        return null;
    }
}
