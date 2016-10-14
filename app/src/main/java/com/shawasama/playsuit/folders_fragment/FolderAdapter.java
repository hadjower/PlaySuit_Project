package com.shawasama.playsuit.folders_fragment;

import android.app.Application;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.songs_fragment.SongsManager;
import com.shawasama.playsuit.util.Constants;
import com.shawasama.playsuit.util.Util;

import java.io.File;
import java.util.List;


public class FolderAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private Application mApp;
    private FoldersFragment mFragment;

    private List<File> items;

    public FolderAdapter
            (Context context, FoldersFragment fragment,
             List<String> nameList,
             List<File> items) {
        super(context, -1, nameList);

        mContext = context;
        mApp = (Application) context.getApplicationContext();
        mFragment = fragment;

        this.items = items;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLay;

        //if layout for back function
        if (position == 0 && items.get(0).getPath().equals("..")) {
            itemLay = LayoutInflater.from(mContext).inflate(R.layout.item_folder, parent, false);
            ImageButton options = (ImageButton) itemLay.findViewById(R.id.item_btn_menu);
            options.setVisibility(View.INVISIBLE);
            return itemLay;
        }

        boolean isFolder = items.get(position).isDirectory();

        ItemViewHolder holder;
        String title, subtitle;
        long duration = 0;

        holder = new ItemViewHolder();
        if (isFolder) {
            File folder = items.get(position);
            itemLay = LayoutInflater.from(mContext).inflate(R.layout.item_folder, parent, false);
            holder.folderIcon = (ImageView) itemLay.findViewById(R.id.folder_icon);

            title = items.get(position).getName();
            SparseArray<Long> info = SongsManager.getInstance().getFolderInfo(folder);
            if (info == null) {
                Log.e("JOWER_FOLDER_INFO_ERR", " Can't get folder info from " + folder.getAbsolutePath());
                subtitle = "unknown";
            } else {
                subtitle = info.get(Constants.AMOUNT_KEY) + " " + mContext.getString(R.string.songs);
                duration = info.get(Constants.DURATION_KEY);
            }

        } else {
            itemLay = LayoutInflater.from(mContext).inflate(R.layout.item_song, parent, false);

            //get info about song
            Song currSong = SongsManager.getInstance().getSong(items.get(position).getAbsolutePath());
            if (currSong != null) {
                title = currSong.getTitle();
                subtitle = currSong.getArtist();
                duration = currSong.getDuration();
            } else {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(mContext, Uri.fromFile(items.get(position)));
                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                subtitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                retriever.release();
            }

            itemLay.setTag(R.string.folder_list_item_type, Constants.AUDIO);
            itemLay.setTag(R.string.folder_path, items.get(position).getAbsolutePath());
            itemLay.setTag(R.string.position, position);

        }

        holder.itemTitle = (TextView) itemLay.findViewById(R.id.title);
        holder.itemSubtitle = (TextView) itemLay.findViewById(R.id.subtitle);
        holder.itemRightSubtitle = (TextView) itemLay.findViewById(R.id.duration);
        holder.itemMenuBtn = (ImageButton) itemLay.findViewById(R.id.item_btn_menu);

        //set info about folder/song
        holder.itemTitle.setText(title);
        holder.itemSubtitle.setText(subtitle);
        holder.itemRightSubtitle.setText(Util.getSongDuration(duration));

        //TODO maybe create popup class and use it for options button in all lists/grids
//            holder.itemMenuBtn.setFocusable(true);
//            holder.itemMenuBtn.setFocusableInTouchMode(true);
//            holder.itemMenuBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });

        itemLay.setTag(holder);
        return itemLay;
    }

    static class ItemViewHolder {
        TextView itemTitle;
        TextView itemSubtitle;
        ImageButton itemMenuBtn;
        TextView itemRightSubtitle;
        ImageView folderIcon;

    }
}
