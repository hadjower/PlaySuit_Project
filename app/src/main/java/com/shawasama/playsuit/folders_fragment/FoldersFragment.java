package com.shawasama.playsuit.folders_fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.activity.MainActivity;
import com.shawasama.playsuit.fragment.AbstractTabFragment;
import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.songs_fragment.SongsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoldersFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.recyclerview_layout;
    private RecyclerView recyclerExplorer;

    //Folder parameter
    private String rootDir;
    private static String currentDir;
    private List<File> foldersAndMusic;

    //HashMap to store the each folder's previous scroll/position state.
    private HashMap<String, Parcelable> mFolderStateMap;
    private FolderAdapter adapter;


    public static FoldersFragment getInstance(Context context) {
        Bundle args = new Bundle();
        FoldersFragment fragment = new FoldersFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.navigation_menu_and_tab_item_folders));
        return fragment;
    }

    public static String getCurrentDir() {
        return currentDir;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        mFolderStateMap = new HashMap<>();

        recyclerExplorer = (RecyclerView) view.findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(context);
        recyclerExplorer.setLayoutManager(manager);

        initRecyclerListeners();

//        rootDir = Environment.getExternalStorageDirectory().getPath();
        rootDir = SongsManager.getInstance().getRootDir();

        if (currentDir == null) {
            currentDir = FoldersManager.getInstance().getRootDir();
        }

        if (currentDir != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //                slideUpListExplorer();
                    getDir(currentDir, null);
                }
            }, 0);
        } else {
            //TODO Write on the background the there is no media
//            Toast.makeText(context, "There is no music", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void initRecyclerListeners() {
        recyclerExplorer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    if (event.getAction() != KeyEvent.ACTION_DOWN)
                        return true;
                    if (!goBack())
                        getActivity().onBackPressed();
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context = null;
        recyclerExplorer = null;
    }


    private void slideUpListExplorer() {
        getDir(rootDir, null);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);

        animation.setDuration(600);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                recyclerExplorer.setVisibility(View.VISIBLE);

            }

        });

        recyclerExplorer.setAnimation(animation);
    }


    /**
     * Retrieves the folder hierarchy for the specified folder.
     *
     * @param dirPath      The path of the new folder.
     * @param restoreState The state of the ListView that should be restored. Pass
     *                     null if the ListView's position should not be restored.
     */
    private void getDir(String dirPath, Parcelable restoreState) {
        foldersAndMusic = FoldersManager.getInstance().getFoldersAndMusic(dirPath);

        adapter = new FolderAdapter(getActivity(), this, foldersAndMusic, setOnItemClickListener(), getCurrentSong());

        recyclerExplorer.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (restoreState != null) {
            recyclerExplorer.getLayoutManager().onRestoreInstanceState(restoreState);
        } else if (mFolderStateMap.containsKey(dirPath)) {
            recyclerExplorer.getLayoutManager().onRestoreInstanceState(mFolderStateMap.get(dirPath));
        }
    }

    private Song getCurrentSong() {
        return ((MainActivity) getActivity()).getMusicSrv().getCurrSong();
    }

    private View.OnClickListener setOnItemClickListener() {
        View.OnClickListener mItemClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = recyclerExplorer.getChildAdapterPosition(v);
                if (index == 0 && !currentDir.equals(rootDir)) {
                    goBack();
                    return;
                }
                //Store the current folder's state in the HashMap.
                if (mFolderStateMap.size() == 3) {
                    mFolderStateMap.clear();
                }

                mFolderStateMap.put(currentDir, recyclerExplorer.getLayoutManager().onSaveInstanceState());

                //Check if the selected item is a folder or a file.
                if (foldersAndMusic.get(index).isDirectory()) {
                    currentDir = foldersAndMusic.get(index).getAbsolutePath();
                    getDir(currentDir, null);
                } else {
                    List<Song> songs = getSongs(foldersAndMusic);
                    int songPos = songs.indexOf(SongsManager.getInstance().getSong(foldersAndMusic.get(index).getPath()));

                    findSelectedAndClear(getCurrentSong());
                    ((MainActivity) getActivity()).playSong(songs, songPos);
                    setSelected(v);
                }
            }

            private void findSelectedAndClear(Song currSong) {
                String songPath = currSong.getPath();
                //if we are in folder with current song
                if (songPath.substring(0, songPath.lastIndexOf("/")).equals(currentDir)) {
                        adapter.removeSelection(currSong);
                } else {
                    for (int i = 0; i < foldersAndMusic.size(); i++) {
                        if (foldersAndMusic.get(i).isDirectory() &&
                                songPath.contains(foldersAndMusic.get(i).getPath())) {
                            removeSelection(i);
                            break;
                        }
                    }
                }
            }

            private void setSelected(View v) {
                TextView title = (TextView) v.findViewById(R.id.aa_title);
                TextView subtitle = (TextView) v.findViewById(R.id.subtitle);
                int color = ContextCompat.getColor(getActivity().getApplicationContext(),
                        R.color.colorComplementary);
                title.setTextColor(color);
                subtitle.setTextColor(color);
            }

            private List<Song> getSongs(List<File> foldersAndMusic) {
                List<Song> songs = new ArrayList<>();
                for (File file : foldersAndMusic) {
                    if (!file.isDirectory()) {
                        Song song = SongsManager.getInstance().getSong(file.getPath());
                        songs.add(song);
                    }
                }
                return songs;
            }
        };
        return mItemClick;
    }

    public boolean goBack() {
        if (currentDir.equals(FoldersManager.getInstance().getRootDir()))
            return false;
        currentDir = currentDir.substring(0, currentDir.lastIndexOf('/'));
        getDir(currentDir, mFolderStateMap == null ? null : mFolderStateMap.get(currentDir));
        return true;
    }

    public void refreshListView() {
        //Update the ListView.
        getDir(currentDir, recyclerExplorer.getLayoutManager().onSaveInstanceState());
    }

    @SuppressLint("SdCardPath")
    private String getRealFilePath(String filePath) {
        if (filePath.equals("/storage/emulated/0") ||
                filePath.equals("/storage/emulated/0/") ||
                filePath.equals("/storage/emulated/legacy") ||
                filePath.equals("/storage/emulated/legacy/") ||
                filePath.equals("/storage/sdcard0") ||
                filePath.equals("/storage/sdcard0/") ||
                filePath.equals("/sdcard") ||
                filePath.equals("/sdcard/") ||
                filePath.equals("/mnt/sdcard") ||
                filePath.equals("/mnt/sdcard/")) {

            return Environment.getExternalStorageDirectory().toString();
        }

        return filePath;
    }


    public void setContext(Context context) {
        this.context = context;
    }


    public void removeSelection(int pos) {
        View view = manager.findViewByPosition(pos);
        TextView title = (TextView) view.findViewById(R.id.aa_title);
        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
        title.setTextColor(Color.WHITE);
        subtitle.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
    }
}
