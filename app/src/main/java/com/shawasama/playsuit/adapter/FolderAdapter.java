package com.shawasama.playsuit.adapter;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawasama.playsuit.Constants;
import com.shawasama.playsuit.R;
import com.shawasama.playsuit.fragment.FoldersFragment;
import com.shawasama.playsuit.pojo.Util;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class FolderAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private Application mApp;
    private FoldersFragment mFragment;

    private List<File> items;
    private List<String> mFileFolderNameList;
    private List<Integer> mFileFolderTypeList;
    private List<String> mFileFolderPathsList;

    public FolderAdapter
            (Context context, FoldersFragment fragment,
             List<String> nameList,
             List<String> fileFolderPathsList,
             List<Integer> mFileFolderTypeList, List<File> items) {
        super(context, -1, nameList);

        mContext = context;
        mApp = (Application) context.getApplicationContext();
        mFragment = fragment;

        this.mFileFolderNameList = nameList;
        this.mFileFolderTypeList = mFileFolderTypeList;
        this.mFileFolderPathsList = fileFolderPathsList;
        this.items = items;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLay;

        if (position == 0) {
            itemLay = LayoutInflater.from(mContext).inflate(R.layout.item_folder, parent, false);
            ImageButton options = (ImageButton) itemLay.findViewById(R.id.item_btn_menu);
            options.setVisibility(View.INVISIBLE);
            return itemLay;
        }


        boolean isFolder = mFileFolderTypeList.get(position) == Constants.FOLDER;

        ItemViewHolder holder;
        String title, subtitle;
        title = subtitle = "";
        long duration = 0;
        //TODO load only directories with music inside it or its subdirectories
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri uri = !isFolder ? MediaStore.Audio.Media.EXTERNAL_CONTENT_URI : MediaStore.Audio.Media.getContentUriForPath(mFileFolderPathsList.get(position));
        Cursor musicCursor = null;
        try {
            musicCursor = musicResolver.query(uri,
                    new String[]{
                            MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.DURATION,
                    },
                    false ? null : MediaStore.Audio.Media.DATA + "=? ",
                    false ? null : new String[]{items.get(position).getCanonicalPath()},
                    null);
        } catch (IOException e) {
            Log.e("SONG_PATH", "Error while trying to get path of " + mFileFolderNameList.get(position));
        }
//        Uri uri = Uri.fromFile(new File(mFileFolderPathsList.get(position)));
//        Cursor musicCursor = musicResolver.query(uri, null, null, null, null);

        holder = new ItemViewHolder();
        if (isFolder) {
            itemLay = LayoutInflater.from(mContext).inflate(R.layout.item_folder, parent, false);
            holder.folderIcon = (ImageView) itemLay.findViewById(R.id.folder_icon);

            //TODO get amount of songs, duration of songs
            if (musicCursor != null && musicCursor.moveToFirst()) {

                int amount = 0;
                do {
                    amount++;
                } while (musicCursor.moveToNext());

                subtitle = amount + " " + mContext.getString(R.string.songs);
            }

            title = mFileFolderNameList.get(position);

        } else {
            itemLay = LayoutInflater.from(mContext).inflate(R.layout.item_song, parent, false);

            //get info about song
            if (musicCursor != null && musicCursor.moveToFirst()) {

                int titleColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.TITLE);
                int artistColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.ARTIST);
                int durationColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.DURATION);

//
                title = musicCursor.getString(titleColumn);
                subtitle = musicCursor.getString(artistColumn);
                duration = musicCursor.getLong(durationColumn);

            }

            if (musicCursor != null) {
                musicCursor.close();
            }

            itemLay.setTag(R.string.folder_list_item_type, Constants.AUDIO);
            itemLay.setTag(R.string.folder_path, mFileFolderPathsList.get(position));
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

        //TODO maybe create popup clas and use it for options button in all lists/grids
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
        public TextView itemTitle;
        public TextView itemSubtitle;
        public ImageButton itemMenuBtn;
        public TextView itemRightSubtitle;
        public ImageView folderIcon;

    }
}
