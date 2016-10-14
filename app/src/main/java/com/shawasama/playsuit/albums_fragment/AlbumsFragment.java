package com.shawasama.playsuit.albums_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.activity.MainActivity;
import com.shawasama.playsuit.asynctask.AsyncLoadAllAlbumsTask;
import com.shawasama.playsuit.fragment.AbstractTabFragment;
import com.shawasama.playsuit.media_class.Album;
import com.shawasama.playsuit.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlbumsFragment extends AbstractTabFragment {
    private static final int LAYOUT = R.layout.recyclerview_layout;

    private RecyclerView albumsView;

    public static AlbumsFragment getInstance(Context context) {
        Bundle args = new Bundle();
        AlbumsFragment fragment = new AlbumsFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.navigation_menu_and_tab_item_albums));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        albumsView = (RecyclerView) view.findViewById(R.id.recycler_view);
        context = getActivity().getApplicationContext();

        GridLayoutManager manager = new GridLayoutManager(context, 2);
        albumsView.setLayoutManager(manager);
        try {
            AsyncLoadAllAlbumsTask asyncTask = (AsyncLoadAllAlbumsTask) ((MainActivity) getActivity()).getAsyncTask(Constants.ASYNC_ALBUMS);
            AlbumListAdapter adapter = new AlbumListAdapter(context, asyncTask.get(), this);
            albumsView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context = null;
        albumsView = null;
    }

    private List<Album> onCreateData() {
        List<Album> albumList = new ArrayList<>();
        albumList.add(new Album(1, "Album1", "Artist", 1, null));
        albumList.add(new Album(2, "Album2", "Artist", 2, null));
        albumList.add(new Album(3, "Album3", "Artist", 3, null));
        albumList.add(new Album(4, "Album4", "Artist", 4, null));
        albumList.add(new Album(5, "Album5", "Artist", 5, null));
        albumList.add(new Album(6, "Album6", "Artist", 6, null));
        albumList.add(new Album(7, "Album7", "Artist", 7, null));
        albumList.add(new Album(8, "Album8", "Artist", 8, null));
        return albumList;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
