package com.shawasama.playsuit.folders_fragment;

import android.app.Application;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.songs_fragment.SongsManager;
import com.shawasama.playsuit.util.Constants;
import com.shawasama.playsuit.util.Util;

import java.io.File;
import java.util.List;


public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ItemViewHolder> {

    private Context mContext;
    private Application mApp;
    private RecyclerView viewGroup;
    private FoldersFragment mFragment;
    private View.OnClickListener mItemClick;
    private Song currentSong;

    private List<File> items;

    public FolderAdapter(Context context, FoldersFragment fragment,
                         List<File> items, View.OnClickListener mItemClick,
                         Song currentSong) {
        mContext = context;
        mApp = (Application) context.getApplicationContext();
        mFragment = fragment;

        this.mItemClick = mItemClick;
        this.items = items;
        this.currentSong = currentSong;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        itemView.setOnClickListener(mItemClick);
        viewGroup = (RecyclerView) parent;
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        //if layout for back function
        if (position == 0 && items.get(0).getPath().equals("..")) {
            holder.itemMenuBtn.setVisibility(View.INVISIBLE);
            return;
        }

        boolean isFolder = items.get(position).isDirectory();

        String title, subtitle;
        long duration = 0;

        if (isFolder) {
            File folder = items.get(position);
            if (currentSong.getPath().contains(folder.getAbsolutePath()))
                holder.setSelected(ContextCompat.getColor(mFragment.getActivity().getApplicationContext(),
                        R.color.colorComplementary));

            title = folder.getName();
            SparseArray<Long> info = SongsManager.getInstance().getFolderInfo(folder);
            if (info == null) {
                Log.e("JOWER_FOLDER_INFO_ERR", " Can't get folder info from " + folder.getAbsolutePath());
                subtitle = "unknown";
            } else {
                subtitle = info.get(Constants.AMOUNT_KEY) + " " + mContext.getString(R.string.songs);
                duration = info.get(Constants.DURATION_KEY);
            }

        } else {
            //get info about song
            Song currSong = SongsManager.getInstance().getSong(items.get(position).getAbsolutePath());
            if (currSong != null) {
                title = currSong.getTitle();
                subtitle = currSong.getArtist();
                duration = currSong.getDuration();
                holder.icon.setImageResource(R.mipmap.ic_music_circle);

                if (currSong == this.currentSong) {
                    holder.setSelected(ContextCompat.getColor(mFragment.getActivity().getApplicationContext(),
                            R.color.colorComplementary));
                }

            } else {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(mContext, Uri.fromFile(items.get(position)));
                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                subtitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                retriever.release();
            }

        }

        //set info about folder/song
        holder.itemTitle.setText(title);
        holder.itemSubtitle.setText(subtitle);
        holder.itemRightSubtitle.setText(Util.getTimeInText(duration));

        //TODO maybe create popup class and use it for options button in all lists/grids
//            holder.itemMenuBtn.setFocusable(true);
//            holder.itemMenuBtn.setFocusableInTouchMode(true);
//            holder.itemMenuBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void removeSelection(Song song) {
        int index = getSongIndex(song);
        if (index > -1) {
            mFragment.removeSelection(index);
        }
    }

    private int getSongIndex(Song song) {
        for (int i = 0; i < items.size(); i++) {
            File item = items.get(i);
            if (!item.isDirectory() && item.getPath().equals(song.getPath())) {
                Log.i("JOWER", "Found selected song: " + item.getName());
                return items.indexOf(item);
            }
        }
        return -1;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parent;
        TextView itemTitle;
        TextView itemSubtitle;
        ImageButton itemMenuBtn;
        TextView itemRightSubtitle;
        ImageView icon;

        ItemViewHolder(View itemView) {
            super(itemView);

            parent = (LinearLayout) itemView.findViewById(R.id.parent_folder);
            itemTitle = (TextView) itemView.findViewById(R.id.aa_title);
            itemSubtitle = (TextView) itemView.findViewById(R.id.subtitle);
            itemRightSubtitle = (TextView) itemView.findViewById(R.id.duration);
            itemMenuBtn = (ImageButton) itemView.findViewById(R.id.item_btn_menu);
            icon = (ImageView) itemView.findViewById(R.id.folder_icon);
        }

        public void setSelected(int color) {
            itemSubtitle.setTextColor(color);
            itemTitle.setTextColor(color);
        }
    }
}
