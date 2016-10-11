package com.shawasama.playsuit.fragment;

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

import com.shawasama.playsuit.Constants;
import com.shawasama.playsuit.R;
import com.shawasama.playsuit.adapter.FolderAdapter;
import com.shawasama.playsuit.pojo.AudioContainer;
import com.shawasama.playsuit.pojo.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class FoldersFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.listview_layout;
    private Application mApp;
    private ListView listExplorer;

    //Folder parameter
    private String rootDir;
    private static String currentDir;
    private ArrayList<String> fileFolderNameList;
    private ArrayList<String> fileFolderPathList;
    private ArrayList<Integer> fileFolderTypeList;

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
        rootDir = AudioContainer.getInstance().getRootDir();

        if (currentDir == null) {
            currentDir = rootDir;
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
        fileFolderNameList = new ArrayList<>();
        fileFolderPathList = new ArrayList<>();
        fileFolderTypeList = new ArrayList<>();

        File f = new File(dirPath);
        List<File> files = Arrays.asList(f.listFiles());
        List<File> foldersAndMusic = new ArrayList<>();
        File emptyFile;

        if (!currentDir.equals(rootDir)) {
            //add empty file so we could go back by clicking its view
            emptyFile = new File("..");
            foldersAndMusic.add(emptyFile);
            fileFolderTypeList.add(0);
            fileFolderNameList.add("..");
            fileFolderPathList.add("..");
        }

        if (!files.isEmpty()) {
            //Sort the files by name.
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return f1.getName().compareTo(f2.getName());
                }
            });

            //Sort the files by type(folder or music)
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    int typeF1 = f1.isDirectory() ? Constants.FOLDER : Constants.AUDIO;
                    int typeF2 = f2.isDirectory() ? Constants.FOLDER : Constants.AUDIO;
                    return typeF1 - typeF2;
                }
            });

            for (File file : files) {
                if (file != null && !file.isHidden() && file.canRead()) {

                    if (file.isDirectory()) {

                        String filePath = file.getAbsolutePath();
                        if (AudioContainer.getInstance().isStoresMusic(filePath)) {
                            fileFolderPathList.add(filePath);
                            fileFolderNameList.add(file.getName());
                            fileFolderTypeList.add(Constants.FOLDER);
                            foldersAndMusic.add(file);
                        }

                    } else if (Util.isAudio(file)) {

                        fileFolderNameList.add(file.getName());
                        fileFolderTypeList.add(Constants.AUDIO);
                        try {
                            String path = file.getCanonicalPath();
                            fileFolderPathList.add(path);
                        } catch (IOException ignored) {
                        }
                        foldersAndMusic.add(file);
                    }

                }

            }
        }

        FolderAdapter folderAdapter = new FolderAdapter(
                getActivity(),
                this,
                fileFolderNameList,
                fileFolderTypeList,
                foldersAndMusic);

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
                    currentDir = currentDir.substring(0, currentDir.lastIndexOf('/'));
                    getDir(currentDir, mFolderStateMap.get(currentDir));
                    return;
                }
                //Store the current folder's state in the HashMap.
                if (mFolderStateMap.size() == 3) {
                    mFolderStateMap.clear();
                }

                mFolderStateMap.put(currentDir, listExplorer.onSaveInstanceState());
                String newPath = fileFolderPathList.get(index);

                //Check if the selected item is a folder or a file.
                if (fileFolderTypeList.get(index) == Constants.FOLDER) {
                    currentDir = newPath;
                    getDir(newPath, null);
                } else {
                    //TODO play
//                    play(fileFolderTypeList.get(index), fileIndex, currentDir);
                }
            }
        });
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
