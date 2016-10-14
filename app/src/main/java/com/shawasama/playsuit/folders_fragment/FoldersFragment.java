package com.shawasama.playsuit.folders_fragment;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.fragment.AbstractTabFragment;
import com.shawasama.playsuit.songs_fragment.SongsManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class FoldersFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.listview_layout;
    private Application mApp;
    private ListView listExplorer;

    //Folder parameter
    private String rootDir;
    private static String currentDir;
    private List<File> currDirList;

    //HashMap to store the each folder's previous scroll/position state.
    private HashMap<String, Parcelable> mFolderStateMap;


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
        context = getActivity().getApplicationContext();
        mApp = (Application) context;
        mFolderStateMap = new HashMap<>();

        listExplorer = (ListView) view.findViewById(R.id.listview);
        listExplorer.setFastScrollEnabled(true);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context = null;
        listExplorer = null;
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
                listExplorer.setVisibility(View.VISIBLE);

            }

        });

        listExplorer.setAnimation(animation);
    }


    /**
     * Retrieves the folder hierarchy for the specified folder.
     *
     * @param dirPath      The path of the new folder.
     * @param restoreState The state of the ListView that should be restored. Pass
     *                     null if the ListView's position should not be restored.
     */
    private void getDir(String dirPath, Parcelable restoreState) {
        FoldersManager.FileListsContainer fileListsContainer = FoldersManager.getInstance().getFileListContainer(dirPath);
        currDirList = fileListsContainer.getFoldersAndMusic();

        FolderAdapter folderAdapter = new FolderAdapter(
                getActivity(),
                this,
                fileListsContainer.getFileFolderNameList(),
                currDirList);

        listExplorer.setAdapter(folderAdapter);
        folderAdapter.notifyDataSetChanged();

        if (restoreState != null) {
            listExplorer.onRestoreInstanceState(restoreState);
        } else if (mFolderStateMap.containsKey(dirPath)) {
            listExplorer.onRestoreInstanceState(mFolderStateMap.get(dirPath));
        }

        initOnItemClickListener();
    }

    private void initOnItemClickListener() {
        listExplorer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if (index == 0 && !currentDir.equals(rootDir)) {
                    goBack();
                    return;
                }
                //Store the current folder's state in the HashMap.
                if (mFolderStateMap.size() == 3) {
                    mFolderStateMap.clear();
                }

                mFolderStateMap.put(currentDir, listExplorer.onSaveInstanceState());
                String newPath = currDirList.get(index).getAbsolutePath();

                //Check if the selected item is a folder or a file.
                if (currDirList.get(index).isDirectory()) {
                    currentDir = newPath;
                    getDir(newPath, null);
                } else {
                    //TODO play
//                    play(fileFolderTypeList.get(index), fileIndex, currentDir);
                }
            }
        });
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
        getDir(currentDir, listExplorer.onSaveInstanceState());
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
}
