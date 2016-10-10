package com.shawasama.playsuit.fragment;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shawasama.playsuit.Constants;
import com.shawasama.playsuit.R;
import com.shawasama.playsuit.adapter.FolderAdapter;
import com.shawasama.playsuit.pojo.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

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
        //TODO check this methods
        listExplorer.setFastScrollEnabled(true);
//        listExplorer.setVisibility(View.INVISIBLE);

//        rootDir = Environment.getExternalStorageDirectory().getPath();
        rootDir = "/storage/";
        if (currentDir == null) {
            currentDir = rootDir;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                slideUpListExplorer();
                getDir(currentDir, null);
            }
        }, 0);

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
        //TODO load only directories with music inside it or its subdirectories
        fileFolderNameList = new ArrayList<>();
        fileFolderPathList = new ArrayList<>();
        fileFolderTypeList = new ArrayList<>();

        File f = new File(dirPath);

        List<File> files = new ArrayList<>();
        File emptyFile = new File("..");
        files.add(emptyFile);
        files.addAll(Arrays.asList(f.listFiles()));


        if (!files.isEmpty()) {
            //Sort the files by name.
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return f1.getName().compareTo(f2.getName());
                }
            });

            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    int typeF1 = f1.isDirectory() ? Constants.FOLDER : Constants.AUDIO;
                    int typeF2 = f2.isDirectory() ? Constants.FOLDER : Constants.AUDIO;
                    return typeF1 - typeF2;
                }
            });


            for (File file : files) {

                if (file == emptyFile) {
                    continue;
                }

                if (!file.isHidden() && file.canRead()) {

                    if (file.isDirectory()) {
                        /*
                         * Starting with Android 4.2, /storage/emulated/legacy/...
						 * is a symlink that points to the actual directory where
						 * the user's files are stored. We need to detect the
						 * actual directory's file path here.
						 */
                        String filePath;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                            filePath = getRealFilePath(file.getAbsolutePath());
                        else
                            filePath = file.getAbsolutePath();

                        fileFolderPathList.add(filePath);
                        fileFolderNameList.add(file.getName());
                        fileFolderTypeList.add(Constants.FOLDER);

                    } else if (Util.isAudio(file)) {

                        fileFolderNameList.add(file.getName());
                        fileFolderTypeList.add(Constants.AUDIO);
                        try {
                            String path = file.getCanonicalPath();
                            fileFolderPathList.add(path);
                        } catch (IOException ignored) {
                        }

                    }

                }

            }
        }

        FolderAdapter folderAdapter = new FolderAdapter(
                getActivity(),
                this,
                fileFolderNameList,
                fileFolderPathList,
                fileFolderTypeList,
                files);

        listExplorer.setAdapter(folderAdapter);
        folderAdapter.notifyDataSetChanged();

        if (restoreState != null) {
            listExplorer.onRestoreInstanceState(restoreState);
        } else if (mFolderStateMap.containsKey(dirPath)) {
            listExplorer.onRestoreInstanceState(mFolderStateMap.get(dirPath));
        }

        listExplorer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if (index == 0) {
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
//                if ((Integer) view.getTag(R.string.folder_list_item_type)==FOLDER)
//                    currentDir = newPath;

                //Check if the selected item is a folder or a file.
                if (fileFolderTypeList.get(index) == Constants.FOLDER) {
                    currentDir = newPath;
                    getDir(newPath, null);
                } else {
                    int fileIndex = 0;
                    for (int i = 0; i < index; i++) {
                        if (fileFolderTypeList.get(i) == Constants.AUDIO)
                            fileIndex++;
                    }

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
