package com.shawasama.playsuit.asynctask;


import android.content.Context;
import android.os.AsyncTask;

import com.shawasama.playsuit.albums_fragment.AlbumsManager;
import com.shawasama.playsuit.media_class.Album;

import java.util.List;

public class AsyncLoadAllAlbumsTask extends AsyncTask<Void, Void, List<Album>> {
    private Context mContext;

    public AsyncLoadAllAlbumsTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected List<Album> doInBackground(Void... params) {
        return AlbumsManager.getInstance().loadAlbums(mContext);
    }

    @Override
    protected void onPostExecute(List<Album> albumList) {
        super.onPostExecute(albumList);
    }
}
